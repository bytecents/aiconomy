package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;


public class BudgetServiceImpl implements BudgetService {
    private final JSONStorageService jsonStorageService;

    public BudgetServiceImpl(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
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
    public Budget getBudgetByCategory(String userId, String category) {
        List<Budget> budgets = getBudgetsByUserId(userId);
        for (Budget budget : budgets) {
            if (budget.getBudgetCategory().equals(category)) {
                return budget;
            }
        }
        return null;
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
            List<TransactionDto> transactions = getTransactionsByUserId(userId);
            for (TransactionDto transaction : transactions) {
                if (transaction.getType().equals(budgetCategory) && transaction.getIncomeOrExpense().equals("Expense")) {
                    totalSpent += Double.parseDouble(transaction.getAmount());
                }
            }
        }
        return totalSpent;
    }

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

    @Override
    public int getLeftDays(String userId) {
        return 30 - LocalDateTime.now().getDayOfMonth();
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
        List<TransactionDto> transactions = getTransactionsByUserId(userId);
        double totalSpentByCategory = 0;
        for (TransactionDto transaction : transactions) {
            if (transaction.getBillType().getDisplayName().equalsIgnoreCase(category) && transaction.getIncomeOrExpense().equals("Expense")) {
                totalSpentByCategory += Double.parseDouble(transaction.getAmount());
            }
        }
        return totalSpentByCategory;
    }

    List<TransactionDto> getTransactionsByUserId(String userId) throws ServiceException {
        List<TransactionDto> transactions = jsonStorageService.findAll(TransactionDto.class);
        return transactions.stream().filter(transaction -> transaction.getUserId().equals(userId)).toList();
    }
}