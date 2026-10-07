package org.example.ui.support;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.common.config.Config;
import org.example.common.models.Goods;

import java.util.List;

import static io.restassured.RestAssured.given;

//Подготовка данных для UI-тестов.

public class UiGoodsFixture {

    private static final Config config = Config.getInstance();

    private final RequestSpecification spec = given()
            .baseUri(config.getBaseUrl())
            .contentType(ContentType.JSON)
            .auth().basic(config.getAdminLogin(), config.getAdminPassword());


    @Step("получить товары витрины (GET /goods/list)")
    public List<Goods> getShelfGoods() {
        long deadline = System.currentTimeMillis() + config.getGoodsWaitTimeoutMs();
        Response response = getShelfGoodsResponse();

        while (response.statusCode() != 200 && System.currentTimeMillis() < deadline) {
            sleep(config.getApiRetryIntervalMs());
            response = getShelfGoodsResponse();
        }

        if (response.statusCode() != 200) {
            throw new AssertionError("Не удалось получить список товаров витрины: " + response.statusCode());
        }
        return response.jsonPath().getList("goods", Goods.class);
    }

    @Step("товар витрины с id={id}")
    public Goods getShelfGoods(long id) {
        return getShelfGoods().stream()
                .filter(goods -> goods.getId() == id)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Товара с id=" + id + " нет на витрине"));
    }

    @Step("товар витрины по индексу {index}")
    public Goods getShelfGoodsAt(int index) {
        List<Goods> shelfGoods = getShelfGoods();
        if (index >= shelfGoods.size()) {
            throw new AssertionError("На витрине всего " + shelfGoods.size() + " товаров, нужен индекс " + index);
        }
        return shelfGoods.get(index);
    }

    @Step("максимальный id товара на витрине")
    public int getMaxShelfGoodsId() {
        return getShelfGoods().stream()
                .mapToInt(Goods::getId)
                .max()
                .orElseThrow(() -> new AssertionError("Витрина пуста"));
    }


    @Step("удалить товар, созданный тестом после id={maxIdBefore}")
    public boolean deleteGoodsCreatedAfter(int maxIdBefore) {
        int lastId = maxIdBefore + config.getCleanupIdProbeStep();

        for (int id = maxIdBefore + 1; id <= lastId; id++) {
            Response response = spec
                    .when()
                    .delete(config.getGoodByIdPath().replace("{id}", String.valueOf(id)))
                    .andReturn();
            if (response.statusCode() == 200) {
                return true;
            }
        }
        return false;
    }

    @Step("восстановить товар id={id} (имя '{name}', цена {price}) через PATCH")
    public void restoreGoods(Goods goods) {
        spec
                .body(goods)
                .when()
                .patch(config.getGoodByIdPath().replace("{id}", String.valueOf(goods.getId())))
                .andReturn();
    }

    private Response getShelfGoodsResponse() {
        return spec
                .when()
                .get(config.getGoodsListPath()
                        + "?page=" + config.getGoodsListPage()
                        + "&size=" + config.getGoodsListSize())
                .andReturn();
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
