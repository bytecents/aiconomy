package com.se.aiconomy.server.service;

import java.util.List;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.entity.Budget;

public interface BudgetService {
        void addBudget(Budget budget); // 添加预算

        void updateBudget(Budget budget); // 更新预算

        Budget getBudget(String budgetId); // 获取预算

        List<Budget> getBudgetsByUserId(String userId); // 根据用户ID获取预算列表

        void removeBudget(String budgetId); // 删除预算

        boolean isBudgetExceeded(Budget budget, double currentExpense); // 预算是否超支

        double getTotalBudget(String userId); // 获取用户的总预算

        double getTotalSpent(String userId) throws ServiceException; // 获取用户的总支出

        int getTotalAlertCount(String userId) throws ServiceException; // 获取用户的总预算超支次数

        double getDailyAvailableBudget(String userId); // 获取用户的每日可用预算

        double getTotalBudgetByCategory(String userId, String category); // 获取用户的某一类别的预算总额

        double getTotalSpentByCategory(String userId, String category); // 获取用户的某一类别的支出总额
}