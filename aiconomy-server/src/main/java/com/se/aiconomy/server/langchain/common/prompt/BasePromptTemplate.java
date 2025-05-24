package com.se.aiconomy.server.langchain.common.prompt;

import com.se.aiconomy.server.langchain.common.config.Locale;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for prompt templates supporting multiple languages.
 * <p>
 * This class provides a unified structure for managing prompt templates in Chinese, English, and Japanese.
 * Subclasses should initialize the language-specific templates via the constructor.
 * </p>
 */
public abstract class BasePromptTemplate {

    /**
     * The prompt template in Chinese.
     */
    protected final String PROMPT_TEMPLATE_CN;

    /**
     * The prompt template in English.
     */
    protected final String PROMPT_TEMPLATE_EN;

    /**
     * The prompt template in Japanese.
     */
    protected final String PROMPT_TEMPLATE_JP;

    /**
     * Initializes the language-specific prompt templates for subclasses.
     *
     * @param cnTemplate the Chinese prompt template
     * @param enTemplate the English prompt template
     * @param jpTemplate the Japanese prompt template
     */
    protected BasePromptTemplate(String cnTemplate, String enTemplate, String jpTemplate) {
        this.PROMPT_TEMPLATE_CN = cnTemplate;
        this.PROMPT_TEMPLATE_EN = enTemplate;
        this.PROMPT_TEMPLATE_JP = jpTemplate;
    }

    /**
     * Returns the prompt template corresponding to the specified locale.
     *
     * @param locale the locale for which to retrieve the prompt template
     * @return the prompt template in the specified language
     */
    public String getPromptTemplate(@NotNull Locale locale) {
        return switch (locale) {
            case CN -> this.PROMPT_TEMPLATE_CN;
            case JP -> this.PROMPT_TEMPLATE_JP;
            default -> this.PROMPT_TEMPLATE_EN;
        };
    }
}