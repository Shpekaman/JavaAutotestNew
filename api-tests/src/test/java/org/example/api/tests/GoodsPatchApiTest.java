package org.example.api.tests;

import io.qameta.allure.Step;
import org.example.common.models.Goods;
import org.example.common.utils.UniqueData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

//Тесты эндпоинта PATCH /goods/{id}.

@Tag("apiTest")
@DisplayName("API: PATCH /goods/{id}")
class GoodsPatchApiTest extends ApiTestBase {

    @Test
    @Tag("smoke")
    @Step("API: изменить имя и цену товара — 200 и поля обновились")
    void patchGoodsNameAndPriceReturns200() {
        Goods created = createGoods();
        Goods updated = new Goods(UniqueData.goodsName(config.getGoodsNamePrefix()), config.getGoodsPriceAdmin());

        var response = goodsApi.patchGoodById(created.getId(), updated);

        apiAssert.statusCode(response, 200);
        assertThat(response.jsonPath().getInt("id"))
                .as("id товара не должен измениться")
                .isEqualTo(created.getId());
        assertThat(response.jsonPath().getString("name"))
                .as("имя товара должно обновиться")
                .isEqualTo(updated.getName());
        assertThat(response.jsonPath().getFloat("price"))
                .as("цена товара должна обновиться")
                .isEqualTo(updated.getPrice());
    }

    @Test
    @Step("API: изменить только имя товара — 200 и цена сохранилась")
    void patchOnlyGoodsNameKeepsPrice() {
        Goods created = createGoods();
        String newName = UniqueData.goodsName(config.getGoodsNamePrefix());

        var response = goodsApi.patchGoodByIdRaw(created.getId(),
                "{\"name\":\"" + newName + "\"}");

        apiAssert.statusCode(response, 200);
        assertThat(response.jsonPath().getString("name"))
                .as("имя товара должно обновиться")
                .isEqualTo(newName);
        assertThat(response.jsonPath().getFloat("price"))
                .as("цена товара должна остаться прежней")
                .isEqualTo(created.getPrice());
    }

    @Test
    @Step("API: изменить несуществующий товар — 404")
    void patchNotExistingGoodsReturns404() {
        var response = goodsApi.patchGoodByIdRaw(Integer.MAX_VALUE,
                "{\"name\":\"" + UniqueData.goodsName(config.getGoodsNamePrefix()) + "\"}");

        apiAssert.statusCode(response, 404);
    }

    @Test
    @Step("API: изменить товар с некорректной ценой — 400")
    void patchGoodsWithInvalidPriceReturns400() {
        Goods created = createGoods();

        var response = goodsApi.patchGoodByIdRaw(created.getId(), "{\"price\":\"not-a-number\"}");

        apiAssert.statusCode(response, 400);
    }

    @Test
    @Step("API: изменить товар с пустым именем — 400")
    void patchGoodsWithEmptyNameReturns400() {
        Goods created = createGoods();

        var response = goodsApi.patchGoodByIdRaw(created.getId(), "{\"name\":\"\"}");

        apiAssert.statusCode(response, 400);
    }
}
