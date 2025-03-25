package com.se.aiconomy.common.langchain.config;

public enum Locale {
    CN("CN"),
    EN("EN"),
    JP("JP");

    private final String value;

    Locale(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
