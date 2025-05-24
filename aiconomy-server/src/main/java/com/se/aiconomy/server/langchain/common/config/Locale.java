package com.se.aiconomy.server.langchain.common.config;

import lombok.Getter;

/**
 * Enumeration representing supported locales.
 * <p>
 * Each locale is associated with a string value.
 * </p>
 */
@Getter
public enum Locale {
    /**
     * Chinese locale.
     */
    CN("CN"),
    /**
     * English locale.
     */
    EN("EN"),
    /**
     * Japanese locale.
     */
    JP("JP");

    /**
     * The string value representing the locale.
     */
    private final String value;

    /**
     * Constructs a Locale enum with the specified string value.
     *
     * @param value the string value of the locale
     */
    Locale(String value) {
        this.value = value;
    }

    /**
     * Returns the string representation of the locale.
     *
     * @return the string value of the locale
     */
    @Override
    public String toString() {
        return value;
    }
}