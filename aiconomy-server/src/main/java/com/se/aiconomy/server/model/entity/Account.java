package com.se.aiconomy.server.model.entity;

import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@Document(collection = "BankAccounts", schemaVersion = "1.0")
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Identifiable {
    @Id
    private String id; // 账户ID
    private String userId; // 用户ID
    private String bankName; // 银行名称
    private String accountType; //账户类型（Checking, Savings, CreditCard）
    private String accountName; // 账户名称（e.g. Primary Checking）
    private double balance; // 余额（添加账户时银行卡的初始余额）
    // 信用卡专有属性
    private double creditLimit; // 信用卡的额度
    private double currentDebt; // 当前欠款
    private LocalDateTime paymentDueDate; // 还款期限
    private double minimumPayment; // 最低还款额度
}
