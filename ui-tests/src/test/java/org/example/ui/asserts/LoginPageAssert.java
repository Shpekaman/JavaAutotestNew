package org.example.ui.asserts;

import io.qameta.allure.Step;
import org.example.ui.page.LoginPage;

import static org.assertj.core.api.Assertions.assertThat;

//проверка страницы с логином
public class LoginPageAssert {

    private final LoginPage loginPage;

    public LoginPageAssert(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    @Step("UI-проверка: форма логина отображается")
    public LoginPageAssert allElementsVisible() {
        assertThat(loginPage.isLoginFormVisible())
                .as("Форма логина должна отображаться на странице входа")
                .isTrue();
        return this;
    }
}
