package com.se.aiconomy.server.langchain.chat;

import com.se.aiconomy.server.langchain.common.chain.ChainFactory;
import com.se.aiconomy.server.langchain.common.config.Configs;
import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.common.config.ModelConfig;
import com.se.aiconomy.server.langchain.service.chat.Chain;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class ChatChainTest {
    private static final Logger log = LoggerFactory.getLogger(ChatChainTest.class);
    private Chain chain;

    @BeforeEach
    void setUp() {
        chain = ChainFactory.createChain(Chain.class, new ModelConfig.Builder()
            .baseUrl(Configs.BASE_URL)
            .apiKey(Configs.API_KEY)
            .modelName(Configs.MODEL)
            .build()
        );
    }

    @Test
    void testInvokeChatChain() {
        String response = chain.invoke(Locale.CN, Map.of("code", "python"));
        log.info("Response: {}", response);
        assertNotNull(response, "Response should not be null");
    }

    @Test
    void testStreamingChatChain() {
        chain.stream(Locale.CN, Map.of("code", "python"), new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String s) {
                assertNotNull(s, "Partial response should not be null");
            }

            @Override
            public void onCompleteResponse(ChatResponse chatResponse) {
                assertNotNull(chatResponse, "Chat response should not be null");
                log.info("Complete response: {}", chatResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                fail("Streaming stream encountered an error: " + throwable.getMessage());
            }
        });
    }
}
