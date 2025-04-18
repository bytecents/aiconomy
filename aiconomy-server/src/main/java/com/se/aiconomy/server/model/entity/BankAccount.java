package com.se.aiconomy.server.model.entity;

import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@Document(collection = "BankAccounts", schemaVersion = "1.0")
public class BankAccount implements Identifiable {
    @Id
    private String id; // 账户ID
    private String bankName; // 银行名称
    private String accountType; //账户类型
    private String accountName; // 账户名称
    private String expense; // 支出
    private String income; // 收入
    private String balance; // 余额

    private Map<String, Object> extraFields = new HashMap<>();

    public BankAccount() {
    }

    public BankAccount(String id, String bankName, String accountType, String accountName, String expense, String income, String balance) {
        this.id = id;
        this.bankName = bankName;
        this.accountType = accountType;
        this.accountName = accountName;
        this.expense = expense;
        this.income = income;
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
