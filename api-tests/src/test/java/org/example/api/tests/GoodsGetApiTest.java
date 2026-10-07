package org.example.api.tests;

import io.qameta.allure.Step;
import org.example.common.models.Goods;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

//Тесты эндпоинта GET /goods/{id}.

@Tag("apiTest")
@DisplayName("API: GET /goods/{id}")
class GoodsGetApiTest extends ApiTestBase {

    @Test
    @Tag("smoke")
    @Step("API: получить существующий товар по id — 200 и его поля")
    void getExistingGoodsByIdReturns200() {
        long id = config.getExistingGoodId();

        var response = goodsApi.getGoodById(id);

        apiAssert.statusCode(response, 200);
        apiAssert.fieldPresent(response, "id");
        apiAssert.fieldPresent(response, "name");
        apiAssert.fieldPresent(response, "price");
        apiAssert.goodsListContainsId(goodsApi.getGoodsList(), (int) id);
    }

    @Test
    @Step("API: получить несуществующий товар по id — 404")
    void getNotExistingGoodsByIdReturns404() {
        apiAssert.statusCode(goodsApi.getGoodById(config.getNotExistingGoodId()), 404);
    }

    @Test
    @Step("API: получить товар по некорректному id — 400")
    void getGoodsByInvalidIdReturns400() {
        apiAssert.statusCode(goodsApi.getGoodById("abc"), 400);
    }

    @Test
    @Step("API: известный дефект стенда — GET /goods/{id} созданного товара возвращает 500")
    void getCreatedGoodsByIdReturnsServerErrorOnStand() {
        Goods goods = createGoods();

        var response = goodsApi.getGoodById(goods.getId());

        apiAssert.statusCode(response, 500);
        apiAssert.bodyContains(response, config.getServerErrorMessage());
    }
}
