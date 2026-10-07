package org.example.api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.example.common.config.Config;

import static io.restassured.RestAssured.given;


public class RestApiBuilder {

    private static final Config config = Config.getInstance();

    private RequestSpecification spec;

    private RestApiBuilder() {
        this.spec = given()
                .baseUri(config.getBaseUrl())
                .contentType(ContentType.JSON);
    }

    @Step("API: клиент с авторизацией администратора")
    public static RestApiBuilder authorized() {
        return new RestApiBuilder()
                .addAuth(config.getAdminLogin(), config.getAdminPassword());
    }

    @Step("API: клиент без авторизации")
    public static RestApiBuilder anonymous() {
        return new RestApiBuilder();
    }

    @Step("API: добавить базовую авторизацию (логин: {login})")
    public RestApiBuilder addAuth(String login, String password) {
        spec = spec.auth().basic(login, password);
        return this;
    }

    @Step("API: задать Content-Type {contentType}")
    public RestApiBuilder withContentType(String contentType) {
        spec = spec.contentType(contentType);
        return this;
    }

    @Step("API: собрать RequestSpecification")
    public RequestSpecification build() {
        return spec;
    }
}
