package com.se.aiconomy.server.langchain.common.chain;

import com.se.aiconomy.server.langchain.common.config.ModelConfig;
import dev.langchain4j.model.chat.request.ChatRequestParameters;

/**
 * Factory class for creating instances of {@link BaseChain}.
 * Provides methods to instantiate chain objects with specified model configurations
 * and optional chat request parameters.
 */
public class ChainFactory {

    /**
     * Creates an instance of the specified {@link BaseChain} subclass with the given model configuration.
     *
     * @param chainClass the class of the chain to instantiate
     * @param config     the model configuration to set on the chain
     * @param <T>        the type of the chain, extending {@link BaseChain}
     * @return an instance of the specified chain class with the model configuration set
     * @throws RuntimeException if instantiation fails
     */
    public static <T extends BaseChain> T createChain(Class<T> chainClass, ModelConfig config) {
        try {
            T chain = chainClass.getDeclaredConstructor().newInstance();
            chain.setModelConfig(config);
            return chain;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create chain: " + e.getMessage(), e);
        }
    }

    /**
     * Creates an instance of the specified {@link BaseChain} subclass with the given model configuration
     * and chat request parameters.
     *
     * @param chainClass the class of the chain to instantiate
     * @param config     the model configuration to set on the chain
     * @param parameters the chat request parameters to set on the chain
     * @param <T>        the type of the chain, extending {@link BaseChain}
     * @return an instance of the specified chain class with the model configuration and request parameters set
     * @throws RuntimeException if instantiation fails
     */
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