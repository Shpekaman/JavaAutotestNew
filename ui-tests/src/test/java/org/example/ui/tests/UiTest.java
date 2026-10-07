package org.example.ui.tests;

import io.qameta.allure.Step;
import org.example.common.config.Config;
import org.example.common.models.Goods;
import org.example.common.utils.UniqueData;
import org.example.ui.AllureUiSetup;
import org.example.ui.asserts.LoginPageAssert;
import org.example.ui.asserts.MainPageAssert;
import org.example.ui.page.LoginPage;
import org.example.ui.page.MainPage;
import org.example.ui.support.UiGoodsFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;


@Tag("uiTest")
@ExtendWith(AllureUiSetup.class)
@DisplayName("UI: кейсы")
class UiTest {

    private static final Config config = Config.getInstance();

    private final LoginPage loginPage = new LoginPage();
    private final MainPage mainPage = new MainPage();
    private final UiGoodsFixture goodsFixture = new UiGoodsFixture();

    private Goods editedGoods;
    private Integer maxShelfIdBeforeAdd;

    @AfterEach
    @Step("убрать изменения стенда")
    void cleanUpTestData() {
        if (editedGoods != null) {
            goodsFixture.restoreGoods(editedGoods);
            editedGoods = null;
        }
        if (maxShelfIdBeforeAdd != null) {
            goodsFixture.deleteGoodsCreatedAfter(maxShelfIdBeforeAdd);
            maxShelfIdBeforeAdd = null;
        }
    }

    @Test
    @Tag("smoke")
    @Step("несколько единиц товара в корзину, оплата и проверка уведомления")
    void addSeveralItemsToCartAndMakeOrder() {
        Goods goods = goodsFixture.getShelfGoodsAt(0);
        String quantity = config.getGoodsQuantityCart();
        double expectedTotal = goods.getPrice() * Integer.parseInt(quantity);

        mainPage.open(config.getBaseUrl())
                .setQuantity(goods.getId(), quantity)
                .addToCart(goods.getId())
                .openCart();

        new MainPageAssert(mainPage)
                .totalPrice(expectedTotal)
                .productInCart(goods.getName());

        mainPage.makeOrder();

        new MainPageAssert(mainPage).toastVisibleWithText(config.getMakeOrderToastText());
    }

    @Test
    @Step("разные товары в корзине и проверка общей суммы")
    void addDifferentItemsAndCheckTotalPrice() {
        List<Goods> shelfGoods = goodsFixture.getShelfGoods();
        Goods first = shelfGoods.get(0);
        Goods second = shelfGoods.get(1);
        double expectedTotal = first.getPrice() + second.getPrice();

        mainPage.open(config.getBaseUrl())
                .addToCart(first.getId())
                .addToCart(second.getId())
                .openCart();

        new MainPageAssert(mainPage)
                .totalPrice(expectedTotal)
                .productInCart(first.getName())
                .productInCart(second.getName());
    }

    @Test
    @Tag("smoke")
    @Step("вход в админку, добавление товара и проверка уведомления")
    void loginAsAdminAndAddProduct() {
        String goodsName = UniqueData.goodsName(config.getGoodsNamePrefixAdmin());
        String goodsPrice = String.valueOf(config.getGoodsPriceAdmin());
        maxShelfIdBeforeAdd = goodsFixture.getMaxShelfGoodsId();

        loginAdmin();

        mainPage.addProduct(goodsName, goodsPrice);

        new MainPageAssert(mainPage).toastVisibleWithText(config.getAddGoodsToastText());
    }

    @Test
    @Step("вход в админку, редактирование товара и проверка изменений")
    void loginAsAdminAndEditProduct() {
        Goods goods = goodsFixture.getShelfGoodsAt(0);
        editedGoods = new Goods(goods.getName(), goods.getPrice());
        editedGoods.setId(goods.getId());

        String newName = UniqueData.goodsName(config.getGoodsNamePrefixAdmin());
        String newPrice = String.valueOf(config.getGoodsPrice());

        loginAdmin();

        mainPage.editProduct(goods.getId(), newName, newPrice);

        new MainPageAssert(mainPage).toastVisibleWithText(config.getEditGoodsToastText());

        mainPage.open(config.getBaseUrl());

        new MainPageAssert(mainPage).productOnShelf(newName);
    }

    @Step("войти в админку и дождаться открытия главной страницы")
    private void loginAdmin() {
        loginPage.open(config.getBaseUrl());
        new LoginPageAssert(loginPage).allElementsVisible();
        loginPage.loginWithConfigCredentials().waitForRedirect();
        new MainPageAssert(mainPage).adminFormVisible();
    }
}
