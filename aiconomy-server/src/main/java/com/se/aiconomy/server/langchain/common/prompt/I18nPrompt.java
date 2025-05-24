package com.se.aiconomy.server.langchain.common.prompt;

import com.hubspot.jinjava.Jinjava;
import com.se.aiconomy.server.langchain.common.config.Locale;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

/**
 * Provides internationalized prompt templates and rendering functionality.
 * <p>
 * This class supports prompt templates in Chinese, English, and Japanese,
 * and allows rendering with variable substitution using Jinja2 syntax.
 * </p>
 */
public class I18nPrompt {
    /**
     * The Jinjava engine instance for rendering Jinja2 templates.
     */
    private static final Jinjava JINJAVA = new Jinjava();

    /**
     * The Chinese prompt template.
     */
    private final PromptTemplate promptCn;

    /**
     * The English prompt template.
     */
    private final PromptTemplate promptEn;

    /**
     * The Japanese prompt template.
     */
    private final PromptTemplate promptJp;

    /**
     * Constructs an I18nPrompt with the specified base prompt template.
     *
     * @param promptTemplate the base prompt template containing language-specific templates
     */
    public I18nPrompt(BasePromptTemplate promptTemplate) {
        this.promptCn = PromptTemplate.from(promptTemplate.getPromptTemplate(Locale.CN));
        this.promptEn = PromptTemplate.from(promptTemplate.getPromptTemplate(Locale.EN));
        this.promptJp = PromptTemplate.from(promptTemplate.getPromptTemplate(Locale.JP));
    }

    /**
     * Returns the raw Jinja2 prompt template (without variable substitution) for the specified locale.
     *
     * @param locale the locale for which to retrieve the prompt template
     * @return the raw prompt template string
     */
    public String getPrompt(Locale locale) {
        return switch (locale) {
            case CN -> promptCn.template();
            case JP -> promptJp.template();
            default -> promptEn.template();
        };
    }

    /**
     * Returns the prompt with variables applied for the specified locale.
     *
     * @param locale   the locale for which to retrieve the prompt
     * @param variable the variables to substitute in the prompt template
     * @return the prompt string with variables applied
     */
    public String getPrompt(Locale locale, Map<String, Object> variable) {
        return switch (locale) {
            case CN -> promptCn.apply(variable).text();
            case JP -> promptJp.apply(variable).text();
            default -> promptEn.apply(variable).text();
        };
    }

    /**
     * Renders the final prompt by directly substituting variables using the Jinja2 engine.
     *
     * @param locale    the locale for which to render the prompt
     * @param variables the variables to substitute in the prompt template
     * @return the rendered prompt string
     */
    public String render(Locale locale, Map<String, Object> variables) {
        return JINJAVA.render(getPrompt(locale), variables);
    }
}