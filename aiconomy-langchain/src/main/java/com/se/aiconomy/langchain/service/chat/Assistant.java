package com.se.aiconomy.langchain.service.chat;

import dev.langchain4j.service.TokenStream;

public interface Assistant {
    String invoke(String prompt);

    TokenStream stream(String prompt);
}
