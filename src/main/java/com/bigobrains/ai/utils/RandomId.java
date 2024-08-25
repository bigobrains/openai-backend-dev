package com.bigobrains.ai.utils;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomId {

    public static String nextId() {
        return RandomStringUtils.random(8, "0123456789");
    }
}
