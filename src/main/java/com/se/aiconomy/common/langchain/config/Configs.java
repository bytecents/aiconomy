package com.se.aiconomy.common.langchain.config;

import dev.langchain4j.model.openai.OpenAiChatModelName;
import io.github.cdimascio.dotenv.Dotenv;

import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class Configs {
    public static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    public static final String BASE_URL = getOrDefault(System.getenv("BASE_URL"), dotenv.get("BASE_URL"));
    public static final String API_KEY = getOrDefault(System.getenv("API_KEY"), dotenv.get("API_KEY"));
    public static final OpenAiChatModelName MODEL = GPT_4_O_MINI;
}