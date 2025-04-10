package com.se.aiconomy.server.service;

import java.util.List;

import com.se.aiconomy.server.model.entity.Budget;

public interface BudgetService {
        void addBudget(Budget budget); // 添加预算

        void updateBudget(Budget budget); // 更新预算

        Budget getBudget(String budgetId); // 获取预算

        List<Budget> getBudgetsByUserId(String userId); // 根据用户ID获取预算列表

        void removeBudget(String budgetId); // 删除预算

        boolean isBudgetExceeded(Budget budget, double currentExpense); // 预算是否超支
}