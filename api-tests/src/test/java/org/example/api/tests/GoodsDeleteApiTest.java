package org.example.api.tests;

import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

//Тесты эндпоинта DELETE /goods/{id}.

@Tag("apiTest")
@DisplayName("API: DELETE /goods/{id}")
class GoodsDeleteApiTest extends ApiTestBase {

    @Test
    @Tag("smoke")
    @Step("API: удаление товара — 200 и сообщение об успешном удалении")
    void deleteGoodsReturns200() {
        var goods = createGoods();

        var response = goodsApi.deleteGoodById(goods.getId());

        apiAssert.statusCode(response, 200);
        apiAssert.bodyContains(response, config.getDeleteSuccessMessage());
    }

    @Test
    @Step("API: после удаления товар больше не обновляется — PATCH возвращает 404")
    void deletedGoodsCannotBePatched() {
        var goods = createGoods();
        goodsApi.deleteGoodById(goods.getId());

        apiAssert.statusCode(goodsApi.patchGoodByIdRaw(goods.getId(), "{\"name\":\"after-delete\"}"), 404);
    }

    @Test
    @Step("API: повторное удаление товара — 404")
    void deleteAlreadyDeletedGoodsReturns404() {
        var goods = createGoods();
        goodsApi.deleteGoodById(goods.getId());

        apiAssert.statusCode(goodsApi.deleteGoodById(goods.getId()), 404);
    }

    @Test
    @Step("API: удаление несуществующего товара — 404")
    void deleteNotExistingGoodsReturns404() {
        apiAssert.statusCode(goodsApi.deleteGoodById(config.getNotExistingGoodId()), 404);
    }

    @Test
    @Step("API: удаление по некорректному id — 400")
    void deleteGoodsByInvalidIdReturns400() {
        apiAssert.statusCode(goodsApi.deleteGoodById("abc"), 400);
    }
}
