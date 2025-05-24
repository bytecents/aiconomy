package com.se.aiconomy.server.langchain.service.chat;

import com.se.aiconomy.server.langchain.common.chain.BaseChain;
import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.common.prompt.I18nPrompt;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The {@code Chain} class extends {@link BaseChain} and provides methods to interact with
 * OpenAI chat models for both synchronous and streaming chat functionalities.
 * <p>
 * It builds and configures OpenAI chat models using the provided model configuration,
 * renders prompts with localization support, and logs the generated prompts.
 * </p>
 */
public class Chain extends BaseChain {
    private static final Logger log = LoggerFactory.getLogger(Chain.class);

    /**
     * Invokes the OpenAI chat model synchronously with the given locale and variables.
     *
     * @param locale    the locale to use for prompt localization
     * @param variables the variables to substitute in the prompt template
     * @return the response from the OpenAI chat model as a String
     */
    @Override
    public String invoke(Locale locale, Map<String, Object> variables) {
        OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
                .baseUrl(modelConfig.getBaseUrl())
                .apiKey(modelConfig.getApiKey())
                .modelName(String.valueOf(modelConfig.getModelName()))
                .temperature(modelConfig.getTemperature())
                .maxTokens(modelConfig.getMaxTokens())
                .logRequests(true)
                .logResponses(true);

        if (requestParameters != null) {
            builder.defaultRequestParameters(requestParameters);
        }

        OpenAiChatModel chatModel = builder.build();

        String prompt = new I18nPrompt(new Prompt()).render(locale, variables);

        log.info("Prompt: {}", prompt);

        return chatModel.chat(prompt);
    }

    /**
     * Invokes the OpenAI streaming chat model with the given locale and variables,
     * and handles the response using the provided {@link StreamingChatResponseHandler}.
     *
     * @param locale          the locale to use for prompt localization
     * @param variables       the variables to substitute in the prompt template
     * @param responseHandler the handler to process streaming chat responses
     */
    @Override
    public void stream(Locale locale, Map<String, Object> variables, StreamingChatResponseHandler responseHandler) {
        OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder builder = OpenAiStreamingChatModel.builder()
                .baseUrl(modelConfig.getBaseUrl())
                .apiKey(modelConfig.getApiKey())
                .modelName(String.valueOf(modelConfig.getModelName()))
                .temperature(modelConfig.getTemperature())
                .maxTokens(modelConfig.getMaxTokens())
                .logRequests(true)
                .logResponses(true);

        if (requestParameters != null) {
            builder.defaultRequestParameters(requestParameters);
        }

        StreamingChatLanguageModel chatModel = builder.build();

        String prompt = new I18nPrompt(new Prompt()).render(locale, variables);

        log.info("Prompt: {}", prompt);

        chatModel.chat(prompt, responseHandler);
    }
}