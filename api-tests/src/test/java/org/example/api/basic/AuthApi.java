package org.example.api.basic;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.api.RestApiBuilder;
import org.example.common.config.Config;

//класс для эндпоинтов авторизации: POST /login, GET /logout.

public class AuthApi {

    private static final Config config = Config.getInstance();

    private final RequestSpecification spec;

    public AuthApi() {
        this(RestApiBuilder.authorized().build());
    }

    public AuthApi(RequestSpecification spec) {
        this.spec = spec;
    }

    @Step("API: войти в админку через POST /login (логин: {login})")
    public Response login(String login, String password) {
        return givenForm()
                .formParam("username", login)
                .formParam("password", password)
                .when()
                .post(config.getLoginPath())
                .andReturn();
    }

    @Step("API: войти в админку учётной записью из конфига через POST /login")
    public Response loginAsAdmin() {
        return login(config.getAdminLogin(), config.getAdminPassword());
    }

    @Step("API: выйти из админки через GET /logout")
    public Response logout() {
        return givenForm()
                .when()
                .get(config.getLogoutPath())
                .andReturn();
    }

    @Step("API: выйти из админки по cookie сессии через GET /logout")
    public Response logout(String sessionId) {
        return spec
                .cookie("JSESSIONID", sessionId)
                .when()
                .get(config.getLogoutPath())
                .andReturn();
    }

    private RequestSpecification givenForm() {
        return RestApiBuilder.anonymous()
                .withContentType(ContentType.URLENC.toString())
                .build();
    }
}
