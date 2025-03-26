package com.se.aiconomy.langchain.common.chain;

import com.se.aiconomy.langchain.common.model.ModelConfig;
import dev.langchain4j.model.chat.request.ChatRequestParameters;

public class ChainFactory {
    public static <T extends BaseChain> T createChain(Class<T> chainClass, ModelConfig config) {
        try {
            T chain = chainClass.getDeclaredConstructor().newInstance();
            chain.setModelConfig(config);
            return chain;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create chain: " + e.getMessage(), e);
        }
    }

    public static <T extends BaseChain> T createChain(Class<T> chainClass, ModelConfig config, ChatRequestParameters parameters) {
        try {
            T chain = chainClass.getDeclaredConstructor().newInstance();
            chain.setModelConfig(config);
            chain.setRequestParameters(parameters);
            return chain;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create chain: " + e.getMessage(), e);
        }
    }
}