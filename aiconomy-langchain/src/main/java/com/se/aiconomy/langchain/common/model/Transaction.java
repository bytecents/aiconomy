package com.se.aiconomy.langchain.common.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Transaction record class, supports fixed fields + extensible fields
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class Transaction {
    @Description("The unique identifier for the transaction record.")
    private String id;

    @Description("The timestamp when the transaction occurred.")
    private LocalDateTime time;

    @Description("The type of the transaction (e.g., '消费' for consumption or '转账' for transfer).")
    private String type;

    @Description("The counterparty involved in the transaction (e.g., 'Walmart', '支付宝').")
    private String counterparty;

    @Description("The name of the product purchased (e.g., 'iPhone 15').")
    private String product;

    @Description("Indicates whether the transaction is an income or expense ('收入' or '支出').")
    private String incomeOrExpense;

    @Description("The amount of the transaction.")
    private String amount;

    @Description("Currency used in the transaction (e.g., 'CNY', 'USD').")
    private String currency;

    @Description("The payment method used for the transaction (e.g., '微信支付', '信用卡').")
    private String paymentMethod;

    @Description("The current status of the transaction (e.g., '成功' for successful, '待支付' for pending payment).")
    private String status;

    @Description("The merchant's order ID, used for reference within the merchant's system.")
    private String merchantOrderId;

    @Description("Additional remarks or notes related to the transaction.")
    private String remark;
}
