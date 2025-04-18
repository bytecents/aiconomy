package com.se.aiconomy.server.service;

import com.se.aiconomy.server.model.entity.Account;

import java.util.List;

public interface AccountService {
    void addBankAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(Long accountId);
    Account getAccountById(Long accountId);
    List<Account> getAllAccounts();
    List<Account> getAccountsByUserId(Long userId);
    void transferMoney(Long fromBankAccountId, Long toBankAccountId, double amount);
    void depositMoney(Long bankAccountId, double amount);
    void withdrawMoney(Long bankAccountId, double amount);
    double checkBalance(Long bankAccountId);
}
