package com.se.aiconomy.server.langchain.common.config;

import lombok.Getter;

@Getter
public enum Locale {
    CN("CN"),
    EN("EN"),
    JP("JP");

    private final String value;

    Locale(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
