package org.example.ui.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.example.common.config.Config;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

//Page object лавной страницы. Локаторы из common
public class MainPage {

    private static final Config config = Config.getInstance();

    private final SelenideElement cartButton = Selenide.$(config.locatorCartButton());
    private final SelenideElement totalPrice = Selenide.$(config.locatorTotalPrice());
    private final SelenideElement makeOrderButton = Selenide.$(config.locatorMakeOrderButton());
    private final SelenideElement toastContainer = Selenide.$(config.locatorToastContainer());
    private final SelenideElement adminNameInput = Selenide.$(config.locatorAdminNameInput());
    private final SelenideElement adminPriceInput = Selenide.$(config.locatorAdminPriceInput());
    private final SelenideElement adminAddButton = Selenide.$(config.locatorAdminAddButton());

    @Step("UI: открыть главную страницу {baseUrl}")
    public MainPage open(String baseUrl) {
        Selenide.open(baseUrl);
        return this;
    }

    // Витрина и корзина
    @Step("UI: указать количество {quantity} для товара id={id}")
    public MainPage setQuantity(long id, String quantity) {
        Selenide.$(config.quantityInput(id)).shouldBe(visible).setValue(quantity);
        return this;
    }

    @Step("UI: добавить товар id={id} в корзину")
    public MainPage addToCart(long id) {
        Selenide.$(config.addToCartButton(id)).shouldBe(clickable).click();
        return this;
    }

    @Step("UI: открыть корзину")
    public MainPage openCart() {
        cartButton.shouldBe(clickable).click();
        return this;
    }

    @Step("UI: оформить заказ")
    public MainPage makeOrder() {
        makeOrderButton.shouldBe(clickable).click();
        return this;
    }

    // Админ-форма на главной странице

    @Step("UI: добавить товар '{name}' с ценой {price} через админку")
    public MainPage addProduct(String name, String price) {
        adminNameInput.shouldBe(visible).setValue(name);
        adminPriceInput.shouldBe(visible).setValue(price);
        adminAddButton.shouldBe(clickable).click();
        return this;
    }

    @Step("UI: отредактировать товар id={id}: имя '{name}', цена {price}")
    public MainPage editProduct(long id, String name, String price) {
        SelenideElement nameInput = Selenide.$(config.productNameInput(id));
        nameInput.clear();
        nameInput.setValue(name);

        SelenideElement priceInput = Selenide.$(config.productPriceInput(id));
        priceInput.clear();
        priceInput.setValue(price);

        Selenide.$(config.updateProductButton(id)).shouldBe(clickable).click();
        return this;
    }

    // Данные со страницы
    @Step("UI-проверка: общая сумма в корзине числом")
    public double getTotalPrice() {
        return Double.parseDouble(totalPrice.getText().trim());
    }

    @Step("UI-проверка: есть ли товар '{name}' на витрине")
    public boolean isProductOnShelf(String name) {
        return $$(config.locatorProductNames()).findBy(text(name)).is(visible);
    }

    @Step("UI-проверка: есть ли товар '{name}' в корзине")
    public boolean isProductInCart(String name) {
        return $$(config.locatorCartItems()).findBy(text(name)).is(visible);
    }

    @Step("UI-проверка: видно ли уведомление с текстом '{expectedText}'")
    public boolean isToastWithTextVisible(String expectedText) {
        return toastContainer.is(visible) && toastContainer.getText().contains(expectedText);
    }

    @Step("UI-проверка: видна ли админ-форма добавления товара")
    public boolean isAdminFormVisible() {
        return adminNameInput.is(visible) && adminPriceInput.is(visible) && adminAddButton.is(visible);
    }
}
