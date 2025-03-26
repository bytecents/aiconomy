package com.se.aiconomy.langchain.chain.chat;

import com.se.aiconomy.langchain.chains.chat.Chain;
import com.se.aiconomy.langchain.common.chain.ChainFactory;
import com.se.aiconomy.langchain.common.config.Configs;
import com.se.aiconomy.langchain.common.config.Locale;
import com.se.aiconomy.langchain.common.model.ModelConfig;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class ChatChainTest {
    private Chain chain;

    private static final Logger LOGGER = LogManager.getLogger(ChatChainTest.class);

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
                LOGGER.info("Complete response: {}", chatResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                fail("Streaming chat encountered an error: " + throwable.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        ChatChainTest test = new ChatChainTest();
        test.setUp();
        test.testStreamingChatChain();
    }
}
