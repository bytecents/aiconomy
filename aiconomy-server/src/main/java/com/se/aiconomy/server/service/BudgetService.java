package com.se.aiconomy.server.service;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.entity.Budget;

import java.util.List;

public interface BudgetService {
        void addBudget(Budget budget); // 添加预算

        void updateBudget(Budget budget); // 更新预算

        void removeBudget(String budgetId); // 删除预算

        boolean isBudgetExceeded(Budget budget) throws ServiceException; // 预算是否超支

        boolean isAlertBudget(Budget budget) throws ServiceException; // 是否超支预警

        List<Budget> getBudgetsByUserId(String userId); // 根据用户ID获取预算列表

        double getTotalBudget(String userId); // 获取用户的总预算

        double getTotalSpent(String userId) throws ServiceException; // 获取用户的总支出

        double getMonthlySpent(String userId) throws ServiceException; // 获取用户的月度支出

        int getTotalAlertCount(String userId) throws ServiceException; // 获取用户超支的预算数量

        double getDailyAvailableBudget(String userId) throws ServiceException; // 获取用户的每日可用预算

        int getLeftDays(String userId);

        double getTotalBudgetByCategory(String userId, String category); // 获取用户的某一类别的预算总额

        double getTotalSpentByCategory(String userId, String category) throws ServiceException; // 获取用户的某一类别的支出总额
}