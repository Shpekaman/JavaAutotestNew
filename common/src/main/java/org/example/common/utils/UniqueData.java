package org.example.common.utils;

import java.util.UUID;

//генерация для тестовых данных
public final class UniqueData {

    private static final int SUFFIX_LENGTH = 8;

    private UniqueData() {
    }

    public static String goodsName(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, SUFFIX_LENGTH);
    }
}
