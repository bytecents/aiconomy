package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.util.List;
import java.util.Optional;


public class BudgetServiceImpl implements BudgetService {
    private final JSONStorageService jsonStorageService;
    private final TransactionService transactionService;


    public BudgetServiceImpl(JSONStorageService jsonStorageService, TransactionService transactionService) {
        this.jsonStorageService = jsonStorageService;
        this.transactionService = transactionService;
        initializeBudgetCollection();
    }

    private void initializeBudgetCollection() {
        if (!jsonStorageService.collectionExists(Budget.class)) {
            jsonStorageService.initializeCollection(Budget.class);
        }
    }

    @Override
    public void addBudget(Budget budget) {
        jsonStorageService.insert(budget);
    }

    @Override
    public void updateBudget(Budget budget) {
        jsonStorageService.update(budget, Budget.class);
    }

    @Override
    public Budget getBudget(String budgetId) {
        Optional<Budget> budget = jsonStorageService.findById(budgetId, Budget.class);
        return budget.orElseThrow(() -> new RuntimeException("Budget not found with id: " + budgetId));
    }

    @Override
    public List<Budget> getBudgetsByUserId(String userId) {
        List<Budget> allBudgets = jsonStorageService.findAll(Budget.class);
        return allBudgets.stream()
                .filter(budget -> budget.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void removeBudget(String budgetId) {
        Budget budget = getBudget(budgetId);
        jsonStorageService.delete(budget, Budget.class);
    }

    @Override
    public boolean isBudgetExceeded(Budget budget, double currentExpense) {
        return currentExpense > budget.getBudgetAmount();
    }

    @Override
    public double getTotalBudget(String userId) {
        List<Budget> budgets = getBudgetsByUserId(userId);
        double totalBudget = 0.0;
        for (Budget budget : budgets) {
            totalBudget += budget.getBudgetAmount();
        }
        return totalBudget;
    }

    @Override
    public double getTotalSpent(String userId) throws ServiceException {
        List<Transaction> allTransactions = transactionService.getTransactionsByUserId(userId);
        double totalSpent = 0.0;
        for (Transaction transaction : allTransactions) {
            if (transaction.getIncomeOrExpense().equals("Expense")) {
                totalSpent += Double.parseDouble(transaction.getAmount());
            }
        }
        return totalSpent;
    }

    @Override
    public int getTotalAlertCount(String userId) throws ServiceException {
        List<Budget> budgets = getBudgetsByUserId(userId);
        int alertCount = 0;
        for (Budget budget : budgets) {
            if (isBudgetExceeded(budget, getTotalSpent(userId))) {
                alertCount++;
            }
        }
        return alertCount;
    }


    @Override
    public double getDailyAvailableBudget(String userId) {
        return 0.0;
    }

    @Override
    public double getTotalBudgetByCategory(String userId, String category) {
        return 0.0;
    }

    @Override
    public double getTotalSpentByCategory(String userId, String category) {
        return 0.0;
    }
}