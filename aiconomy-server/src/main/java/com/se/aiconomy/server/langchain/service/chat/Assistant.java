package com.se.aiconomy.server.langchain.service.chat;

import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

/**
 * The Assistant interface defines methods for interacting with an AI assistant
 * using text prompts. It provides both synchronous and streaming response capabilities.
 */
public interface Assistant {

    /**
     * Sends a prompt to the AI assistant and returns the response as a String.
     *
     * @param prompt the user message to send to the assistant
     * @return the assistant's response as a String
     */
    String invoke(@UserMessage String prompt);

    /**
     * Sends a prompt to the AI assistant and returns a TokenStream for streaming responses.
     *
     * @param prompt the user message to send to the assistant
     * @return a TokenStream representing the assistant's streaming response
     */
    TokenStream stream(@UserMessage String prompt);
}