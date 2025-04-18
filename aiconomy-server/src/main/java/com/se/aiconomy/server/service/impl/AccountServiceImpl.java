package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.AccountService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.util.List;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {
    private final JSONStorageService jsonStorageService;

    public AccountServiceImpl(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
        initializeAccountCollection();
    }

    private void initializeAccountCollection() {
        if(!jsonStorageService.collectionExists(Account.class)) {
            jsonStorageService.initializeCollection(Account.class);
        }
    }

    @Override
    public void addBankAccount(Account account) {
        jsonStorageService.insert(account);
    }

    @Override
    public void updateAccount(Account account) {
        jsonStorageService.update(account, Account.class);
    }

    @Override
    public void deleteAccount(String accountId) {
        Account account = getAccountById(accountId);
        jsonStorageService.delete(account, Account.class);
    }

    @Override
    public Account getAccountById(String accountId) {
        Optional<Account> account = jsonStorageService.findById(accountId, Account.class);
        return account.orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
    }

    @Override
    public List<Account> getAccountsByUserId(String userId) {
        List<Account> allAccounts = jsonStorageService.findAll(Account.class);
        return allAccounts.stream().filter(account -> account.getUserId().equals(userId)).toList();
    }

    private List<TransactionDto> getTransactionsByUserId(String userId) {
        return null;
    }

    @Override
    public double getTotalBalance(String userId) {

        return 0;
    }

    @Override
    public double getNumberOfAccounts(String userId) {
        return getAccountsByUserId(userId).size();
    }

    @Override
    public double calculateNetWorth(String userId) {
        double totalBalance = getTotalBalance(userId);  // 总余额
        double totalIncome = 0.0;
        double totalSpending = 0.0;

        // 遍历用户所有账户的交易记录
//        for (Account account : getAccountsByUserId(userId)) {
//            for (Transaction transaction : transactions.getTransactions(userId)) {
//                if (transaction.getType() == TransactionType.INCOME) {
//                    totalIncome += transaction.getAmount(); // 收入
//                } else if (transaction.getType() == TransactionType.EXPENSE) {
//                    totalSpending += transaction.getAmount(); // 支出
//                }
//            }
//        }

        return totalBalance + totalIncome - totalSpending;  // 净资产 = 余额 + 收入 - 支出
    }

    @Override
    public double calculateMonthlySpending(String userId) {
        return 0;
    }

    @Override
    public double calculateMonthlyIncome(String userId) {
        return 0;
    }

    @Override
    public double calculateCreditDue(String userId) {
        return 0;
    }
}
