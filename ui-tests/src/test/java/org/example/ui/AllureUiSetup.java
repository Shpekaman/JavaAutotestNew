package org.example.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.example.common.config.Config;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

//Настройка браузера и подключение AllureSelenide:

public class AllureUiSetup implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        Config config = Config.getInstance();

        Configuration.browser = config.getBrowser();
        Configuration.browserSize = config.getBrowserSize();
        Configuration.headless = config.isHeadless();
        Configuration.timeout = config.getElementTimeoutMs();
        Configuration.pageLoadTimeout = config.getPageLoadTimeoutMs();

        SelenideLogger.addListener(config.getAllureServletName(),
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true)
                        .includeSelenideSteps(true));
    }
}
