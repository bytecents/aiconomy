package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

/**
 * Implementation of the BudgetService interface.
 * <p>
 * This class provides methods for managing budgets, including adding, updating, removing budgets,
 * checking if a budget is exceeded or alert threshold is reached, and calculating various budget-related statistics.
 * It interacts with a JSON-based storage service to persist and retrieve budget and transaction data.
 * </p>
 */
public class BudgetServiceImpl implements BudgetService {
    /**
     * The JSON storage service used for data persistence.
     */
    private final JSONStorageService jsonStorageService;

    /**
     * Constructs a BudgetServiceImpl with the specified JSON storage service.
     *
     * @param jsonStorageService the JSON storage service to use
     */
    public BudgetServiceImpl(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
        initializeBudgetCollection();
    }

    /**
     * Initializes the budget collection in the storage if it does not exist.
     */
    public void initializeBudgetCollection() {
        if (!jsonStorageService.collectionExists(Budget.class)) {
            jsonStorageService.initializeCollection(Budget.class);
        }
    }

    /**
     * Adds a new budget to the storage.
     *
     * @param budget the budget to add
     */
    @Override
    public void addBudget(Budget budget) {
        jsonStorageService.insert(budget);
    }

    /**
     * Updates an existing budget in the storage.
     *
     * @param budget the budget to update
     */
    @Override
    public void updateBudget(Budget budget) {
        jsonStorageService.update(budget, Budget.class);
    }

    /**
     * Removes a budget from the storage by its ID.
     *
     * @param budgetId the ID of the budget to remove
     */
    @Override
    public void removeBudget(String budgetId) {
        String categoty = "";
        String userId = "";
        List<Budget> budgets = jsonStorageService.findAll(Budget.class);
        for (Budget budget : budgets) {
            if (budget.getId().equals(budgetId)) {
                categoty = budget.getBudgetCategory();
                userId = budget.getUserId();
                break;
            }
        }
        Budget budget = getBudgetByCategory(userId, categoty);
        jsonStorageService.delete(budget, Budget.class);
    }

    /**
     * Checks if the specified budget is exceeded.
     *
     * @param budget the budget to check
     * @return true if the budget is exceeded, false otherwise
     * @throws ServiceException if an error occurs while retrieving transactions
     */
    @Override
    public boolean isBudgetExceeded(Budget budget) throws ServiceException {
        String budgetCategory = budget.getBudgetCategory();
        double totalBudget = getTotalBudgetByCategory(budget.getUserId(), budgetCategory);
        double totalSpent = getTotalSpentByCategory(budget.getUserId(), budgetCategory);
        return totalBudget < totalSpent;
    }

    /**
     * Checks if the specified budget has reached the alert threshold.
     *
     * @param budget the budget to check
     * @return true if the alert threshold is reached, false otherwise
     * @throws ServiceException if an error occurs while retrieving transactions
     */
    @Override
    public boolean isAlertBudget(Budget budget) throws ServiceException {
        double alertSettings = budget.getAlertSettings();
        String budgetCategory = budget.getBudgetCategory();
        double totalBudget = getTotalBudgetByCategory(budget.getUserId(), budgetCategory);
        double totalSpent = getTotalSpentByCategory(budget.getUserId(), budgetCategory);
        double ratio = totalSpent / totalBudget;
        return ratio >= alertSettings;
    }

    /**
     * Retrieves all budgets associated with a specific user ID.
     *
     * @param userId the user ID
     * @return a list of budgets belonging to the user
     */
    @Override
    public List<Budget> getBudgetsByUserId(String userId) {
        List<Budget> budgets = jsonStorageService.findAll(Budget.class);
        return budgets.stream().filter(budget -> budget.getUserId().equals(userId)).toList();
    }

    /**
     * Retrieves a budget by user ID and category.
     *
     * @param userId   the user ID
     * @param category the budget category
     * @return the budget for the specified user and category, or null if not found
     */
    @Override
    public Budget getBudgetByCategory(String userId, String category) {
        List<Budget> budgets = getBudgetsByUserId(userId);
        for (Budget budget : budgets) {
            if (budget.getBudgetCategory().equals(category)) {
                return budget;
            }
        }
        return null;
    }

    /**
     * Calculates the total budget amount for a user.
     *
     * @param userId the user ID
     * @return the total budget amount
     */
    @Override
    public double getTotalBudget(String userId) {
        List<Budget> budgets = getBudgetsByUserId(userId);
        double totalBudget = 0;
        for (Budget budget : budgets) {
            totalBudget += budget.getBudgetAmount();
        }
        return totalBudget;
    }

