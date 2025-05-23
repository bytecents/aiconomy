package com.se.aiconomy.server.model.dto.account.request;

import com.se.aiconomy.server.model.entity.Account;

public class UpdateAccountRequest {
    private String userId;
    private Account account;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}