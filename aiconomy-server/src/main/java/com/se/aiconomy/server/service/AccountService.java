package com.se.aiconomy.server.service;

import com.se.aiconomy.server.model.entity.Account;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountService {
    void addBankAccount(Account account);
    void updateAccount(Account account);

    void deleteAccount(String accountId);

    Account getAccountById(String accountId);

    List<Account> getAccountsByUserId(String userId);


    double getNumberOfAccounts(String userId); // Account界面上面的ActiveAccounts

    double calculateNetWorth(String userId); // Dashboard界面上面的NetWorth

    double calculateMonthlySpending(String userId); // Dashboard界面上面的MonthlySpending

    double calculateMonthlyIncome(String userId); // Dashboard界面上面的MonthlyIncome

    double calculateCreditDue(String userId); // Dashboard界面上面的CreditCardDue

    LocalDateTime getLatestPaymentDueDate(String userId); // Dashboard界面上面的CreditCardDue的日期
}
