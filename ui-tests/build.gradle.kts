//Модуль UI-автотестов: PageObject-модель, Assert-классы, кейсы вебинара VI
val allureVersion = property("allureVersion") as String

dependencies {
    testImplementation(project(":common"))
    testImplementation("com.codeborne:selenide:7.18.1")
    testImplementation("io.qameta.allure:allure-selenide:$allureVersion")
    //Фикстуры UI-тестов готовят товары через API тестового стенда
    testImplementation("io.rest-assured:rest-assured:5.5.6")
    testImplementation("io.rest-assured:json-path:5.4.0")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}

//Полный прогон всех UI-автотестов
tasks.named<Test>("test") {
    useJUnitPlatform {
        includeTags("uiTest")
    }
}

//Smoke-прогон UI-автотестов
tasks.register<Test>("smokeUiTest") {
    group = "verification"
    description = "Smoke-прогон UI-автотестов"
    useJUnitPlatform {
        includeTags("smoke")
    }
    shouldRunAfter(tasks.named("test"))
}