package com.se.aiconomy.server.langchain.common.config;

import com.se.aiconomy.server.langchain.common.model.ChatModelName;
import lombok.Getter;

/**
 * Configuration class for model parameters.
 * <p>
 * This class encapsulates the configuration required for a model,
 * including base URL, API key, model name, temperature, and max tokens.
 * It uses the builder pattern for flexible and readable instantiation.
 * </p>
 */
@Getter
public class ModelConfig {
    /**
     * The base URL for the model API.
     */
    private final String baseUrl;
    /**
     * The API key for authentication.
     */
    private final String apiKey;
    /**
     * The name of the chat model.
     */
    private final ChatModelName modelName;
    /**
     * The temperature parameter for the model.
     */
    private final double temperature;
    /**
     * The maximum number of tokens allowed.
     */
    private final int maxTokens;

    /**
     * Private constructor for ModelConfig.
     * Instantiated via the {@link Builder}.
     *
     * @param builder the builder instance containing configuration values
     */
    private ModelConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.apiKey = builder.apiKey;
        this.modelName = builder.modelName;
        this.temperature = builder.temperature;
        this.maxTokens = builder.maxTokens;
    }

    /**
     * Builder class for {@link ModelConfig}.
     * <p>
     * Provides a fluent API to set configuration parameters.
     * </p>
     */
    public static class Builder {
        private String baseUrl;
        private String apiKey;
        private ChatModelName modelName;
        private double temperature = 0.7;
        private int maxTokens = 2048;

        /**
         * Sets the base URL for the model API.
         *
         * @param baseUrl the base URL
         * @return the builder instance
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets the API key for authentication.
         *
         * @param apiKey the API key
         * @return the builder instance
         */
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Sets the chat model name.
         *
         * @param modelName the chat model name
         * @return the builder instance
         */
        public Builder modelName(ChatModelName modelName) {
            this.modelName = modelName;
            return this;
        }

        /**
         * Sets the temperature parameter for the model.
         *
         * @param temperature the temperature value
         * @return the builder instance
         */
        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        /**
         * Sets the maximum number of tokens.
         *
         * @param maxTokens the maximum tokens
         * @return the builder instance
         */
        public Builder maxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        /**
         * Builds and returns a {@link ModelConfig} instance.
         *
         * @return a new ModelConfig object
         */
        public ModelConfig build() {
            return new ModelConfig(this);
        }
    }

}