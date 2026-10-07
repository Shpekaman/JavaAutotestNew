package org.example.ui.asserts;

import io.qameta.allure.Step;
import org.example.common.config.Config;
import org.example.ui.page.MainPage;

import static org.assertj.core.api.Assertions.assertThat;

//проверка главной страницы
public class MainPageAssert {

    private final MainPage mainPage;

    public MainPageAssert(MainPage mainPage) {
        this.mainPage = mainPage;
    }

    @Step("UI-проверка: общая сумма в корзине равна {expected}")
    public MainPageAssert totalPrice(double expected) {
        double delta = Config.getInstance().getPriceDelta();
        assertThat(mainPage.getTotalPrice())
                .as("Общая сумма в корзине (допуск " + delta + ")")
                .isCloseTo(expected, org.assertj.core.data.Offset.offset(delta));
        return this;
    }

    @Step("UI-проверка: товар '{name}' есть в корзине")
    public MainPageAssert productInCart(String name) {
        assertThat(mainPage.isProductInCart(name))
                .as("Товар '" + name + "' должен быть в корзине")
                .isTrue();
        return this;
    }

    @Step("UI-проверка: товар '{name}' отображается на витрине")
    public MainPageAssert productOnShelf(String name) {
        assertThat(mainPage.isProductOnShelf(name))
                .as("Товар '" + name + "' должен отображаться на витрине")
                .isTrue();
        return this;
    }

    @Step("UI-проверка: уведомление видно и содержит '{expectedText}'")
    public MainPageAssert toastVisibleWithText(String expectedText) {
        assertThat(mainPage.isToastWithTextVisible(expectedText))
                .as("Должно появиться уведомление с текстом: " + expectedText)
                .isTrue();
        return this;
    }

    @Step("UI-проверка: админ-форма добавления товара отображается")
    public MainPageAssert adminFormVisible() {
        assertThat(mainPage.isAdminFormVisible())
                .as("После входа админ-форма должна отображаться на главной странице")
                .isTrue();
        return this;
    }
}
