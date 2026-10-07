package org.example.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

//Единая точка доступа ко всем тестовым данным проекта.

public final class Config {

    private static final String CONFIG_FILE = "config.properties";
    private static final Config INSTANCE = new Config();

    private final Properties properties = new Properties();

    private Config() {
        load();
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    private void load() {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException(CONFIG_FILE + " не найден в classpath");
            }
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать " + CONFIG_FILE, e);
        }
    }

    public String get(String key) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(toEnvName(key));
        }
        if (value == null || value.isBlank()) {
            value = properties.getProperty(key);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("В " + CONFIG_FILE + " нет обязательного параметра: " + key);
        }
        return value.trim();
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public float getFloat(String key) {
        return Float.parseFloat(get(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    // Адреса и эндпоинты
    public String getBaseUrl() {
        return trimTrailingSlash(get("BASE_URL"));
    }

    public String getLoginPath() {
        return get("LOGIN_API_PATH");
    }

    public String getLogoutPath() {
        return get("LOGOUT_API_PATH");
    }

    public String getGoodsAddPath() {
        return get("GOODS_ADD_API_PATH");
    }

    public String getGoodsListPath() {
        return get("GOODS_LIST_API_PATH");
    }

    public String getGoodByIdPath() {
        return get("GOOD_BY_ID_API_PATH");
    }

    public int getGoodsListSize() {
        return getInt("GOODS_LIST_SIZE");
    }

    public int getGoodsListPage() {
        return getInt("GOODS_LIST_PAGE");
    }

    public double getPriceDelta() {
        return getFloat("PRICE_DELTA");
    }

    public int getGoodsWaitTimeoutMs() {
        return getInt("GOODS_WAIT_TIMEOUT_MS");
    }

    public int getApiRetryIntervalMs() {
        return getInt("API_RETRY_INTERVAL_MS");
    }

    public int getCleanupIdProbeStep() {
        return getInt("CLEANUP_ID_PROBE_STEP");
    }

    public String getAdminPath() {
        return get("ADMIN_PAGE_PATH");
    }

    public String getLoginErrorRedirect() {
        return get("LOGIN_ERROR_REDIRECT");
    }

    // Учётная запись администратора
    public String getAdminLogin() {
        return get("ADMIN_LOGIN");
    }

    public String getAdminPassword() {
        return get("ADMIN_PASSWORD");
    }

    // Браузер и таймауты
    public String getBrowser() {
        return get("BROWSER");
    }

    public String getBrowserSize() {
        return get("BROWSER_SIZE");
    }

    public boolean isHeadless() {
        return getBoolean("HEADLESS");
    }

    public int getElementTimeoutMs() {
        return getInt("ELEMENT_TIMEOUT_MS");
    }

    public int getPageLoadTimeoutMs() {
        return getInt("PAGE_LOAD_TIMEOUT_MS");
    }

    public String getAllureServletName() {
        return get("ALLURE_SERVLET_NAME");
    }

    // Локаторы
    public String locatorLoginUsername() {
        return get("LOCATOR_LOGIN_USERNAME");
    }

    public String locatorLoginPassword() {
        return get("LOCATOR_LOGIN_PASSWORD");
    }

    public String locatorLoginSubmit() {
        return get("LOCATOR_LOGIN_SUBMIT");
    }

    public String locatorAdminNameInput() {
        return get("LOCATOR_ADMIN_NAME_INPUT");
    }

    public String locatorAdminPriceInput() {
        return get("LOCATOR_ADMIN_PRICE_INPUT");
    }

    public String locatorAdminAddButton() {
        return get("LOCATOR_ADMIN_ADD_BUTTON");
    }

    public String locatorCartButton() {
        return get("LOCATOR_CART_BUTTON");
    }

    public String locatorCartItems() {
        return get("LOCATOR_CART_ITEMS");
    }

    public String locatorTotalPrice() {
        return get("LOCATOR_TOTAL_PRICE");
    }

    public String locatorMakeOrderButton() {
        return get("LOCATOR_MAKE_ORDER_BUTTON");
    }

    public String locatorToastContainer() {
        return get("LOCATOR_TOAST_CONTAINER");
    }

    public String locatorProductNames() {
        return get("LOCATOR_PRODUCT_NAMES");
    }

    public String quantityInput(long id) {
        return get("LOCATOR_QUANTITY_INPUT_PREFIX") + id;
    }

    public String addToCartButton(long id) {
        return get("LOCATOR_ADD_TO_CART_PREFIX") + id + "']";
    }

    public String productNameInput(long id) {
        return get("LOCATOR_NAME_INPUT_PREFIX") + id;
    }

    public String productPriceInput(long id) {
        return get("LOCATOR_PRICE_INPUT_PREFIX") + id;
    }

    public String updateProductButton(long id) {
        return get("LOCATOR_UPDATE_BUTTON_PREFIX") + id + "']";
    }

    // Ожидаемый текст
    public String getAddGoodsToastText() {
        return get("ADD_GOODS_TOAST_TEXT");
    }

    public String getEditGoodsToastText() {
        return get("EDIT_GOODS_TOAST_TEXT");
    }

    public String getMakeOrderToastText() {
        return get("MAKE_ORDER_TOAST_TEXT");
    }

    // Данные для тестов
    public String getGoodsNamePrefix() {
        return get("GOODS_NAME_PREFIX");
    }

    public String getGoodsNamePrefixAdmin() {
        return get("GOODS_NAME_PREFIX_ADMIN");
    }

    public float getGoodsPrice() {
        return getFloat("GOODS_PRICE");
    }

    public float getGoodsPriceAdmin() {
        return getFloat("GOODS_PRICE_ADMIN");
    }

    public String getGoodsQuantityCart() {
        return get("GOODS_QUANTITY_CART");
    }

    public long getExistingGoodId() {
        return getInt("EXISTING_GOOD_ID");
    }

    public long getNotExistingGoodId() {
        return getInt("NOT_EXISTING_GOOD_ID");
    }

    // Ожидаемые тексты ответов API
    public String getDeleteSuccessMessage() {
        return get("DELETE_SUCCESS_MESSAGE");
    }

    public String getAddGoodsSuccessMessage() {
        return get("ADD_GOODS_SUCCESS_MESSAGE");
    }

    public String getGoodsNameEmptyErrorMessage() {
        return get("GOODS_NAME_EMPTY_ERROR_MESSAGE");
    }

    public String getServerErrorMessage() {
        return get("SERVER_ERROR_MESSAGE");
    }

    private static String toEnvName(String key) {
        return key.toUpperCase().replace('-', '_').replace('.', '_');
    }

    private static String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
