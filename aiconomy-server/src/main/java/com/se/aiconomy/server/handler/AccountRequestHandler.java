package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.dto.account.request.AddAccountsRequest;
import com.se.aiconomy.server.model.dto.account.request.DeleteAccountRequest;
import com.se.aiconomy.server.model.dto.account.request.GetAccountsByUserIdRequest;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.AccountService;
import com.se.aiconomy.server.service.impl.AccountServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;

import java.util.List;

public class AccountRequestHandler {
    private final AccountService accountService;

    public AccountRequestHandler() {
        JSONStorageService jsonStorageService = JSONStorageServiceImpl.getInstance();
        this.accountService = new AccountServiceImpl(jsonStorageService);
    }

    public AccountRequestHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    public List<Account> handleAddAccountRequest(AddAccountsRequest request) {
        String userId = request.getUserId();
        List<Account> accounts = request.getAccounts();

        accounts.forEach(accountService::addAccount);

        return accountService.getAccountsByUserId(userId);
    }

    public List<Account> handleGetAccountsByUserIdRequest(GetAccountsByUserIdRequest request) throws ServiceException {
        String userId = request.getUserId();
        if (userId == null || userId.isEmpty()) {
            throw new ServiceException("User ID cannot be null or empty", null);
        }

        return accountService.getAccountsByUserId(userId);
    }

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
}
