package com.se.aiconomy.langchain.AIservices.classfication;

import com.se.aiconomy.langchain.AIServices.classification.Assistant;
import com.se.aiconomy.langchain.AIServices.classification.BillType;
import com.se.aiconomy.langchain.AIServices.classification.Prompt;
import com.se.aiconomy.langchain.model.Transaction;
import com.se.aiconomy.langchain.common.config.Configs;
import com.se.aiconomy.langchain.common.config.Locale;
import com.se.aiconomy.langchain.common.prompt.I18nPrompt;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ChatAssistantTest {
    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.builder()
            .baseUrl(Configs.BASE_URL)
            .apiKey(Configs.API_KEY)
            .modelName(Configs.MODEL)
            .build();
        Assistant assistant = AiServices.create(Assistant.class, model);

        Transaction transaction = new Transaction(
            LocalDateTime.now(), "消费", "Starbucks", "Cappuccino",
            "支出", "35.5", "信用卡", "成功",
            "TXN123456", "M123456", "早餐"
        );

        // 添加额外字段
        transaction.addExtraField("loyaltyPoints", 10);
        transaction.addExtraField("geoLocation", "Shanghai, China");

        // 构建 Jinja 变量
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
        System.out.println(prompt);

        BillType billType = assistant.classifyTransactionFrom(prompt);
        System.out.println(billType);
    }
}
