package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.AccountService;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.service.TransactionService;

import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardRequestHandler {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;

    public DashboardRequestHandler(AccountService accountService, TransactionService transactionService, BudgetService budgetService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
    }

    /**
     * 获取用户的 Net Worth，Monthly Spending，Monthly Income 和 Credit Card Due
     *
     * @param userId 用户ID
     * @param month  要查询的月份
     * @return 包含 Net Worth，Monthly Spending，Monthly Income 和 Credit Card Due 的数据
     * @throws ServiceException 如果查询过程中发生任何错误
     */
    public DashboardData getDashboardData(String userId, Month month) throws ServiceException {
        double netWorth = accountService.calculateNetWorth(userId);
        double monthlySpending = accountService.calculateMonthlySpending(userId, month);
        double monthlyIncome = accountService.calculateMonthlyIncome(userId, month);
        double creditCardDue = accountService.calculateCreditCardDue(userId);

        return new DashboardData(netWorth, monthlySpending, monthlyIncome, creditCardDue);
    }

    /**
     * 获取账户交易记录
     *
     * @param userId    用户ID
     * @param accountId 账户ID
     * @return 包含交易记录的列表
     * @throws ServiceException 如果查询过程中发生任何错误
     */
    public List<TransactionDto> getTransactionsByAccountId(String userId, String accountId) throws ServiceException {
        return transactionService.getTransactionsByAccountId(accountId, userId);
    }

    /**
     * 获取用户的预算分类的支出/预算比例
     * @param userId 用户 ID
     * @return 一个包含Food, Shopping, Transportation三个类别的支出/预算比例的 Map
     * @throws ServiceException 如果处理过程中发生错误
     */
    public Map<String, Double> getBudgetSpendingRatio(String userId) throws ServiceException {
        // 获取与用户相关的所有预算
        List<Budget> budgets = budgetService.getBudgetsByUserId(userId);

        // 初始化用于存储比例的 Map
        Map<String, Double> spendingRatios = new HashMap<>();

        // 定义要处理的预算类别
        String[] categories = {"Food", "Shopping", "Transportation"};

        // 遍历这些类别
        for (String category : categories) {
            // 获取每个类别的总预算和总支出
            double totalBudget = budgetService.getTotalBudgetByCategory(userId, category);
            double totalSpent = budgetService.getTotalSpentByCategory(userId, category);

            // 防止除以0，避免出现除数为0的情况
            if (totalBudget != 0) {
                // 计算并存储支出/预算比例
                double spendingRatio = totalSpent / totalBudget;
                spendingRatios.put(category, spendingRatio);
            } else {
                spendingRatios.put(category, 0.0);  // 如果预算为0，比例为0
            }
        }

        return spendingRatios;
    }

    /**
     * 根据用户ID查询其账户，如果有超过2个账户，只返回前两个
     *
     * @param userId 用户ID
     * @return 账户列表，最多包含两个账户
     * @throws ServiceException 如果查询过程中发生任何错误
     */
    public List<Account> getAccountsForUser(String userId) throws ServiceException {
        List<Account> accounts = accountService.getAccountsByUserId(userId);

        // 如果账户数超过两个，只返回前两个账户
        if (accounts.size() > 2) {
            return accounts.stream().limit(2).collect(Collectors.toList());
        }

        return accounts;
    }

    /**
     * 内部类，用来封装从 AccountService 获取的 Dashboard 数据
     */
    public static class DashboardData {
        private final double netWorth;
        private final double monthlySpending;
        private final double monthlyIncome;
        private final double creditCardDue;

        public DashboardData(double netWorth, double monthlySpending, double monthlyIncome, double creditCardDue) {
            this.netWorth = netWorth;
            this.monthlySpending = monthlySpending;
            this.monthlyIncome = monthlyIncome;
            this.creditCardDue = creditCardDue;
        }

        // Getter methods
        public double getNetWorth() {
            return netWorth;
        }

        public double getMonthlySpending() {
            return monthlySpending;
        }

        public double getMonthlyIncome() {
            return monthlyIncome;
        }

        public double getCreditCardDue() {
            return creditCardDue;
        }

        @Override
        public String toString() {
            return "DashboardData{" +
                    "netWorth=" + netWorth +
                    ", monthlySpending=" + monthlySpending +
                    ", monthlyIncome=" + monthlyIncome +
                    ", creditCardDue=" + creditCardDue +
                    '}';
        }
    }
}