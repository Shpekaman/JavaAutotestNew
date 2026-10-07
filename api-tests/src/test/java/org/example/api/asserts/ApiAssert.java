package org.example.api.asserts;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.common.models.Goods;

import static org.assertj.core.api.Assertions.assertThat;

//Класс проверок для API-автотестов
public class ApiAssert {

    @Step("API-проверка: код ответа {expected}")
    public void statusCode(Response response, int expected) {
        assertThat(response.statusCode())
                .as("Ожидаемый код ответа: " + expected + ", фактический: " + response.statusCode()
                        + ", тело ответа: " + response.asString())
                .isEqualTo(expected);
    }

    @Step("API-проверка: id созданного товара возвращается в ответе")
    public int createdGoodsId(Response response) {
        int id = response.jsonPath().getInt("data.id");
        assertThat(id)
                .as("POST /goods/add должен вернуть id созданного товара в поле data.id")
                .isPositive();
        return id;
    }

    @Step("API-проверка: список товаров не пуст")
    public void goodsListNotEmpty(Response response) {
        assertThat(response.jsonPath().getList("goods"))
                .as("GET /goods/list должен возвращать непустой список товаров")
                .isNotEmpty();
    }

    @Step("API-проверка: товар по id={id} содержит те же имя и цену")
    public void goodFieldsMatch(Response response, Goods goods) {
        assertThat(response.jsonPath().getInt("id")).as("id товара").isEqualTo(goods.getId());
        assertThat(response.jsonPath().getString("name")).as("имя товара").isEqualTo(goods.getName());
        assertThat(response.jsonPath().getFloat("price")).as("цена товара").isEqualTo(goods.getPrice());
    }

    @Step("API-проверка: в ответе есть поле '{field}'")
    public void fieldPresent(Response response, String field) {
        assertThat((Object) response.jsonPath().get(field))
                .as("В ответе должно быть поле: " + field + ", тело ответа: " + response.asString())
                .isNotNull();
    }

    @Step("API-проверка: текст ответа содержит '{text}'")
    public void bodyContains(Response response, String text) {
        assertThat(response.asString())
                .as("Текст ответа должен содержать: " + text + ", фактический: " + response.asString())
                .contains(text);
    }

    @Step("API-проверка: редирект ведёт на '{expectedLocation}'")
    public void redirectTo(Response response, String expectedLocation) {
        assertThat(response.header("Location"))
                .as("Ожидаемый редирект: " + expectedLocation + ", фактический: " + response.header("Location"))
                .isEqualTo(expectedLocation);
    }

    @Step("API-проверка: редирект содержит '{text}'")
    public void redirectContains(Response response, String text) {
        assertThat(response.header("Location"))
                .as("Редирект должен содержать: " + text + ", фактический: " + response.header("Location"))
                .contains(text);
    }

    @Step("API-проверка: в ответе установлена cookie сессии")
    public void sessionCookieIsSet(Response response) {
        assertThat(response.getCookie("JSESSIONID"))
                .as("После входа должна устанавливаться cookie JSESSIONID")
                .isNotNull()
                .isNotBlank();
    }

    @Step("API-проверка: в списке товаров есть товар с id={id}")
    public void goodsListContainsId(Response response, int id) {
        assertThat(response.jsonPath().getList("goods", Goods.class))
                .as("В GET /goods/list должен быть товар с id=" + id)
                .anySatisfy(goods -> assertThat(goods.getId()).isEqualTo(id));
    }
}
