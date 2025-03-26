package com.se.aiconomy.server.model.entity;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 交易记录类，支持固定字段 + 可扩展字段
 */
@Document(collection = "Transactions", schemaVersion = "1.0")
public class Transaction {
    private LocalDateTime transactionTime;  // 交易时间
    private String transactionType;  // 交易类型 (如 "消费", "转账")
    private String counterparty;  // 交易对方 (如 "Walmart", "支付宝")
    private String product;  // 商品名称 (如 "iPhone 15")
    private String incomeOrExpense;  // 收/支 ("收入" 或 "支出")
    private String amount;  // 金额（元）
    private String paymentMethod;  // 支付方式 (如 "微信支付", "信用卡")
    private String status;  // 交易状态 (如 "成功", "待支付")

    @Id
    private String transactionId;  // 交易单号
    private String merchantOrderId;  // 商户单号
    private String remark;  // 备注

    private Map<String, Object> extraFields = new HashMap<>();

    public Transaction() {

    }

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

    // 添加扩展字段
    public void addExtraField(String key, Object value) {
        extraFields.put(key, value);
    }

    // 获取扩展字段
    public Object getExtraField(String key) {
        return extraFields.get(key);
    }

    // Getters and Setters
    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getIncomeOrExpense() {
        return incomeOrExpense;
    }

    public void setIncomeOrExpense(String incomeOrExpense) {
        this.incomeOrExpense = incomeOrExpense;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Map<String, Object> getExtraFields() {
        return extraFields;
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "transactionTime='" + transactionTime + '\'' +
            ", transactionType='" + transactionType + '\'' +
            ", counterparty='" + counterparty + '\'' +
            ", product='" + product + '\'' +
            ", incomeOrExpense='" + incomeOrExpense + '\'' +
            ", amount='" + amount + '\'' +
            ", paymentMethod='" + paymentMethod + '\'' +
            ", status='" + status + '\'' +
            ", transactionId='" + transactionId + '\'' +
            ", merchantOrderId='" + merchantOrderId + '\'' +
            ", remark='" + remark + '\'' +
            ", extraFields=" + extraFields +
            '}';
    }
}