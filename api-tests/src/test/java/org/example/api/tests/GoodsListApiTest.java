package org.example.api.tests;

import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

//Тесты эндпоинта GET /goods/list.

@Tag("apiTest")
@DisplayName("API: GET /goods/list")
class GoodsListApiTest extends ApiTestBase {

    @Test
    @Tag("smoke")
    @Step("API: список товаров — 200 и непустой")
    void getGoodsListReturns200AndNotEmpty() {
        var response = goodsApi.getGoodsListWithRetry();

        apiAssert.statusCode(response, 200);
        apiAssert.goodsListNotEmpty(response);
    }

    @Test
    @Step("API: в списке есть товар, существующий на стенде до прогона")
    void getGoodsListContainsExistingGoods() {
        apiAssert.goodsListContainsId(goodsApi.getGoodsListWithRetry(), (int) config.getExistingGoodId());
    }

    @Test
    @Step("API: список товаров возвращается в виде массива goods")
    void getGoodsListReturnsGoodsArray() {
        var response = goodsApi.getGoodsListWithRetry();

        apiAssert.statusCode(response, 200);
        apiAssert.fieldPresent(response, "goods");
    }
}
