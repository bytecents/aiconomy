package com.se.aiconomy.server.langchain.common.config;

import com.se.aiconomy.server.langchain.common.model.ChatModelName;
import io.github.cdimascio.dotenv.Dotenv;

import static com.se.aiconomy.server.langchain.common.model.ChatModelName.GPT_4_1_MINI;
import static dev.langchain4j.internal.Utils.getOrDefault;

/**
 * Configuration class for loading environment variables and default settings.
 * <p>
 * This class provides access to environment variables using the Dotenv library,
 * and defines constants for the base URL, API key, and default chat model.
 * </p>
 */
public class Configs {
    /**
     * Loads environment variables from a .env file if present, otherwise ignores if missing.
     */
    public static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    /**
     * The base URL for API requests, loaded from environment variables or .env file.
     */
    public static final String BASE_URL = getOrDefault(System.getenv("BASE_URL"), dotenv.get("BASE_URL"));

    /**
     * The API key for authentication, loaded from environment variables or .env file.
     */
    public static final String API_KEY = getOrDefault(System.getenv("API_KEY"), dotenv.get("API_KEY"));

    /**
     * The default chat model to be used in the application.
     */
    public static final ChatModelName MODEL = GPT_4_1_MINI;
}