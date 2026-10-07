
val allureVersion = property("allureVersion") as String

dependencies {
    testImplementation(project(":common"))
    testImplementation("io.rest-assured:rest-assured:5.5.6")
    testImplementation("io.rest-assured:json-path:5.4.0")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    testImplementation("io.qameta.allure:allure-rest-assured:$allureVersion")
}

//Полный прогон всех API-автотестов
tasks.named<Test>("test") {
    useJUnitPlatform {
        includeTags("apiTest")
    }
}

//Smoke-прогон API-автотестов
tasks.register<Test>("smokeApiTest") {
    group = "verification"
    description = "Smoke-прогон API-автотестов"
    useJUnitPlatform {
        includeTags("smoke")
    }
    shouldRunAfter(tasks.named("test"))
}
