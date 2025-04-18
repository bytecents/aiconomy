package com.se.aiconomy.server.service;

import com.se.aiconomy.server.model.entity.Account;

import java.util.List;

public interface AccountService {
    void addBankAccount(Account account);
    void updateAccount(Account account);

    void deleteAccount(String accountId);

    Account getAccountById(String accountId);
    List<Account> getAllAccounts();

    List<Account> getAccountsByUserId(String userId);

    double getTotalBalance(); // Account界面上面的TotalBalance

    double getNumberOfAccounts(); // Account界面上面的ActiveAccounts

    double calculateNetWorth(); // Dashboard界面上面的NetWorth

    double calculateMonthlySpending(); // Dashboard界面上面的MonthlySpending

    double calculateMonthlyIncome(); // Dashboard界面上面的MonthlyIncome

    double calculateCreditDue(); // Dashboard界面上面的CreditCardDue
}
