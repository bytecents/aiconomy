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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatService {
    private final ChatLanguageModel model = OpenAiChatModel.builder()
            .baseUrl(Configs.BASE_URL)
            .apiKey(Configs.API_KEY)
            .modelName(String.valueOf(Configs.MODEL))
            .build();
    private final StreamingChatLanguageModel streamingModel = OpenAiStreamingChatModel.builder()
            .baseUrl(Configs.BASE_URL)
            .apiKey(Configs.API_KEY)
            .modelName(String.valueOf(Configs.MODEL))
            .build();
    private final ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
    private final I18nPrompt prompt = new I18nPrompt(new Prompt());
    public String userId;

    public String chat(String prompt, Locale locale) {
        Assistant assistant = getAssistant(locale);
        return assistant.invoke(prompt);
    }

    public void stream(String prompt, Locale locale, Consumer<String> onPartialResponse, Consumer<ChatResponse> onCompleteResponse, Consumer<Throwable> onError) {
        Assistant assistant = getStreamingAssistant(locale);
        TokenStream tokenStream = assistant.stream(prompt);

        tokenStream.onPartialResponse(onPartialResponse)
                .onCompleteResponse(onCompleteResponse)
                .onError(onError)
                .start();
    }

    private Assistant getAssistant(Locale locale) {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .systemMessageProvider(obj -> (this.userId == null || this.userId.isEmpty()) ? getSystemMessage(locale) : getSystemMessage(locale, this.userId))
                .chatMemory(chatMemory)
                .tools(new Tools())
                .build();
    }

    private Assistant getStreamingAssistant(Locale locale) {
        return AiServices.builder(Assistant.class)
                .streamingChatLanguageModel(streamingModel)
                .systemMessageProvider(obj -> (this.userId == null || this.userId.isEmpty()) ? getSystemMessage(locale) : getSystemMessage(locale, this.userId))
                .chatMemory(chatMemory)
                .tools(new Tools())
                .build();
    }

    public void clearChatMemory() {
        chatMemory.clear();
    }

    private String getSystemMessage(Locale locale) {
        return prompt.getPrompt(locale);
    }

    private String getSystemMessage(Locale locale, String userId) {
        return prompt.render(locale, Map.of("user_id", userId));
    }
}
