package com.se.aiconomy.langchain.common.prompt;

import com.se.aiconomy.langchain.common.config.Locale;
import org.jetbrains.annotations.NotNull;

public abstract class BasePromptTemplate {

    protected final String PROMPT_TEMPLATE_CN;
    protected final String PROMPT_TEMPLATE_EN;
    protected final String PROMPT_TEMPLATE_JP;

    /**
     * 通过构造函数初始化子类 `Prompt` 模板
     */
    protected BasePromptTemplate(String cnTemplate, String enTemplate, String jpTemplate) {
        this.PROMPT_TEMPLATE_CN = cnTemplate;
        this.PROMPT_TEMPLATE_EN = enTemplate;
        this.PROMPT_TEMPLATE_JP = jpTemplate;
    }

    /**
     * 统一的 `getPromptTemplate` 逻辑
     */
    public String getPromptTemplate(@NotNull Locale locale) {
        return switch (locale) {
            case CN -> this.PROMPT_TEMPLATE_CN;
            case JP -> this.PROMPT_TEMPLATE_JP;
            default -> this.PROMPT_TEMPLATE_EN;
        };
    }
}