    /**
     * Calculates the total spent amount for a user across all budget categories.
     *
     * @param userId the user ID
     * @return the total spent amount
     * @throws ServiceException if an error occurs while retrieving transactions
     */
    @Override
    public double getTotalSpent(String userId) throws ServiceException {
        List<Budget> budgets = getBudgetsByUserId(userId);
        double totalSpent = 0;
        for (Budget budget : budgets) {
            String budgetCategory = budget.getBudgetCategory();
            List<TransactionDto> transactions = getTransactionsByUserId(userId);
            for (TransactionDto transaction : transactions) {
                if (transaction.getType().equals(budgetCategory) && transaction.getIncomeOrExpense().equals("Expense")) {
                    totalSpent += Double.parseDouble(transaction.getAmount());
                }
            }
        }
        return totalSpent;
    }

    /**
     * Calculates the total spent amount for a user in the current month.
     *
     * @param userId the user ID
     * @return the total spent amount in the current month
     * @throws ServiceException if an error occurs while retrieving transactions
     */
    @Override
    public double getMonthlySpent(String userId) throws ServiceException {
        List<TransactionDto> transactions = getTransactionsByUserId(userId);
        Month currentMonth = LocalDateTime.now().getMonth();
        double totalSpent = 0;
        for (TransactionDto transaction : transactions) {
            if (transaction.getIncomeOrExpense().equals("Expense") && transaction.getTime().getMonth().equals(currentMonth)) {
                totalSpent += Double.parseDouble(transaction.getAmount());
            }
        }
        return totalSpent;
    }

    /**
     * Calculates the total number of budgets that have reached the alert threshold for a user.
     *
     * @param userId the user ID
     * @return the total alert count
     * @throws ServiceException if an error occurs while retrieving transactions
     */
    @Override
    public int getTotalAlertCount(String userId) throws ServiceException {
        List<Budget> budgets = getBudgetsByUserId(userId);
        int alertCount = 0;
        for (Budget budget : budgets) {
            if (isAlertBudget(budget)) {
                alertCount++;
            }
        }
        return alertCount;
    }

    /**
     * Calculates the daily available budget for a user.
     *
     * @param userId the user ID
     * @return the daily available budget
     * @throws ServiceException if an error occurs while retrieving transactions
     */
    @Override
    public double getDailyAvailableBudget(String userId) throws ServiceException {
        double totalBudget = getTotalBudget(userId);
        double totalSpent = getTotalSpent(userId);
        int leftDays = 30 - LocalDateTime.now().getDayOfMonth();
        double dailyAvailable = (totalBudget - totalSpent) / leftDays;
        double dailySpent = 0;
        List<TransactionDto> transactions = getTransactionsByUserId(userId);
        LocalDateTime today = LocalDateTime.now();
        for (TransactionDto transaction : transactions) {
            if (transaction.getTime().equals(today) && transaction.getIncomeOrExpense().equals("Expense")) {
                dailySpent += Double.parseDouble(transaction.getAmount());
            }
        }
        return dailyAvailable - dailySpent;
    }

    /**
     * Calculates the number of days left in the current month.
     *
     * @param userId the user ID
     * @return the number of days left in the month
     */
    @Override
    public int getLeftDays(String userId) {
        return 30 - LocalDateTime.now().getDayOfMonth();
    }

    /**
     * Calculates the total budget amount for a user in a specific category.
     *
     * @param userId   the user ID
     * @param category the budget category
     * @return the total budget amount for the category
     */
    @Override
    public double getTotalBudgetByCategory(String userId, String category) {
        List<Budget> budgets = getBudgetsByUserId(userId);
        double totalBudgetByCategory = 0;
        for (Budget budget : budgets) {
            if (budget.getBudgetCategory().equals(category)) {
                totalBudgetByCategory += budget.getBudgetAmount();
            }
        }
        return totalBudgetByCategory;
    }

    /**
     * Calculates the total spent amount for a user in a specific category.
     *
     * @param userId   the user ID
     * @param category the budget category
     * @return the total spent amount for the category
     * @throws ServiceException if an error occurs while retrieving transactions
     */
    @Override
    public double getTotalSpentByCategory(String userId, String category) throws ServiceException {
        List<TransactionDto> transactions = getTransactionsByUserId(userId);
        double totalSpentByCategory = 0;
        for (TransactionDto transaction : transactions) {
            if (transaction.getBillType().getDisplayName().equalsIgnoreCase(category) && transaction.getIncomeOrExpense().equals("Expense")) {
                totalSpentByCategory += Double.parseDouble(transaction.getAmount());
            }
        }
        return totalSpentByCategory;
    }

    /**
     * Retrieves all transactions associated with a specific user ID.
     *
     * @param userId the user ID
     * @return a list of transactions belonging to the user
     * @throws ServiceException if an error occurs while retrieving transactions
     */
    List<TransactionDto> getTransactionsByUserId(String userId) throws ServiceException {
        List<TransactionDto> transactions = jsonStorageService.findAll(TransactionDto.class);
        return transactions.stream().filter(transaction -> transaction.getUserId().equals(userId)).toList();
    }
}
