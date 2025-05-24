package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.AccountService;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.service.impl.AccountServiceImpl;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;

import java.time.Month;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardRequestHandler {
    private  final AccountService accountService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;

    public DashboardRequestHandler(AccountService accountService, TransactionService transactionService, BudgetService budgetService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
    }

    private DashboardRequestHandler()
    {
        JSONStorageService jsonStorageService = JSONStorageServiceImpl.getInstance();
        this.accountService = new AccountServiceImpl(jsonStorageService);
        this.transactionService = new TransactionServiceImpl();
        this.budgetService = new BudgetServiceImpl(jsonStorageService);
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
     * 获取用户的预算分类的支出/预算比例，并按比例降序排序
     * @param userId 用户 ID
     * @return 一个包含所有预算类别的支出/预算比例的 Map，按比例降序排序
     * @throws ServiceException 如果处理过程中发生错误
     */
    public Map<String, Double> getBudgetSpendingRatio(String userId) throws ServiceException {
        // 获取与用户相关的所有预算
        List<Budget> budgets = budgetService.getBudgetsByUserId(userId);

        // 计算每个预算类别的支出/预算比例
        Map<String, Double> spendingRatios = new HashMap<>();
        for (Budget budget : budgets) {
            String category = budget.getBudgetCategory();
            double totalBudget = budgetService.getTotalBudgetByCategory(userId, category);
            double totalSpent = budgetService.getTotalSpentByCategory(userId, category);

            // 防止除以0
            double spendingRatio = (totalBudget != 0) ? totalSpent / totalBudget : 0.0;
            spendingRatios.put(category, spendingRatio);
        }

        // 按比例降序排序
        return spendingRatios.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())) // 降序排序
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // 合并函数（处理键冲突，保留第一个值）
                        LinkedHashMap::new
                ));
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