package com.se.aiconomy.langchain.common.chain;

import com.se.aiconomy.langchain.common.config.Locale;
import com.se.aiconomy.langchain.common.model.ModelConfig;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;

import java.util.Map;

public abstract class BaseChain {
    protected Locale defaultLocale = Locale.EN;
    protected ModelConfig modelConfig;
    protected ChatRequestParameters requestParameters;

    public abstract String invoke(Locale locale, Map<String, Object> variables);

    public abstract void stream(Locale locale, Map<String, Object> variables, StreamingChatResponseHandler responseHandler);

    public void setModelConfig(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    public void setRequestParameters(ChatRequestParameters parameters) {
        this.requestParameters = parameters;
    }

    public BaseChain withLocale(Locale locale) {
        this.defaultLocale = locale;
        return this;
    }

    public BaseChain withRequestParameters(ChatRequestParameters parameters) {
        this.requestParameters = parameters;
        return this;
    }
}