package com.se.aiconomy.server.service.impl;

import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.model.entity.Budget;
import java.util.List;

public class BudgetServiceImpl implements BudgetService {
    @Override
    public void addBudget(Budget budget) {
        // Implementation for adding a budget
    }

    @Override
    public void updateBudget(Budget budget) {
        // Implementation for updating a budget
    }

    @Override
    public Budget getBudget(String budgetId) {
        // Implementation for getting a budget by ID
        return null;
    }

    @Override
    public List<Budget> getBudgetsByUserId(String userId) {
        // Implementation for getting budgets by user ID
        return null;
    }

    @Override
    public void removeBudget(String budgetId) {
        // Implementation for removing a budget
    }

    @Override
    public boolean isBudgetExceeded(Budget budget, double currentExpense) {
        return currentExpense > budget.getBudgetAmount();
    }
}
