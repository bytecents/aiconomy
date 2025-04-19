package com.se.aiconomy.server.langchain.service.classification;

import com.se.aiconomy.server.langchain.common.config.Configs;
import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.langchain.common.prompt.I18nPrompt;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionClassificationService {
    private static final Logger log = LoggerFactory.getLogger(TransactionClassificationService.class);
    private final Assistant assistant;

    public TransactionClassificationService() {
        ChatLanguageModel model = OpenAiChatModel.builder()
            .baseUrl(Configs.BASE_URL)
            .apiKey(Configs.API_KEY)
            .modelName(String.valueOf(Configs.MODEL))
            .build();
        this.assistant = AiServices.create(Assistant.class, model);
    }

    public BillType classifyTransaction(Transaction transaction, Locale locale) {
        Map<String, Object> context = buildContext(transaction);
        String prompt = new I18nPrompt(new Prompt()).render(locale, context);
        log.info("Classification Prompt: {}", prompt);
        return assistant.classifyTransactionFrom(prompt);
    }

    public BillType classifyTransaction(Transaction transaction) {
        return classifyTransaction(transaction, Locale.EN);
    }

    public List<BillType> classifyTransactions(List<Transaction> transactions) {
        return classifyTransactions(transactions, Locale.EN);
    }

    public List<BillType> classifyTransactions(List<Transaction> transactions, Locale locale) {
        return new ArrayList<>(transactions.parallelStream()
            .map(transaction -> classifyTransaction(transaction, locale != null ? locale : Locale.EN))
            .toList());
    }

    @NotNull
    private Map<String, Object> buildContext(@NotNull Transaction transaction) {
        Map<String, Object> context = new HashMap<>();
        context.put("id", transaction.getId());
        context.put("time", transaction.getTime());
        context.put("type", transaction.getType());
        context.put("counterparty", transaction.getCounterparty());
        context.put("product", transaction.getProduct());
        context.put("income_or_expense", transaction.getIncomeOrExpense());
        context.put("amount", transaction.getAmount());
        context.put("currency", "CNY");
        context.put("payment_method", transaction.getPaymentMethod());
        context.put("status", transaction.getStatus());
        context.put("merchant_order_id", transaction.getMerchantOrderId());
        context.put("remark", transaction.getRemark());

        log.info("Transaction Context: {}", context);
        return context;
    }
}
