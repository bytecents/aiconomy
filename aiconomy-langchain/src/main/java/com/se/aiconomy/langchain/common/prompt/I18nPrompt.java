package com.se.aiconomy.langchain.common.prompt;

import com.hubspot.jinjava.Jinjava;
import com.se.aiconomy.langchain.common.config.Locale;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

public class I18nPrompt {
    private static final Jinjava JINJAVA = new Jinjava();
    private final PromptTemplate promptCn;
    private final PromptTemplate promptEn;
    private final PromptTemplate promptJp;

    public I18nPrompt(BasePromptTemplate promptTemplate) {
        this.promptCn = PromptTemplate.from(promptTemplate.getPromptTemplate(Locale.CN));
        this.promptEn = PromptTemplate.from(promptTemplate.getPromptTemplate(Locale.EN));
        this.promptJp = PromptTemplate.from(promptTemplate.getPromptTemplate(Locale.JP));
    }

    /**
     * 获取原始 Jinja2 Prompt（未填充变量）
     */
    public String getPrompt(Locale locale) {
        return switch (locale) {
            case CN -> promptCn.template();
            case JP -> promptJp.template();
            default -> promptEn.template();
        };
    }

    public String getPrompt(Locale locale, Map<String, Object> variable) {
        return switch (locale) {
            case CN -> promptCn.apply(variable).text();
            case JP -> promptJp.apply(variable).text();
            default -> promptEn.apply(variable).text();
        };
    }

    /**
     * 直接填充变量并返回最终的 Prompt
     */
    public String render(Locale locale, Map<String, Object> variables) {
        return JINJAVA.render(getPrompt(locale), variables);
    }
}
