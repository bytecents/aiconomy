package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.AccountService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {
    private final JSONStorageService jsonStorageService;

    public AccountServiceImpl(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
        initializeAccountCollection();
    }

    private void initializeAccountCollection() {
        if (!jsonStorageService.collectionExists(Account.class)) {
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
        List<TransactionDto> allTransactions = jsonStorageService.findAll(TransactionDto.class);
        return allTransactions.stream()
                .filter(tx -> tx.getUserId().equals(userId))
                .toList();
    }

    @Override
    public double getNumberOfAccounts(String userId) {
        return getAccountsByUserId(userId).size();
    }

    @Override
    public double calculateNetWorth(String userId) {
        double totalBalance = 0.0;
        double totalIncome = 0.0;
        double totalSpending = 0.0;

        for (Account account : getAccountsByUserId(userId)) {
            totalBalance += account.getBalance();
        }

        for (TransactionDto tx : getTransactionsByUserId(userId)) {
            if (Objects.equals(tx.getIncomeOrExpense(), "Income")) {
                totalIncome += Double.parseDouble(tx.getAmount());
            } else if (Objects.equals(tx.getIncomeOrExpense(), "Expense")) {
                totalSpending += Double.parseDouble(tx.getAmount());
            }
        }

        return totalBalance + totalIncome - totalSpending;
    }


    @Override
    public double calculateMonthlySpending(String userId) {
        List<TransactionDto> allTransactions = getTransactionsByUserId(userId);
        double total = 0.0;
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1);
        LocalDateTime lastDayOfMonth = LocalDateTime.now().withDayOfMonth(1).plusMonths(1).minusDays(1);

        for (TransactionDto tx : allTransactions) {
            if (Objects.equals(tx.getIncomeOrExpense(), "Expense") && tx.getTime().isAfter(firstDayOfMonth) && tx.getTime().isBefore(lastDayOfMonth)) {
                total += Double.parseDouble(tx.getAmount());
            }
        }
        return total;
    }

    @Override
    public double calculateMonthlyIncome(String userId) {
        List<TransactionDto> allTransactions = getTransactionsByUserId(userId);
        double total = 0.0;
        LocalDateTime firstDayOfMonth = LocalDateTime.now().withDayOfMonth(1);
        LocalDateTime lastDayOfMonth = LocalDateTime.now().withDayOfMonth(1).plusMonths(1).minusDays(1);

        for (TransactionDto tx : allTransactions) {
            if (Objects.equals(tx.getIncomeOrExpense(), "Income") && tx.getTime().isAfter(firstDayOfMonth) && tx.getTime().isBefore(lastDayOfMonth)) {
                total += Double.parseDouble(tx.getAmount());
            }
        }
        return total;
    }

    @Override
    public double calculateCreditDue(String userId) {
        List<Account> allAccounts = getAccountsByUserId(userId);
        double totalDue = 0.0;
        for (Account account : allAccounts) {
            if (account.getAccountType().equals("CreditCard")) {
                totalDue += account.getCurrentDebt();
            }
        }
        return totalDue; // 返回计算出的总欠款
    }

    @Override
    public LocalDateTime getLatestPaymentDueDate(String userId) {
        List<Account> allAccounts = getAccountsByUserId(userId);
        LocalDateTime latestPaymentDueDate = LocalDateTime.MAX; // 初始化为最大日期，以便后续比较
        for (Account account : allAccounts) {
            if (account.getAccountType().equals("CreditCard")) {
                LocalDateTime paymentDueDate = account.getPaymentDueDate();
                if (paymentDueDate.isBefore(latestPaymentDueDate)) {
                    latestPaymentDueDate = paymentDueDate;
                }
            }
        }
        return latestPaymentDueDate;
    }
}
