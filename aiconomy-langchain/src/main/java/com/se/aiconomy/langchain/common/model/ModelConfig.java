package com.se.aiconomy.langchain.common.model;

import dev.langchain4j.model.openai.OpenAiChatModelName;
import lombok.Getter;

@Getter
public class ModelConfig {
    private final String baseUrl;
    private final String apiKey;
    private final OpenAiChatModelName modelName;
    private final double temperature;
    private final int maxTokens;

    private ModelConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.apiKey = builder.apiKey;
        this.modelName = builder.modelName;
        this.temperature = builder.temperature;
        this.maxTokens = builder.maxTokens;
    }

    public static class Builder {
        private String baseUrl;
        private String apiKey;
        private OpenAiChatModelName modelName;
        private double temperature = 0.7;
        private int maxTokens = 2048;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder modelName(OpenAiChatModelName modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder maxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public ModelConfig build() {
            return new ModelConfig(this);
        }
    }

}