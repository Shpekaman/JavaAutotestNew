package org.example.api.basic;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.api.RestApiBuilder;
import org.example.common.config.Config;
import org.example.common.models.Goods;

//класс для эндпоинтов товаров: POST /goods/add, GET /goods/list, GET /goods/{id}, PATCH /goods/{id}, DELETE /goods/{id}.

public class GoodsApi {

    private static final Config config = Config.getInstance();

    private final RequestSpecification spec;

    public GoodsApi() {
        this(RestApiBuilder.authorized().build());
    }

    public GoodsApi(RequestSpecification spec) {
        this.spec = spec;
    }

    @Step("API: создать товар '{name}' с ценой {price} через POST /goods/add")
    public Response addGoods(Goods goods) {
        return spec
                .body(goods)
                .when()
                .post(config.getGoodsAddPath())
                .andReturn();
    }

    @Step("API: создать товар с произвольным телом через POST /goods/add")
    public Response addGoodsRaw(String body) {
        return spec
                .body(body)
                .when()
                .post(config.getGoodsAddPath())
                .andReturn();
    }

    @Step("API: получить список товаров через GET /goods/list")
    public Response getGoodsList() {
        return spec
                .when()
                .get(config.getGoodsListPath() + "?size=" + config.getGoodsListSize())
                .andReturn();
    }

    @Step("API: получить товар по id={id} через GET /goods/{id}")
    public Response getGoodById(Object id) {
        return spec
                .when()
                .get(config.getGoodByIdPath().replace("{id}", String.valueOf(id)))
                .andReturn();
    }

    @Step("API: изменить товар id={id} произвольным телом через PATCH /goods/{id}")
    public Response patchGoodByIdRaw(Object id, Object body) {
        return spec
                .body(body)
                .when()
                .patch(config.getGoodByIdPath().replace("{id}", String.valueOf(id)))
                .andReturn();
    }

    @Step("API: изменить товар '{goods}' через PATCH /goods/{id}")
    public Response patchGoodById(Object id, Goods goods) {
        return patchGoodByIdRaw(id, goods);
    }

    @Step("API: удалить товар id={id} через DELETE /goods/{id}")
    public Response deleteGoodById(Object id) {
        return spec
                .when()
                .delete(config.getGoodByIdPath().replace("{id}", String.valueOf(id)))
                .andReturn();
    }

    /**
     * Тестовый стенд нестабилен: сразу после создания товаров GET /goods/list может отдать 500,
     * поэтому список читается с повтором до успешного ответа.
     */
    @Step("API: получить список товаров через GET /goods/list (с повтором при 500)")
    public Response getGoodsListWithRetry() {
        long deadline = System.currentTimeMillis() + config.getGoodsWaitTimeoutMs();
        Response response = getGoodsList();

        while (response.statusCode() != 200 && System.currentTimeMillis() < deadline) {
            sleep(config.getApiRetryIntervalMs());
            response = getGoodsList();
        }
        return response;
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
