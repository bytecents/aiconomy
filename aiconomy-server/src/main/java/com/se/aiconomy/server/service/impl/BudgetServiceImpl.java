package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.time.LocalDateTime;
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

    public void initializeBudgetCollection() {
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
    public void removeBudget(String budgetId) {
        Optional<Budget> budget = jsonStorageService.findById(budgetId, Budget.class);
        budget.ifPresent(value -> jsonStorageService.delete(value, Budget.class));
    }

    @Override
    public boolean isBudgetExceeded(Budget budget) throws ServiceException {
        String budgetCategory = budget.getBudgetCategory();
        double totalBudget = getTotalBudgetByCategory(budget.getUserId(), budgetCategory);
        double totalSpent = getTotalSpentByCategory(budget.getUserId(), budgetCategory);
        return totalBudget < totalSpent;
    }

    @Override
    public boolean isAlertBudget(Budget budget) throws ServiceException {
        double alertSettings = budget.getAlertSettings();
        String budgetCategory = budget.getBudgetCategory();
        double totalBudget = getTotalBudgetByCategory(budget.getUserId(), budgetCategory);
        double totalSpent = getTotalSpentByCategory(budget.getUserId(), budgetCategory);
        double ratio = totalSpent / totalBudget;
        return ratio >= alertSettings;
    }

    @Override
    public List<Budget> getBudgetsByUserId(String userId) {
        List<Budget> budgets = jsonStorageService.findAll(Budget.class);
        return budgets.stream().filter(budget -> budget.getUserId().equals(userId)).toList();
    }

    @Override
    public double getTotalBudget(String userId) {
        List<Budget> budgets = getBudgetsByUserId(userId);
        double totalBudget = 0;
        for (Budget budget : budgets) {
            totalBudget += budget.getBudgetAmount();
        }
        return totalBudget;
    }

    @Override
    public double getTotalSpent(String userId) throws ServiceException {
        List<Budget> budgets = getBudgetsByUserId(userId);
        double totalSpent = 0;
        for (Budget budget : budgets) {
            String budgetCategory = budget.getBudgetCategory();
            List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
            for (Transaction transaction : transactions) {
                if (transaction.getType().equals(budgetCategory) && transaction.getIncomeOrExpense().equals("Expense")) {
                    totalSpent += Double.parseDouble(transaction.getAmount());
                }
            }
        }
        return totalSpent;
    }

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

    @Override
    public double getDailyAvailableBudget(String userId) throws ServiceException {
        double totalBudget = getTotalBudget(userId);
        double totalSpent = getTotalSpent(userId);
        double dailyAvailable = (totalBudget - totalSpent) / 30;
        double dailySpent = 0;
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        LocalDateTime today = LocalDateTime.now();
        for (Transaction transaction : transactions) {
            if (transaction.getTime().equals(today) && transaction.getIncomeOrExpense().equals("Expense")) {
                dailySpent += Double.parseDouble(transaction.getAmount());
            }
        }
        return dailyAvailable - dailySpent;
    }

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

    @Override
    public double getTotalSpentByCategory(String userId, String category) throws ServiceException {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        double totalSpentByCategory = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals(category) && transaction.getIncomeOrExpense().equals("Expense")) {
                totalSpentByCategory += Double.parseDouble(transaction.getAmount());
            }
        }
        return totalSpentByCategory;
    }
}