package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.AccountService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {
    private final JSONStorageService jsonStorageService;

    public AccountServiceImpl(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
        initializeAccountCollection();
    }

    public void initializeAccountCollection() {
        if (!jsonStorageService.collectionExists(Account.class)) {
            jsonStorageService.initializeCollection(Account.class);
        }
    }

    @Override
    public void addAccount(Account account) {
        jsonStorageService.upsert(account);
    }

    @Override
    public void updateAccount(Account account) {
        jsonStorageService.update(account, Account.class);
    }

    @Override
    public void removeAccount(String accountId) {
        Optional<Account> account = jsonStorageService.findById(accountId, Account.class);
        account.ifPresent(value -> jsonStorageService.delete(value, Account.class));
    }

    @Override
    public List<Account> getAccountsByUserId(String userId) {
        List<Account> accounts = jsonStorageService.findAll(Account.class);
        return accounts.stream().filter(account -> account.getUserId().equals(userId)).toList();
    }

    @Override
    public double calculateNetWorth(String userId) {
        return 0;
    }

    @Override
    public double calculateMonthlySpending(String userId, Month month) throws ServiceException {
        List<TransactionDto> transactions = getTransactionsByUserId(userId);
        double totalSpending = 0;
        for (TransactionDto transaction : transactions) {
            if (transaction.getTime().getMonth().equals(month) && transaction.getIncomeOrExpense().equals("expense")) {
                totalSpending += Double.parseDouble(transaction.getAmount());
            }
        }
        return totalSpending;
    }

    @Override
    public double calculateMonthlyIncome(String userId, Month month) throws ServiceException {
        List<TransactionDto> transactions = getTransactionsByUserId(userId);
        double totalIncome = 0;
        for (TransactionDto transaction : transactions) {
            if (transaction.getTime().getMonth().equals(month) && transaction.getIncomeOrExpense().equals("income")) {
                totalIncome += Double.parseDouble(transaction.getAmount());
            }
        }
        return totalIncome;
    }

    @Override
    public double calculateCreditCardDue(String userId) {
        List<Account> accounts = getAccountsByUserId(userId);
        double totalDue = 0;
        for (Account account : accounts) {
            if (account.getAccountType().equals("credit")) {
                totalDue += account.getCurrentDebt();
            }
        }
        return totalDue;
    }

    @Override
    public LocalDateTime calculateNextDueDate(String userId) {
        List<Account> accounts = getAccountsByUserId(userId);
        LocalDateTime nextDueDate = LocalDateTime.MAX;
        for (Account account : accounts) {
            if (account.getAccountType().equals("credit")) {
                if (account.getPaymentDueDate().isBefore(nextDueDate)) {
                    nextDueDate = account.getPaymentDueDate();
                }
            }
        }
        return nextDueDate;
    }

    List<TransactionDto> getTransactionsByUserId(String userId) throws ServiceException {
        List<TransactionDto> transactions = jsonStorageService.findAll(TransactionDto.class);
        return transactions.stream().filter(transaction -> transaction.getUserId().equals(userId)).toList();
    }

//    @Override
//    public List<BillType>getBillTypeByAccountId(String accountId, String userId) {
//        List<TransactionDto> allTransactions = getTransactionsByUserId(userId);
//        return allTransactions.stream()
//                .filter(tx -> tx.getAccountId().equals(accountId))
//                .map(TransactionDto::getBillType)
//                .distinct()
//                .toList();
//    }
}
