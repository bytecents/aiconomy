package com.se.aiconomy.server.model.entity;

import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import io.jsondb.annotation.Secret;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 交易记录类，支持固定字段 + 可扩展字段
 */
@Getter
@ToString
@Document(collection = "Transactions", schemaVersion = "1.0")
public class Transaction implements Identifiable {
    @Id
    @Setter
    private String id;
    // Getters and Setters
    @Setter
    private LocalDateTime time;  // 交易时间
    @Setter
    private String type;  // 交易类型 (如 "消费", "转账")
    @Setter
    private String counterparty;  // 交易对象 (如 "Walmart", "支付宝")
    @Setter
    private String product;  // 商品名称 (如 "iPhone 15")
    @Setter
    private String incomeOrExpense;  // 收/支 ("收入" 或 "支出")
    @Setter
    private String amount;  // 金额（元）
    @Setter
    private String paymentMethod;  // 支付方式 (如 "微信支付", "信用卡")
    @Setter
    private String status;  // 交易状态 (如 "成功", "待支付")

    @Setter
    private String merchantOrderId;  // 商户单号
    @Setter
    private String remark;  // 备注

    @Secret
    private String privateKey;

    private Map<String, Object> extraFields = new HashMap<>();

    public Transaction() {

    }

    public Transaction(LocalDateTime transactionTime, String transactionType, String counterparty,
                       String product, String incomeOrExpense, String amount, String paymentMethod,
                       String status, String transactionId, String merchantOrderId, String remark) {
        this.time = transactionTime;
        this.type = transactionType;
        this.counterparty = counterparty;
        this.product = product;
        this.incomeOrExpense = incomeOrExpense;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
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
}