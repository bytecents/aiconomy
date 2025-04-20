package com.se.aiconomy.server.model.entity;

import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@Document(collection = "BankAccounts", schemaVersion = "1.0")
public class Account implements Identifiable {
    @Id
    private String id; // 账户ID
    private String userId; // 用户ID
    private String bankName; // 银行名称
    private String accountType; //账户类型
    private String accountName; // 账户名称
    private double balance; // 余额
    // 信用卡专有属性（我懒得再写一个实体类了）
    private double creditLimit; // 信用卡的额度
    private double currentDebt; // 当前欠款
    private LocalDateTime paymentDueDate; // 还款期限
    private double minimumPayment; // 最低还款额度

    private Map<String, Object> extraFields = new HashMap<>();

    public Account() {
    }

    public Account(String id, String userId, String bankName, String accountType, String accountName, double balance) {
        this.id = id;
        this.userId = userId;
        this.bankName = bankName;
        this.accountType = accountType;
        this.accountName = accountName;
        this.balance = balance;
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
