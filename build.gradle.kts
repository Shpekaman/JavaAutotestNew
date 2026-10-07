plugins {
    id("base")
}

val allureVersion = property("allureVersion") as String

repositories {

    mavenCentral()
}

//чтобы отчёт собирался сразу по обоим модулям
val sharedAllureResults = rootProject.layout.buildDirectory.dir("allure-results")
val allureReportDir = rootProject.layout.buildDirectory.dir("reports/allure-report")
val allureHome = rootProject.layout.buildDirectory.dir("allure/commandline")


val allureCommandline: Configuration by configurations.creating
dependencies {
    allureCommandline("io.qameta.allure:allure-commandline:$allureVersion@zip")
}

val extractAllureCommandline by tasks.registering(Sync::class) {
    description = "Скачать и распаковать Allure CLI"
    from({ zipTree(allureCommandline.singleFile) }) {
        //В архиве Allure CLI всё лежит в папке allure-<версия>, убираем лишний уровень
        eachFile { path = path.substringAfter("/") }
        includeEmptyDirs = false
    }
    into(allureHome)
}

val isWindows = System.getProperty("os.name").lowercase().contains("windows")

tasks.register<Exec>("allureReport") {
    group = "reporting"
    description = "Собрать Allure-отчёт по API- и UI-автотестам"
    dependsOn(extractAllureCommandline)

    inputs.dir(sharedAllureResults).withPropertyName("allureResults")
    outputs.dir(allureReportDir).withPropertyName("allureReport")

    val script = allureHome.map { home ->
        home.file(if (isWindows) "bin\\allure.bat" else "bin/allure").asFile
    }

    doFirst {
        commandLine(
            script.get().absolutePath,
            "generate",
            sharedAllureResults.get().asFile.absolutePath,
            "--clean",
            "-o",
            allureReportDir.get().asFile.absolutePath
        )
    }
}

tasks.register<Exec>("allureServe") {
    group = "reporting"
    description = "Открыть Allure-отчёт в браузере"
    dependsOn(extractAllureCommandline)

    val script = allureHome.map { home ->
        home.file(if (isWindows) "bin\\allure.bat" else "bin/allure").asFile
    }

    doFirst {
        commandLine(script.get().absolutePath, "open", allureReportDir.get().asFile.absolutePath)
    }
}

subprojects {
    apply(plugin = "java-library")

    //АспектJ-агент для перехвата аннотаций @Step в тестах модуля
    val aspectj: Configuration by configurations.creating

    repositories {
        mavenCentral()
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    //Кодировка исходников и результатов тестов фиксирована, чтобы кириллица не ломалась
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        systemProperty("file.encoding", "UTF-8")
        systemProperty("allure.results.directory", sharedAllureResults.get().asFile.absolutePath)
        //aspectjweaver нужен Allure для перехвата аннотаций @Step
        doFirst {
            jvmArgs("-javaagent:${aspectj.resolve().first()}")
        }
        testLogging {
            events("failed")
        }
    }

    dependencies {
        add(aspectj.name, "org.aspectj:aspectjweaver:1.9.22.1")
        "testImplementation"(platform("org.junit:junit-bom:5.10.0"))
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testImplementation"("org.assertj:assertj-core:3.27.7")
        "testImplementation"("io.qameta.allure:allure-junit5:$allureVersion")
        "testImplementation"("io.qameta.allure:allure-java-commons:$allureVersion")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }
}

tasks.register("smoke") {
    group = "verification"
    description = "Smoke-прогон API- и UI-автотестов"
    dependsOn(":api-tests:smokeApiTest", ":ui-tests:smokeUiTest")
}

tasks.register("apiTests") {
    group = "verification"
    description = "Полный прогон API-автотестов"
    dependsOn(":api-tests:test")
}

tasks.register("uiTests") {
    group = "verification"
    description = "Полный прогон UI-автотестов"
    dependsOn(":ui-tests:test")
}

tasks.register("allTests") {
    group = "verification"
    description = "Полный прогон API- и UI-автотестов"
    dependsOn(":api-tests:test", ":ui-tests:test")
}
