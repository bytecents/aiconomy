package com.se.aiconomy.server.langchain.service.chat;

import com.se.aiconomy.server.langchain.common.config.Configs;
import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.common.prompt.I18nPrompt;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.*;

import java.util.Map;
import java.util.function.Consumer;

/**
 * ChatService provides methods to interact with AI chat models, supporting both synchronous and streaming chat functionalities.
 * It manages chat memory, prompt localization, and user context, and allows for clearing chat history.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatService {
    /**
     * The synchronous chat language model instance.
     */
    private final ChatLanguageModel model = OpenAiChatModel.builder()
            .baseUrl(Configs.BASE_URL)
            .apiKey(Configs.API_KEY)
            .modelName(String.valueOf(Configs.MODEL))
            .build();

    /**
     * The streaming chat language model instance.
     */
    private final StreamingChatLanguageModel streamingModel = OpenAiStreamingChatModel.builder()
            .baseUrl(Configs.BASE_URL)
            .apiKey(Configs.API_KEY)
            .modelName(String.valueOf(Configs.MODEL))
            .build();

    /**
     * The chat memory for storing recent messages.
     */
    private final ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);

    /**
     * The internationalized prompt template.
     */
    private final I18nPrompt prompt = new I18nPrompt(new Prompt());

    /**
     * The user ID associated with the chat session.
     */
    public String userId;

    /**
     * Sends a prompt to the AI assistant and returns the response as a String.
     *
     * @param prompt the user prompt to send
     * @param locale the locale for prompt localization
     * @return the assistant's response as a String
     */
    public String chat(String prompt, Locale locale) {
        Assistant assistant = getAssistant(locale);
        return assistant.invoke(prompt);
    }

    /**
     * Sends a prompt to the AI assistant and handles streaming responses.
     *
     * @param prompt             the user prompt to send
     * @param locale             the locale for prompt localization
     * @param onPartialResponse  consumer for partial responses
     * @param onCompleteResponse consumer for the complete response
     * @param onError            consumer for handling errors
     */
    public void stream(String prompt, Locale locale, Consumer<String> onPartialResponse, Consumer<ChatResponse> onCompleteResponse, Consumer<Throwable> onError) {
        Assistant assistant = getStreamingAssistant(locale);
        TokenStream tokenStream = assistant.stream(prompt);

        tokenStream.onPartialResponse(onPartialResponse)
                .onCompleteResponse(onCompleteResponse)
                .onError(onError)
                .start();
    }

    /**
     * Creates an Assistant instance for synchronous chat.
     *
     * @param locale the locale for prompt localization
     * @return an Assistant instance
     */
    private Assistant getAssistant(Locale locale) {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .systemMessageProvider(obj -> (this.userId == null || this.userId.isEmpty()) ? getSystemMessage(locale) : getSystemMessage(locale, this.userId))
                .chatMemory(chatMemory)
                .tools(new Tools())
                .build();
    }

    /**
     * Creates an Assistant instance for streaming chat.
     *
     * @param locale the locale for prompt localization
     * @return an Assistant instance
     */
    private Assistant getStreamingAssistant(Locale locale) {
        return AiServices.builder(Assistant.class)
                .streamingChatLanguageModel(streamingModel)
                .systemMessageProvider(obj -> (this.userId == null || this.userId.isEmpty()) ? getSystemMessage(locale) : getSystemMessage(locale, this.userId))
                .chatMemory(chatMemory)
                .tools(new Tools())
                .build();
    }

    /**
     * Clears the chat memory.
     */
    public void clearChatMemory() {
        chatMemory.clear();
    }

    /**
     * Gets the system message for the specified locale.
     *
     * @param locale the locale for prompt localization
     * @return the system message as a String
     */
    private String getSystemMessage(Locale locale) {
        return prompt.getPrompt(locale);
    }

    /**
     * Gets the system message for the specified locale and user ID.
     *
     * @param locale the locale for prompt localization
     * @param userId the user ID to include in the prompt
     * @return the system message as a String
     */
    private String getSystemMessage(Locale locale, String userId) {
        return prompt.render(locale, Map.of("user_id", userId));
    }
}