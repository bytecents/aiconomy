package com.se.aiconomy.langchain.chains.chat;

import com.se.aiconomy.langchain.common.chain.BaseChain;
import com.se.aiconomy.langchain.common.config.Locale;
import com.se.aiconomy.langchain.common.prompt.I18nPrompt;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class Chain extends BaseChain {

    private static final Logger LOGGER = LogManager.getLogger(Chain.class);

    @Override
    public String invoke(Locale locale, Map<String, Object> variables) {
        OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
            .baseUrl(modelConfig.getBaseUrl())
            .apiKey(modelConfig.getApiKey())
            .modelName(modelConfig.getModelName())
            .temperature(modelConfig.getTemperature())
            .maxTokens(modelConfig.getMaxTokens())
            .logRequests(true)
            .logResponses(true);

        if (requestParameters != null) {
            builder.defaultRequestParameters(requestParameters);
        }

        OpenAiChatModel chatModel = builder.build();

        String prompt = new I18nPrompt(new Prompt()).render(locale, variables);

        LOGGER.info("Prompt: {}", prompt);

//        ChatRequest chatRequest = ChatRequest.builder()
//            .messages(UserMessage.from(prompt))
//            .build();

        return chatModel.chat(prompt);
    }

    @Override
    public void stream(Locale locale, Map<String, Object> variables, StreamingChatResponseHandler responseHandler) {
        OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder = OpenAiStreamingChatModel.builder()
            .baseUrl(modelConfig.getBaseUrl())
            .apiKey(modelConfig.getApiKey())
            .modelName(modelConfig.getModelName())
            .temperature(modelConfig.getTemperature())
            .maxTokens(modelConfig.getMaxTokens())
            .logRequests(true)
            .logResponses(true);

        if (requestParameters != null) {
            builder.defaultRequestParameters(requestParameters);
        }

        StreamingChatLanguageModel chatModel = builder.build();

        String prompt = new I18nPrompt(new Prompt()).render(locale, variables);

        LOGGER.info("Prompt: {}", prompt);

//        ChatRequest chatRequest = ChatRequest.builder()
//            .messages(UserMessage.from(prompt))
//            .build();

        chatModel.chat(prompt, responseHandler);
    }
}