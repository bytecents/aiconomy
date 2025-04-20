package com.se.aiconomy.server.service;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.entity.Account;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public interface AccountService {
    void addAccount(Account account);

    void updateAccount(Account account);

    void removeAccount(String accountId);

    List<Account> getAccountsByUserId(String userId);

    // Dashboard related methods
    double calculateNetWorth(String userId);

    double calculateMonthlySpending(String userId, Month month) throws ServiceException;

    double calculateMonthlyIncome(String userId, Month month) throws ServiceException;

    double calculateCreditCardDue(String userId);

    LocalDateTime calculateNextDueDate(String userId);

    // Account related methods
    // TODO: add methods for account related operations


    // List<BillType> getBillTypeByAccountId(String accountId, String userId); // 获取账单类型
}
