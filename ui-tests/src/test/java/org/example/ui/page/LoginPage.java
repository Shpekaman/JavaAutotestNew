package org.example.ui.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.example.common.config.Config;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.visible;

//Page object страницы с логином. Локаторы из common
public class LoginPage {

    private static final Config config = Config.getInstance();

    private final SelenideElement usernameInput = Selenide.$(config.locatorLoginUsername());
    private final SelenideElement passwordInput = Selenide.$(config.locatorLoginPassword());
    private final SelenideElement submitButton = Selenide.$(config.locatorLoginSubmit());

    @Step("UI: открыть страницу логина {baseUrl}")
    public LoginPage open(String baseUrl) {
        Selenide.open(baseUrl + config.getLoginPath());
        return this;
    }

    @Step("UI: заполнить логин '{login}'")
    public LoginPage setLogin(String login) {
        usernameInput.shouldBe(visible).setValue(login);
        return this;
    }

    @Step("UI: заполнить пароль")
    public LoginPage setPassword(String password) {
        passwordInput.shouldBe(visible).setValue(password);
        return this;
    }

    @Step("UI: нажать кнопку входа")
    public LoginPage clickLogin() {
        submitButton.shouldBe(clickable).click();
        return this;
    }

    @Step("UI: войти в админку логином и паролем из конфига")
    public LoginPage loginWithConfigCredentials() {
        return setLogin(config.getAdminLogin())
                .setPassword(config.getAdminPassword())
                .clickLogin();
    }

    @Step("UI-проверка: видна ли форма логина")
    public boolean isLoginFormVisible() {
        return usernameInput.is(visible) && passwordInput.is(visible) && submitButton.is(visible);
    }

    @Step("UI: дождаться редиректа после входа")
    public LoginPage waitForRedirect() {
        long deadline = System.currentTimeMillis() + config.getPageLoadTimeoutMs();
        while (System.currentTimeMillis() < deadline) {
            if (!WebDriverRunner.getWebDriver().getCurrentUrl().contains(config.getLoginPath())) {
                return this;
            }
            Selenide.sleep(200);
        }
        throw new AssertionError("После входа не произошло перехода со страницы логина");
    }
}
