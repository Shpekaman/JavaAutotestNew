package org.example.api.tests;

import java.util.ArrayList;
import java.util.List;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.api.AllureApiSetup;
import org.example.api.asserts.ApiAssert;
import org.example.api.basic.GoodsApi;
import org.example.common.config.Config;
import org.example.common.models.Goods;
import org.example.common.utils.UniqueData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

//Базовый класс API-автотестов
@ExtendWith(AllureApiSetup.class)
public abstract class ApiTestBase {

    protected static final Config config = Config.getInstance();

    protected final GoodsApi goodsApi = new GoodsApi();
    protected final ApiAssert apiAssert = new ApiAssert();

    private final List<Integer> createdGoodsIds = new ArrayList<>();

    @Step("создать товар '{name}' с ценой {price} и запомнить его для очистки")
    protected Goods createGoods(String name, float price) {
        Response response = goodsApi.addGoods(new Goods(name, price));
        Goods goods = new Goods(name, price);
        goods.setId(apiAssert.createdGoodsId(response));
        createdGoodsIds.add(goods.getId());
        return goods;
    }

    @Step("создать товар с уникальным именем из конфига")
    protected Goods createGoods() {
        return createGoods(UniqueData.goodsName(config.getGoodsNamePrefix()), config.getGoodsPrice());
    }

    @Step("запомнить товар для очистки")
    protected void registerCreatedGoods(int id) {
        createdGoodsIds.add(id);
    }

    @AfterEach
    @Step("удалить все товары, созданные тестом")
    void deleteCreatedGoods() {
        for (Integer id : createdGoodsIds) {
            goodsApi.deleteGoodById(id);
        }
        createdGoodsIds.clear();
    }
}
