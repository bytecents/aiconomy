package com.se.aiconomy.langchain.service.chat;

import com.se.aiconomy.langchain.common.prompt.BasePromptTemplate;

public class Prompt extends BasePromptTemplate {

    private static final String PROMPT_TEMPLATE_EN = """
        Give me a code about {{ code }}.
        """;

    private static final String PROMPT_TEMPLATE_CN = """
        给我一段有关{{ code }}的代码。
        """;

    private static final String PROMPT_TEMPLATE_JP = """
        {{ code }}に関するコードを教えてください。
        """;

    public Prompt() {
        super(PROMPT_TEMPLATE_CN, PROMPT_TEMPLATE_EN, PROMPT_TEMPLATE_JP);
    }
}
