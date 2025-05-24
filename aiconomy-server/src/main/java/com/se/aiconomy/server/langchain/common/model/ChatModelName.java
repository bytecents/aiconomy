package com.se.aiconomy.server.langchain.common.model;

/**
 * Enum representing the names of various chat models.
 * Each enum constant is associated with a string value that identifies the model.
 */
public enum ChatModelName {
    /**
     * GPT-3.5 Turbo model.
     */
    GPT_3_5_TURBO("gpt-3.5-turbo"),
    /**
     * GPT-3.5 Turbo 1106 model.
     */
    GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106"),
    /**
     * GPT-3.5 Turbo 0125 model.
     */
    GPT_3_5_TURBO_0125("gpt-3.5-turbo-0125"),
    /**
     * GPT-3.5 Turbo 16K model.
     */
    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k"),
    /**
     * GPT-4 model.
     */
    GPT_4("gpt-4"),
    /**
     * GPT-4 0613 model.
     */
    GPT_4_0613("gpt-4-0613"),
    /**
     * GPT-4 Turbo Preview model.
     */
    GPT_4_TURBO_PREVIEW("gpt-4-turbo-preview"),
    /**
     * GPT-4 1106 Preview model.
     */
    GPT_4_1106_PREVIEW("gpt-4-1106-preview"),
    /**
     * GPT-4 0125 Preview model.
     */
    GPT_4_0125_PREVIEW("gpt-4-0125-preview"),
    /**
     * GPT-4 Turbo model.
     */
    GPT_4_TURBO("gpt-4-turbo"),
    /**
     * GPT-4 Turbo 2024-04-09 model.
     */
    GPT_4_TURBO_2024_04_09("gpt-4-turbo-2024-04-09"),
    /**
     * GPT-4 32K model.
     */
    GPT_4_32K("gpt-4-32k"),
    /**
     * GPT-4 32K 0613 model.
     */
    GPT_4_32K_0613("gpt-4-32k-0613"),
    /**
     * GPT-4o model.
     */
    GPT_4_O("gpt-4o"),
    /**
     * GPT-4o 2024-05-13 model.
     */
    GPT_4_O_2024_05_13("gpt-4o-2024-05-13"),
    /**
     * GPT-4o 2024-08-06 model.
     */
    GPT_4_O_2024_08_06("gpt-4o-2024-08-06"),
    /**
     * GPT-4o 2024-11-20 model.
     */
    GPT_4_O_2024_11_20("gpt-4o-2024-11-20"),
    /**
     * GPT-4o Mini model.
     */
    GPT_4_O_MINI("gpt-4o-mini"),
    /**
     * GPT-4o Mini 2024-07-18 model.
     */
    GPT_4_O_MINI_2024_07_18("gpt-4o-mini-2024-07-18"),
    /**
     * O1 model.
     */
    O1("o1"),
    /**
     * O1 2024-12-17 model.
     */
    O1_2024_12_17("o1-2024-12-17"),
    /**
     * O1 Mini model.
     */
    O1_MINI("o1-mini"),
    /**
     * O1 Mini 2024-09-12 model.
     */
    O1_MINI_2024_09_12("o1-mini-2024-09-12"),
    /**
     * O1 Preview model.
     */
    O1_PREVIEW("o1-preview"),
    /**
     * O1 Preview 2024-09-12 model.
     */
    O1_PREVIEW_2024_09_12("o1-preview-2024-09-12"),
    /**
     * O3 Mini model.
     */
    O3_MINI("o3-mini"),
    /**
     * O3 Mini 2025-01-31 model.
     */
    O3_MINI_2025_01_31("o3-mini-2025-01-31"),
    /**
     * GPT-4.1 model.
     */
    GPT_4_1("gpt-4.1"),
    /**
     * GPT-4.1 Mini model.
     */
    GPT_4_1_MINI("gpt-4.1-mini"),
    /**
     * GPT-4.1 Nano model.
     */
    GPT_4_1_NANO("gpt-4.1-nano");

    /**
     * The string value representing the model name.
     */
    private final String stringValue;

    /**
     * Constructs a ChatModelName enum constant with the specified string value.
     *
     * @param stringValue the string value representing the model name
     */
    ChatModelName(String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * Returns the string value of the model name.
     *
     * @return the string value representing the model name
     */
    public String toString() {
        return this.stringValue;
    }
}