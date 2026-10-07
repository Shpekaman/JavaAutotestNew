package org.example.api.tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.common.models.Goods;
import org.example.common.utils.UniqueData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

//Тесты эндпоинта POST /goods/add.

@Tag("apiTest")
@DisplayName("API: POST /goods/add")
class GoodsAddApiTest extends ApiTestBase {

    @Test
    @Tag("smoke")
    @Step("API: добавление товара — 200, возвращается id и сообщение об успехе")
    void addGoodsReturnsIdAndSuccessMessage() {
        Goods goods = new Goods(UniqueData.goodsName(config.getGoodsNamePrefix()), config.getGoodsPrice());

        Response response = goodsApi.addGoods(goods);

        apiAssert.statusCode(response, 200);
        int id = apiAssert.createdGoodsId(response);
        goods.setId(id);
        registerCreatedGoods(id);
        apiAssert.bodyContains(response, config.getAddGoodsSuccessMessage());
    }

    @Test
    @Step("API: добавленный товар сразу доступен для изменения через PATCH")
    void addedGoodsCanBePatched() {
        Goods goods = createGoods();

        Response patched = goodsApi.patchGoodById(goods.getId(), goods);

        apiAssert.statusCode(patched, 200);
        apiAssert.goodFieldsMatch(patched, goods);
    }

    @Test
    @Step("API: добавление товара с пустым телом — 400")
    void addGoodsWithEmptyBodyReturns400() {
        Response response = goodsApi.addGoodsRaw("{}");

        apiAssert.statusCode(response, 400);
        apiAssert.bodyContains(response, config.getGoodsNameEmptyErrorMessage());
    }

    @Test
    @Step("API: добавление товара без обязательного поля name — 400")
    void addGoodsWithoutNameReturns400() {
        Response response = goodsApi.addGoodsRaw("{\"price\":" + config.getGoodsPrice() + "}");

        apiAssert.statusCode(response, 400);
    }
}
