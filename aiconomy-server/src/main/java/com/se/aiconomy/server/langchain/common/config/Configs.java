package com.se.aiconomy.server.langchain.common.config;

import com.se.aiconomy.server.langchain.common.model.ChatModelName;
import io.github.cdimascio.dotenv.Dotenv;

import static com.se.aiconomy.server.langchain.common.model.ChatModelName.GPT_4_1_MINI;
import static dev.langchain4j.internal.Utils.getOrDefault;

public class Configs {
    public static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    public static final String BASE_URL = getOrDefault(System.getenv("BASE_URL"), dotenv.get("BASE_URL"));
    public static final String API_KEY = getOrDefault(System.getenv("API_KEY"), dotenv.get("API_KEY"));
    public static final ChatModelName MODEL = GPT_4_1_MINI;
}