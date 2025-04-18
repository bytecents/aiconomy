package com.se.aiconomy.server.model.entity;

import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Document(collection = "Accounts", schemaVersion = "1.0")
public class Account implements Identifiable {
    @Id
    private String id; // 用户账户ID
    private String totalSavings; // 用户所有账户的总金额
    private String availableAmount; // 用户可用金额
    private String numAccounts; // 用户账户数量
    private String totalExpense; // 用户所有账户的总支出
    private String totalIncome; // 用户所有账户的总收入
    private String totalBudget; // 用户所有账户的总预算
    private List<BankAccount> accounts;

    private Map<String, Object> extraFields = new HashMap<>();

    public Account() {}

    public Account(String id, String totalSavings, String availableAmount, String numAccounts, String totalExpense, String totalIncome, String totalBudget, List<BankAccount> accounts) {
        this.id = id;
        this.totalSavings = totalSavings;
        this.availableAmount = availableAmount;
        this.numAccounts = numAccounts;
        this.totalExpense = totalExpense;
        this.totalIncome = totalIncome;
        this.totalBudget = totalBudget;
        this.accounts = accounts;
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
