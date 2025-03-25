package com.se.aiconomy.service.langchain.AIServices;

import com.se.aiconomy.common.langchain.config.Configs;
import com.se.aiconomy.common.langchain.config.Locale;
import com.se.aiconomy.common.langchain.prompt.I18nPrompt;
import com.se.aiconomy.model.langchain.Transaction;
import com.se.aiconomy.service.langchain.AIServices.classification.Assistant;
import com.se.aiconomy.service.langchain.AIServices.classification.BillType;
import com.se.aiconomy.service.langchain.AIServices.classification.Prompt;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChatAssistantTest {
    private static final Logger log = LoggerFactory.getLogger(ChatAssistantTest.class);

    @Test
    public void testClassifyTransaction() {
        ChatLanguageModel model = OpenAiChatModel.builder()
            .baseUrl(Configs.BASE_URL)
            .apiKey(Configs.API_KEY)
            .modelName(Configs.MODEL)
            .build();
        Assistant assistant = AiServices.create(Assistant.class, model);

        Transaction transaction = new Transaction(
            LocalDateTime.now(), "Expense", "Starbucks", "Cappuccino",
            "Expense", "35.5", "Credit Card", "Success",
            "TXN123456", "M123456", "Breakfast"
        );

        // Add extra fields
        transaction.addExtraField("loyaltyPoints", 10);
        transaction.addExtraField("geoLocation", "Shanghai, China");

        // Build Jinja template context
        Map<String, Object> context = new HashMap<>();
        context.put("transaction_time", transaction.getTransactionTime());
        context.put("transaction_type", transaction.getTransactionType());
        context.put("counterparty", transaction.getCounterparty());
        context.put("product", transaction.getProduct());
        context.put("income_or_expense", transaction.getIncomeOrExpense());
        context.put("amount", transaction.getAmount());
        context.put("currency", "CNY");
        context.put("payment_method", transaction.getPaymentMethod());
        context.put("status", transaction.getStatus());
        context.put("transaction_id", transaction.getTransactionId());
        context.put("merchant_order_id", transaction.getMerchantOrderId());
        context.put("remark", transaction.getRemark());
        context.put("extra_fields", transaction.getExtraFields());

        String prompt = new I18nPrompt(new Prompt()).render(Locale.EN, context);
        assertNotNull(prompt);

        BillType billType = assistant.classifyTransactionFrom(prompt);
        assertNotNull(billType);
        log.info("Bill Type: {}", billType);
    }
}
