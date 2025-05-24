package com.se.aiconomy.server.langchain.common.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Represents a transaction record, supporting both fixed and extensible fields.
 * This class encapsulates details about a financial transaction, such as the transaction type,
 * counterparty, product, amount, currency, payment method, status, and additional remarks.
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class Transaction {
    /**
     * The unique identifier for the transaction record.
     */
    @Description("The unique identifier for the transaction record.")
    private String id;

    /**
     * The timestamp when the transaction occurred.
     */
    @Description("The timestamp when the transaction occurred.")
    private LocalDateTime time;

    /**
     * The type of the transaction (e.g., '消费' for consumption or '转账' for transfer).
     */
    @Description("The type of the transaction (e.g., '消费' for consumption or '转账' for transfer).")
    private String type;

    /**
     * The counterparty involved in the transaction (e.g., 'Walmart', '支付宝').
     */
    @Description("The counterparty involved in the transaction (e.g., 'Walmart', '支付宝').")
    private String counterparty;

    /**
     * The name of the product purchased (e.g., 'iPhone 15').
     */
    @Description("The name of the product purchased (e.g., 'iPhone 15').")
    private String product;

    /**
     * Indicates whether the transaction is an income or expense ('收入' or '支出').
     */
    @Description("Indicates whether the transaction is an income or expense ('收入' or '支出').")
    private String incomeOrExpense;

    /**
     * The amount of the transaction.
     */
    @Description("The amount of the transaction.")
    private String amount;

    /**
     * Currency used in the transaction (e.g., 'CNY', 'USD').
     */
    @Description("Currency used in the transaction (e.g., 'CNY', 'USD').")
    private String currency;

    /**
     * The payment method used for the transaction (e.g., '微信支付', '信用卡').
     */
    @Description("The payment method used for the transaction (e.g., '微信支付', '信用卡').")
    private String paymentMethod;

    /**
     * The current status of the transaction (e.g., '成功' for successful, '待支付' for pending payment).
     */
    @Description("The current status of the transaction (e.g., '成功' for successful, '待支付' for pending payment).")
    private String status;

    /**
     * The merchant's order ID, used for reference within the merchant's system.
     */
    @Description("The merchant's order ID, used for reference within the merchant's system.")
    private String merchantOrderId;

    /**
     * Account ID associated with the transaction.
     */
    private String accountId; // Account ID

    /**
     * Additional remarks or notes related to the transaction.
     */
    @Description("Additional remarks or notes related to the transaction.")
    private String remark;
}