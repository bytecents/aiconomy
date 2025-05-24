package com.se.aiconomy.server.langchain.service.classification;

import com.se.aiconomy.server.langchain.common.config.Configs;
import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
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

/**
 * Service for classifying financial transactions into dynamic bill types using AI models.
 * <p>
 * This service leverages an AI assistant to classify single or multiple transactions
 * based on their details and the specified locale. It supports internationalization
 * and can generate prompts in different languages for classification.
 * </p>
 */
public class TransactionClassificationService {
    private static final Logger log = LoggerFactory.getLogger(TransactionClassificationService.class);
    private final Assistant assistant;

    /**
     * Constructs a new TransactionClassificationService with an AI-powered assistant.
     * The assistant is created using the OpenAI chat model and configured with
     * application-specific settings.
     */
    public TransactionClassificationService() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl(Configs.BASE_URL)
                .apiKey(Configs.API_KEY)
                .modelName(String.valueOf(Configs.MODEL))
                .build();
        this.assistant = AiServices.create(Assistant.class, model);
    }

    /**
     * Classifies a single transaction into a dynamic bill type using the specified locale.
     *
     * @param transaction the transaction to classify
     * @param locale      the locale for prompt internationalization
     * @return the classified {@link DynamicBillType}
     */
    public DynamicBillType classifyTransaction(Transaction transaction, Locale locale) {
        Map<String, Object> context = buildContext(transaction);
        String prompt = new I18nPrompt(new Prompt()).render(locale, context);
        log.info("Classification Prompt: {}", prompt);
        return assistant.classifyTransactionFrom(prompt);
    }

    /**
     * Classifies a single transaction into a dynamic bill type using the default locale (EN).
     *
     * @param transaction the transaction to classify
     * @return the classified {@link DynamicBillType}
     */
    public DynamicBillType classifyTransaction(Transaction transaction) {
        return classifyTransaction(transaction, Locale.EN);
    }

    /**
     * Classifies a list of transactions into dynamic bill types using the default locale (EN).
     *
     * @param transactions the list of transactions to classify
     * @return a list of classified {@link DynamicBillType} objects
     */
    public List<DynamicBillType> classifyTransactions(List<Transaction> transactions) {
        return classifyTransactions(transactions, Locale.EN);
    }

    /**
     * Classifies a list of transactions into dynamic bill types using the specified locale.
     *
     * @param transactions the list of transactions to classify
     * @param locale       the locale for prompt internationalization
     * @return a list of classified {@link DynamicBillType} objects
     */
    public List<DynamicBillType> classifyTransactions(List<Transaction> transactions, Locale locale) {
        return new ArrayList<>(transactions.parallelStream()
                .map(transaction -> classifyTransaction(transaction, locale != null ? locale : Locale.EN))
                .toList());
    }

    /**
     * Builds a context map from a transaction for prompt rendering.
     *
     * @param transaction the transaction to extract context from
     * @return a map containing transaction details for prompt rendering
     */
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