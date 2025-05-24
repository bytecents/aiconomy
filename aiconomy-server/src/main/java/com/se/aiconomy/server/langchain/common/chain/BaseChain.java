package com.se.aiconomy.server.langchain.common.chain;

import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.common.config.ModelConfig;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.Setter;

import java.util.Map;

/**
 * Abstract base class for all chain implementations.
 * Provides the basic structure for invoking and streaming chat operations
 * with specific model configurations and request parameters.
 */
@Setter
public abstract class BaseChain {
    /**
     * The configuration for the language model.
     */
    protected ModelConfig modelConfig;

    /**
     * The parameters for the chat request.
     */
    protected ChatRequestParameters requestParameters;

    /**
     * Invokes the chain with the specified locale and variables.
     *
     * @param locale    the locale to use for the operation
     * @param variables a map of variables to be used in the chain
     * @return the result of the chain invocation as a String
     */
    public abstract String invoke(Locale locale, Map<String, Object> variables);

    /**
     * Streams the chain response with the specified locale and variables.
     *
     * @param locale          the locale to use for the operation
     * @param variables       a map of variables to be used in the chain
     * @param responseHandler the handler for streaming chat responses
     */
    public abstract void stream(Locale locale, Map<String, Object> variables, StreamingChatResponseHandler responseHandler);
}