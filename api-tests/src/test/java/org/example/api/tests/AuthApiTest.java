package org.example.api.tests;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.example.api.AllureApiSetup;
import org.example.api.asserts.ApiAssert;
import org.example.api.basic.AuthApi;
import org.example.api.basic.GoodsApi;
import org.example.common.config.Config;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;

//Тесты эндпоинтов авторизации: POST /login, GET /logout,

@Tag("apiTest")
@ExtendWith(AllureApiSetup.class)
@DisplayName("API: авторизация")
class AuthApiTest {

    private static final Config config = Config.getInstance();

    private final AuthApi authApi = new AuthApi();
    private final GoodsApi goodsApi = new GoodsApi();
    private final ApiAssert apiAssert = new ApiAssert();

    @Test
    @Tag("smoke")
    @Step("API: успешный вход — 302 на админку и cookie сессии")
    void loginWithValidCredentialsRedirectsToAdmin() {
        Response response = authApi.loginAsAdmin();

        apiAssert.statusCode(response, 302);
        apiAssert.redirectTo(response, config.getBaseUrl() + config.getAdminPath());
        apiAssert.sessionCookieIsSet(response);
    }

    @Test
    @Step("API: вход с неверными учётными данными — 302 обратно на страницу логина")
    void loginWithWrongCredentialsRedirectsToLoginWithError() {
        Response response = authApi.login(config.getAdminLogin(), config.getAdminPassword() + "_wrong");

        apiAssert.statusCode(response, 302);
        apiAssert.redirectContains(response, config.getLoginErrorRedirect());
    }

    @Test
    @Tag("smoke")
    @Step("API: выход из админки — 204")
    void logoutReturnsNoContent() {
        apiAssert.statusCode(authApi.logout(), 204);
    }

    @Test
    @Step("API: после выхода сессия больше не действует")
    void sessionIsNotValidAfterLogout() {
        Response login = authApi.loginAsAdmin();
        apiAssert.sessionCookieIsSet(login);

        Response logout = authApi.logout(login.getCookie("JSESSIONID"));

        apiAssert.statusCode(logout, 204);
    }

    @Test
    @Step("API: добавление товара без авторизации — 401")
    void addGoodsWithoutAuthorizationReturns401() {
        Response response = given()
                .contentType("application/json")
                .body("{\"name\":\"" + config.getGoodsNamePrefix() + "\",\"price\":" + config.getGoodsPrice() + "}")
                .when()
                .post(config.getGoodsAddPath())
                .andReturn();

        apiAssert.statusCode(response, 401);
    }

    @Test
    @Step("API: список товаров доступен без авторизации")
    void goodsListIsAvailableWithoutAuthorization() {
        apiAssert.statusCode(goodsApi.getGoodsList(), 200);
    }
}
