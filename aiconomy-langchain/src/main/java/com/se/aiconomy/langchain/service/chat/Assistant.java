package com.se.aiconomy.langchain.service.chat;

import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface Assistant {
    String invoke(@UserMessage String prompt);

    TokenStream stream(@UserMessage String prompt);
}
