package com.se.aiconomy.server.service;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.entity.Budget;

import java.util.List;

/**
 * BudgetService interface provides methods for managing user budgets,
 * checking budget status, and calculating budget-related statistics.
 */
public interface BudgetService {
    /**
     * Adds a new budget.
     *
     * @param budget the budget to add
     */
    void addBudget(Budget budget);

    /**
     * Updates an existing budget.
     *
     * @param budget the budget to update
     */
    void updateBudget(Budget budget);

    /**
     * Removes a budget by its ID.
     *
     * @param budgetId the ID of the budget to remove
     */
    void removeBudget(String budgetId);

    /**
     * Checks if the budget is exceeded.
     *
     * @param budget the budget to check
     * @return true if the budget is exceeded, false otherwise
     * @throws ServiceException if an error occurs during the check
     */
    boolean isBudgetExceeded(Budget budget) throws ServiceException;

    /**
     * Checks if the budget is in alert status (close to being exceeded).
     *
     * @param budget the budget to check
     * @return true if the budget is in alert status, false otherwise
     * @throws ServiceException if an error occurs during the check
     */
    boolean isAlertBudget(Budget budget) throws ServiceException;

    /**
     * Retrieves all budgets associated with a specific user ID.
     *
     * @param userId the user ID
     * @return a list of budgets belonging to the user
     */
    List<Budget> getBudgetsByUserId(String userId);

    /**
     * Retrieves a budget by user ID and category.
     *
     * @param userId   the user ID
     * @param category the budget category
     * @return the budget for the specified category, or null if not found
     */
    Budget getBudgetByCategory(String userId, String category);

    /**
     * Gets the total budget amount for a user.
     *
     * @param userId the user ID
     * @return the total budget amount
     */
    double getTotalBudget(String userId);

    /**
     * Gets the total amount spent by a user.
     *
     * @param userId the user ID
     * @return the total spent amount
     * @throws ServiceException if an error occurs during calculation
     */
    double getTotalSpent(String userId) throws ServiceException;

    /**
     * Gets the total amount spent by a user in the current month.
     *
     * @param userId the user ID
     * @return the monthly spent amount
     * @throws ServiceException if an error occurs during calculation
     */
    double getMonthlySpent(String userId) throws ServiceException;

    /**
     * Gets the total number of budgets that have triggered an alert for a user.
     *
     * @param userId the user ID
     * @return the number of alert budgets
     * @throws ServiceException if an error occurs during calculation
     */
    int getTotalAlertCount(String userId) throws ServiceException;

    /**
     * Gets the available daily budget for a user.
     *
     * @param userId the user ID
     * @return the daily available budget
     * @throws ServiceException if an error occurs during calculation
     */
    double getDailyAvailableBudget(String userId) throws ServiceException;

    /**
     * Gets the number of days left in the current month for a user.
     *
     * @param userId the user ID
     * @return the number of days left
     */
    int getLeftDays(String userId);

    /**
     * Gets the total budget amount for a specific category for a user.
     *
     * @param userId   the user ID
     * @param category the budget category
     * @return the total budget amount for the category
     */
    double getTotalBudgetByCategory(String userId, String category);

    /**
     * Gets the total amount spent by a user in a specific category.
     *
     * @param userId   the user ID
     * @param category the budget category
     * @return the total spent amount for the category
     * @throws ServiceException if an error occurs during calculation
     */
    double getTotalSpentByCategory(String userId, String category) throws ServiceException;
}
