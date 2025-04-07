package com.se.aiconomy.langchain.service.classification;

import com.se.aiconomy.langchain.common.config.Configs;
import com.se.aiconomy.langchain.common.config.Locale;
import com.se.aiconomy.langchain.common.model.BillType;
import com.se.aiconomy.langchain.common.prompt.I18nPrompt;
import com.se.aiconomy.langchain.common.model.Transaction;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TransactionClassificationService {
    private static final Logger log = LoggerFactory.getLogger(TransactionClassificationService.class);
    private final Assistant assistant;

    public TransactionClassificationService() {
        ChatLanguageModel model = OpenAiChatModel.builder()
            .baseUrl(Configs.BASE_URL)
            .apiKey(Configs.API_KEY)
            .modelName(Configs.MODEL)
            .build();
        this.assistant = AiServices.create(Assistant.class, model);
    }

    public BillType classifyTransaction(Transaction transaction) {
        return classifyTransaction(transaction, Locale.EN);
    }

    public BillType classifyTransaction(Transaction transaction, Locale locale) {
        Map<String, Object> context = buildContext(transaction);
        String prompt = new I18nPrompt(new Prompt()).render(locale, context);
        log.info("Classification Prompt: {}", prompt);
        return assistant.classifyTransactionFrom(prompt);
    }

    @NotNull
    private Map<String, Object> buildContext(@NotNull Transaction transaction) {
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

        log.info("Transaction Context: {}", context);
        return context;
    }
}
