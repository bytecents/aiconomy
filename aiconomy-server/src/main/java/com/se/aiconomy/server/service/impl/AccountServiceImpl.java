package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.AccountService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the AccountService interface.
 * <p>
 * This class provides methods for managing accounts, calculating net worth,
 * monthly spending and income, credit card dues, and next due dates.
 * It interacts with a JSON-based storage service to persist and retrieve data.
 * </p>
 */
public class AccountServiceImpl implements AccountService {
    /**
     * The JSON storage service used for data persistence.
     */
    private final JSONStorageService jsonStorageService;

    /**
     * Constructs an AccountServiceImpl with the specified JSON storage service.
     *
     * @param jsonStorageService the JSON storage service to use
     */
    public AccountServiceImpl(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
        initializeAccountCollection();
    }

    /**
     * Initializes the account collection in the storage if it does not exist.
     */
    public void initializeAccountCollection() {
        if (!jsonStorageService.collectionExists(Account.class)) {
            jsonStorageService.initializeCollection(Account.class);
        }
    }

    /**
     * Adds a new account to the storage.
     *
     * @param account the account to add
     */
    @Override
    public void addAccount(Account account) {
        jsonStorageService.upsert(account);
    }

    /**
     * Updates an existing account in the storage.
     *
     * @param account the account to update
     */
    @Override
    public void updateAccount(Account account) {
        jsonStorageService.update(account, Account.class);
    }

    /**
     * Removes an account from the storage by its ID.
     *
     * @param accountId the ID of the account to remove
     */
    @Override
    public void removeAccount(String accountId) {
        Optional<Account> account = jsonStorageService.findById(accountId, Account.class);
        account.ifPresent(value -> jsonStorageService.delete(value, Account.class));
    }

    /**
     * Retrieves all accounts associated with a specific user ID.
     *
     * @param userId the user ID
     * @return a list of accounts belonging to the user
     */
    @Override
    public List<Account> getAccountsByUserId(String userId) {
        List<Account> accounts = jsonStorageService.findAll(Account.class);
        return accounts.stream().filter(account -> account.getUserId().equals(userId)).toList();
    }

    /**
     * Calculates the net worth for a user by summing account balances and transaction amounts.
     *
     * @param userId the user ID
     * @return the calculated net worth
     */
    @Override
    public double calculateNetWorth(String userId) {
        List<Account> accounts = getAccountsByUserId(userId);
        List<TransactionDto> transactions = getTransactionsByUserId(userId);
        double netWorth = 0;
        for (Account account : accounts) {
            netWorth += account.getBalance();
        }
        for (TransactionDto transaction : transactions) {
            if (transaction.getIncomeOrExpense().equals("Income")) {
                netWorth += Double.parseDouble(transaction.getAmount());
            } else if (transaction.getIncomeOrExpense().equals("Expense")) {
                netWorth -= Double.parseDouble(transaction.getAmount());
            }
        }
        return netWorth;
    }

    /**
     * Calculates the total spending for a user in a specific month.
     *
     * @param userId the user ID
     * @param month  the month to calculate spending for
     * @return the total spending amount
     */
    @Override
    public double calculateMonthlySpending(String userId, Month month) {
        List<TransactionDto> transactions = getTransactionsByUserId(userId);
        double totalSpending = 0;
        for (TransactionDto transaction : transactions) {
            if (transaction.getTime().getMonth().equals(month) && transaction.getIncomeOrExpense().equals("Expense")) {
                totalSpending += Double.parseDouble(transaction.getAmount());
            }
        }
        return totalSpending;
    }

    /**
     * Calculates the total income for a user in a specific month.
     *
     * @param userId the user ID
     * @param month  the month to calculate income for
     * @return the total income amount
     */
    @Override
    public double calculateMonthlyIncome(String userId, Month month) {
        List<TransactionDto> transactions = getTransactionsByUserId(userId);
        double totalIncome = 0;
        for (TransactionDto transaction : transactions) {
            if (transaction.getTime().getMonth().equals(month) && transaction.getIncomeOrExpense().equals("Income")) {
                totalIncome += Double.parseDouble(transaction.getAmount());
            }
        }
        return totalIncome;
    }

    /**
     * Calculates the total credit card due amount for a user.
     *
     * @param userId the user ID
     * @return the total credit card due amount
     */
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

    /**
     * Calculates the next payment due date for all credit accounts of a user.
     *
     * @param userId the user ID
     * @return the next due date, or LocalDateTime.MAX if none found
     */
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

    /**
     * Retrieves all transactions associated with a specific user ID.
     *
     * @param userId the user ID
     * @return a list of transactions belonging to the user
     */
    List<TransactionDto> getTransactionsByUserId(String userId) {
        List<TransactionDto> transactions = jsonStorageService.findAll(TransactionDto.class);
        return transactions.stream()
                .filter(transaction -> transaction.getUserId() != null && transaction.getUserId().equals(userId))
                .toList();
    }
}
