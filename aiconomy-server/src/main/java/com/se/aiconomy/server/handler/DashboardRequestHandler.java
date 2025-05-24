package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.AccountService;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.service.impl.AccountServiceImpl;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Month;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handler for dashboard-related requests, providing methods to retrieve user financial data,
 * transaction records, budget spending ratios, and account information.
 */
public class DashboardRequestHandler {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;

    /**
     * Constructs a DashboardRequestHandler with the specified services.
     *
     * @param accountService     the account service
     * @param transactionService the transaction service
     * @param budgetService      the budget service
     */
    public DashboardRequestHandler(AccountService accountService, TransactionService transactionService, BudgetService budgetService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
    }

    /**
     * Default constructor initializing services with default implementations.
     */
    private DashboardRequestHandler() {
        JSONStorageService jsonStorageService = JSONStorageServiceImpl.getInstance();
        this.accountService = new AccountServiceImpl(jsonStorageService);
        this.transactionService = new TransactionServiceImpl();
        this.budgetService = new BudgetServiceImpl(jsonStorageService);
    }

    /**
     * Retrieves the user's net worth, monthly spending, monthly income, and credit card due for a given month.
     *
     * @param userId the user ID
     * @param month  the month to query
     * @return a DashboardData object containing net worth, monthly spending, monthly income, and credit card due
     * @throws ServiceException if any error occurs during the query
     */
    public DashboardData getDashboardData(String userId, Month month) throws ServiceException {
        double netWorth = accountService.calculateNetWorth(userId);
        double monthlySpending = accountService.calculateMonthlySpending(userId, month);
        double monthlyIncome = accountService.calculateMonthlyIncome(userId, month);
        double creditCardDue = accountService.calculateCreditCardDue(userId);

        return new DashboardData(netWorth, monthlySpending, monthlyIncome, creditCardDue);
    }

    /**
     * Retrieves the transaction records for a specific account of a user.
     *
     * @param userId    the user ID
     * @param accountId the account ID
     * @return a list of TransactionDto containing the transaction records
     * @throws ServiceException if any error occurs during the query
     */
    public List<TransactionDto> getTransactionsByAccountId(String userId, String accountId) throws ServiceException {
        return transactionService.getTransactionsByAccountId(accountId, userId);
    }

    /**
     * Retrieves the spending/budget ratio for each budget category of a user, sorted in descending order.
     *
     * @param userId the user ID
     * @return a map containing the spending/budget ratio for each budget category, sorted in descending order
     * @throws ServiceException if any error occurs during processing
     */
    public Map<String, Double> getBudgetSpendingRatio(String userId) throws ServiceException {
        // Get all budgets related to the user
        List<Budget> budgets = budgetService.getBudgetsByUserId(userId);

        // Calculate the spending/budget ratio for each category
        Map<String, Double> spendingRatios = new HashMap<>();
        for (Budget budget : budgets) {
            String category = budget.getBudgetCategory();
            double totalBudget = budgetService.getTotalBudgetByCategory(userId, category);
            double totalSpent = budgetService.getTotalSpentByCategory(userId, category);

            // Prevent division by zero
            double spendingRatio = (totalBudget != 0) ? totalSpent / totalBudget : 0.0;
            spendingRatios.put(category, spendingRatio);
        }

        // Sort by ratio in descending order
        return spendingRatios.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Retrieves the accounts for a user. If there are more than two accounts, only the first two are returned.
     *
     * @param userId the user ID
     * @return a list of accounts, containing at most two accounts
     * @throws ServiceException if any error occurs during the query
     */
    public List<Account> getAccountsForUser(String userId) throws ServiceException {
        List<Account> accounts = accountService.getAccountsByUserId(userId);

        // If there are more than two accounts, return only the first two
        if (accounts.size() > 2) {
            return accounts.stream().limit(2).collect(Collectors.toList());
        }

        return accounts;
    }

    /**
     * Inner class used to encapsulate dashboard data retrieved from AccountService.
     */
    @Getter
    @ToString
    @AllArgsConstructor
    public static class DashboardData {
        /**
         * -- GETTER --
         * Gets the net worth.
         *
         * @return the net worth
         */
        private final double netWorth;
        /**
         * -- GETTER --
         * Gets the monthly spending.
         *
         * @return the monthly spending
         */
        private final double monthlySpending;
        /**
         * -- GETTER --
         * Gets the monthly income.
         *
         * @return the monthly income
         */
        private final double monthlyIncome;
        /**
         * -- GETTER --
         * Gets the credit card due.
         *
         * @return the credit card due
         */
        private final double creditCardDue;
    }
}