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

/**
 * Unit tests for the {@link Chain} chat chain functionality.
 * <p>
 * This class tests both synchronous and streaming chat invocation using the Chain abstraction.
 * </p>
 */
class ChatChainTest {

    /**
     * Logger instance for this test class.
     */
    private static final Logger log = LoggerFactory.getLogger(ChatChainTest.class);

    /**
     * Chain instance used for testing.
     */
    private Chain chain;

    /**
     * Initializes the Chain instance before each test.
     */
    @BeforeEach
    void setUp() {
        chain = ChainFactory.createChain(Chain.class, new ModelConfig.Builder()
                .baseUrl(Configs.BASE_URL)
                .apiKey(Configs.API_KEY)
                .modelName(Configs.MODEL)
                .build()
        );
    }

    /**
     * Tests the synchronous invocation of the chat chain.
     * <p>
     * Invokes the chain with a sample input and asserts that the response is not null.
     * </p>
     */
    @Test
    void testInvokeChatChain() {
        String response = chain.invoke(Locale.CN, Map.of("code", "python"));
        log.info("Response: {}", response);
        assertNotNull(response, "Response should not be null");
    }

    /**
     * Tests the streaming invocation of the chat chain.
     * <p>
     * Invokes the chain in streaming mode and verifies that partial and complete responses are received.
     * </p>
     */
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
