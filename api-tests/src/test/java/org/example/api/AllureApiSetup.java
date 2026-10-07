package org.example.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

//Глобальное подключение AllureRestAssured
public class AllureApiSetup implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        RestAssured.filters(new AllureRestAssured());
    }
}
