package com.se.aiconomy.langchain.common.chain;

import com.se.aiconomy.langchain.common.config.Locale;
import com.se.aiconomy.langchain.common.model.ModelConfig;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.Setter;

import java.util.Map;

@Setter
public abstract class BaseChain {
    protected ModelConfig modelConfig;
    protected ChatRequestParameters requestParameters;

    public abstract String invoke(Locale locale, Map<String, Object> variables);

    public abstract void stream(Locale locale, Map<String, Object> variables, StreamingChatResponseHandler responseHandler);
}