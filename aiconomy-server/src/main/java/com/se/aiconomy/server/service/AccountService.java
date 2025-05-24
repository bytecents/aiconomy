package com.se.aiconomy.server.service;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.entity.Account;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

/**
 * AccountService interface provides methods for managing user accounts,
 * calculating financial statistics, and handling account-related operations.
 */
public interface AccountService {
    /**
     * Adds a new account.
     *
     * @param account the account to add
     */
    void addAccount(Account account);

    /**
     * Updates an existing account.
     *
     * @param account the account to update
     */
    void updateAccount(Account account);

    /**
     * Removes an account by its ID.
     *
     * @param accountId the ID of the account to remove
     */
    void removeAccount(String accountId);

    /**
     * Retrieves all accounts associated with a specific user ID.
     *
     * @param userId the user ID
     * @return a list of accounts belonging to the user
     */
    List<Account> getAccountsByUserId(String userId);

    // Dashboard related methods

    /**
     * Calculates the net worth for a user by summing account balances and transaction amounts.
     *
     * @param userId the user ID
     * @return the calculated net worth
     * @throws ServiceException if an error occurs during calculation
     */
    double calculateNetWorth(String userId) throws ServiceException;

    /**
     * Calculates the total spending for a user in a specific month.
     *
     * @param userId the user ID
     * @param month  the month to calculate spending for
     * @return the total spending amount
     * @throws ServiceException if an error occurs during calculation
     */
    double calculateMonthlySpending(String userId, Month month) throws ServiceException;

    /**
     * Calculates the total income for a user in a specific month.
     *
     * @param userId the user ID
     * @param month  the month to calculate income for
     * @return the total income amount
     * @throws ServiceException if an error occurs during calculation
     */
    double calculateMonthlyIncome(String userId, Month month) throws ServiceException;

    /**
     * Calculates the total credit card due amount for a user.
     *
     * @param userId the user ID
     * @return the total credit card due amount
     */
    double calculateCreditCardDue(String userId);

    /**
     * Calculates the next payment due date for all credit accounts of a user.
     *
     * @param userId the user ID
     * @return the next due date, or LocalDateTime.MAX if none found
     */
    LocalDateTime calculateNextDueDate(String userId);
}
