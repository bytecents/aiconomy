package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.storage.service.JSONStorageService;

import java.util.List;
import java.util.Optional;


public class BudgetServiceImpl implements BudgetService {
    private final JSONStorageService jsonStorageService;


    public BudgetServiceImpl(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
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
}
