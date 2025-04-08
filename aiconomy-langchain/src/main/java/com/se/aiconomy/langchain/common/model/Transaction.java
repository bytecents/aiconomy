package com.se.aiconomy.langchain.common.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Transaction record class, supports fixed fields + extensible fields
 */
@Getter
@ToString
public class Transaction {

    @Setter
    @Description("The timestamp when the transaction occurred.")
    private LocalDateTime transactionTime;

    @Setter
    @Description("The type of the transaction (e.g., '消费' for consumption or '转账' for transfer).")
    private String transactionType;

    @Setter
    @Description("The counterparty involved in the transaction (e.g., 'Walmart', '支付宝').")
    private String counterparty;

    @Setter
    @Description("The name of the product purchased (e.g., 'iPhone 15').")
    private String product;

    @Setter
    @Description("Indicates whether the transaction is an income or expense ('收入' or '支出').")
    private String incomeOrExpense;

    @Setter
    @Description("The amount of the transaction in Yuan (CNY).")
    private String amount;

    @Setter
    @Description("The payment method used for the transaction (e.g., '微信支付', '信用卡').")
    private String paymentMethod;

    @Setter
    @Description("The current status of the transaction (e.g., '成功' for successful, '待支付' for pending payment).")
    private String status;

    @Setter
    @Description("The unique transaction identifier for tracking the transaction.")
    private String transactionId;

    @Setter
    @Description("The merchant's order ID, used for reference within the merchant's system.")
    private String merchantOrderId;

    @Setter
    @Description("Additional remarks or notes related to the transaction.")
    private String remark;

    @Description("A map to hold any extra, custom fields that may be included in the transaction.")
    private Map<String, Object> extraFields = new HashMap<>();

    public Transaction(LocalDateTime transactionTime, String transactionType, String counterparty,
                       String product, String incomeOrExpense, String amount, String paymentMethod,
                       String status, String transactionId, String merchantOrderId, String remark) {
        this.transactionTime = transactionTime;
        this.transactionType = transactionType;
        this.counterparty = counterparty;
        this.product = product;
        this.incomeOrExpense = incomeOrExpense;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionId = transactionId;
        this.merchantOrderId = merchantOrderId;
        this.remark = remark;
    }

    public void addExtraField(String key, Object value) {
        extraFields.put(key, value);
    }

    public Object getExtraField(String key) {
        return extraFields.get(key);
    }
}
