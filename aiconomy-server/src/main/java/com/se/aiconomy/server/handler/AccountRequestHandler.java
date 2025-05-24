package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.dto.account.request.AddAccountsRequest;
import com.se.aiconomy.server.model.dto.account.request.DeleteAccountRequest;
import com.se.aiconomy.server.model.dto.account.request.GetAccountsByUserIdRequest;
import com.se.aiconomy.server.model.dto.account.request.UpdateAccountRequest;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.AccountService;
import com.se.aiconomy.server.service.impl.AccountServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;

import java.util.List;

/**
 * Handles account-related requests, including adding, retrieving, deleting, and updating accounts.
 */
public class AccountRequestHandler {
    private final AccountService accountService;

    /**
     * Default constructor that initializes the AccountService with a JSONStorageService instance.
     */
    public AccountRequestHandler() {
        JSONStorageService jsonStorageService = JSONStorageServiceImpl.getInstance();
        this.accountService = new AccountServiceImpl(jsonStorageService);
    }

    /**
     * Constructor that allows injection of a custom AccountService.
     *
     * @param accountService the AccountService to use
     */
    public AccountRequestHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Handles the request to add accounts for a user.
     *
     * @param request the request containing user ID and accounts to add
     * @return the list of accounts for the user after addition
     */
    public List<Account> handleAddAccountRequest(AddAccountsRequest request) {
        String userId = request.getUserId();
        List<Account> accounts = request.getAccounts();

        accounts.forEach(accountService::addAccount);

        return accountService.getAccountsByUserId(userId);
    }

    /**
     * Handles the request to retrieve accounts by user ID.
     *
     * @param request the request containing the user ID
     * @return the list of accounts for the specified user
     * @throws ServiceException if the user ID is null or empty
     */
    public List<Account> handleGetAccountsByUserIdRequest(GetAccountsByUserIdRequest request) throws ServiceException {
        String userId = request.getUserId();
        if (userId == null || userId.isEmpty()) {
            throw new ServiceException("User ID cannot be null or empty", null);
        }

        return accountService.getAccountsByUserId(userId);
    }

    /**
     * Handles the request to delete an account.
     *
     * @param deleteAccountRequest the request containing user ID and account ID to delete
     * @return true if the account was deleted successfully, false otherwise
     */
    public boolean handleDeleteAccountRequest(DeleteAccountRequest deleteAccountRequest) {
        String userId = deleteAccountRequest.getUserId();
        String accountId = deleteAccountRequest.getAccountId();

        try {
            accountService.removeAccount(accountId);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Handles the request to update an account.
     *
     * @param request the request containing user ID and the account to update
     * @throws ServiceException if the request, user ID, or account information is invalid,
     *                          or if the account does not belong to the specified user
     */
    public void handleUpdateAccountRequest(UpdateAccountRequest request) throws ServiceException {
        if (request == null) {
            throw new ServiceException("Update account request cannot be null", null);
        }

        String userId = request.getUserId();
        Account account = request.getAccount();

        if (userId == null || userId.isEmpty()) {
            throw new ServiceException("User ID cannot be null or empty", null);
        }

        if (account == null || account.getId() == null || account.getId().isEmpty()) {
            throw new ServiceException("Account or account ID cannot be null or empty", null);
        }

        // Verify that the account belongs to the user
        List<Account> userAccounts = accountService.getAccountsByUserId(userId);
        boolean accountExists = userAccounts.stream().anyMatch(acc -> acc.getId().equals(account.getId()));
        if (!accountExists) {
            throw new ServiceException("Account does not belong to the specified user", null);
        }

        // Update the account
        accountService.updateAccount(account);
    }
}