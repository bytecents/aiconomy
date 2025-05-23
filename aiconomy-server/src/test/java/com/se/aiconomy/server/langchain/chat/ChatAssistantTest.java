package com.se.aiconomy.server.langchain.chat;

import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.service.chat.ChatService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatAssistantTest {

    private static final Logger log = LoggerFactory.getLogger(ChatAssistantTest.class);
    private static ChatService chatService;

    @BeforeAll
    static void setUp() {
        chatService = new ChatService("1");
    }

    @Test
    @Order(1)
    void testChat() {
        String response = chatService.chat("用户 1 的账单信息如何？", Locale.EN);
        log.info("Response: {}", response);
        assertNotNull(response, "Response should not be null");
    }

    @Test
    @Order(2)
    void testStream() {
        AtomicBoolean onPartialResponseCalled = new AtomicBoolean(false);
        AtomicBoolean onCompleteResponseCalled = new AtomicBoolean(false);

        CompletableFuture<String> future = new CompletableFuture<>();

        chatService.stream(
                "用户 1 的预算如何？",
                Locale.EN,
                partialResponse -> {
                    log.info("Partial response: {}", partialResponse);
                    assertNotNull(partialResponse, "Partial response should not be null");
                    onPartialResponseCalled.set(true);
                },
                completeResponse -> {
                    log.info("Complete response: {}", completeResponse);
                    assertNotNull(completeResponse, "Complete response should not be null");
                    onCompleteResponseCalled.set(true);
                    future.complete(String.valueOf(completeResponse));
                },
                future::completeExceptionally
        );

        try {
            String result = future.get(120, TimeUnit.SECONDS);

            assertNotNull(result, "Complete response should not be null");
            assertTrue(onPartialResponseCalled.get(), "onPartialResponse should be called");
            assertTrue(onCompleteResponseCalled.get(), "onCompleteResponse should be called");
        } catch (TimeoutException e) {
            fail("Stream operation timed out after 10 seconds");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test was interrupted");
        } catch (ExecutionException e) {
            fail("Stream operation failed: " + e.getCause().getMessage());
        }
    }

    @Test
    @Order(3)
    void testContinuousChat() {
        String response = chatService.chat("What did I said to you?", Locale.EN);
        log.info("Response: {}", response);
    }

    @Test
    @Order(4)
    void testClearChatMemory() {
        chatService.clearChatMemory();
        String response = chatService.chat("What did I said to you?", Locale.EN);
        log.info("Response: {}", response);
    }
}
