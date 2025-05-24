package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.DashboardRequestHandler;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardRequestHandlerTest {

    private static final String TEST_USER = "test-user-" + System.currentTimeMillis(); // 唯一的测试用户 ID
    private static final LocalDateTime TEST_TIME = LocalDateTime.of(2025, 5, 23, 21, 55); // 固定的测试时间

    private DashboardRequestHandler dashboardRequestHandler;
    private BudgetServiceImpl budgetService;
    private JSONStorageService storageService;

    @BeforeEach
    void setUp() {
        // 直接获取 JSONStorageService 的单例实例
        storageService = JSONStorageServiceImpl.getInstance();
        budgetService = new BudgetServiceImpl(storageService);
        dashboardRequestHandler = new DashboardRequestHandler(null, null, budgetService);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        cleanupTestData();
    }

    @Test
    void testGetBudgetSpendingRatio_Success() throws ServiceException {
        // 准备测试数据
        String userId = TEST_USER;

        // 创建预算
        Budget foodBudget = createBudget(userId, "budget1", "Other", 1000.0);
        Budget shoppingBudget = createBudget(userId, "budget2", "Shopping", 500.0);
        Budget transportBudget = createBudget(userId, "budget3", "Transportation", 200.0);

        // 插入预算
        storageService.insert(foodBudget);
        storageService.insert(shoppingBudget);
        storageService.insert(transportBudget);

        // 创建交易
        TransactionDto foodTransaction = createTransaction(userId, "tx1", "Other", "Expense", "800.0", TEST_TIME);
        TransactionDto shoppingTransaction = createTransaction(userId, "tx2", "Shopping", "Expense", "450.0", TEST_TIME);
        TransactionDto transportTransaction = createTransaction(userId, "tx3", "Transportation", "Expense", "100.0", TEST_TIME);

        // 插入交易
        storageService.insert(foodTransaction);
        storageService.insert(shoppingTransaction);
        storageService.insert(transportTransaction);

        // 执行测试方法
        Map<String, Double> result = dashboardRequestHandler.getBudgetSpendingRatio(userId);

        // 验证结果
        assertNotNull(result, "结果不应为空");
        assertEquals(3, result.size(), "应包含 3 个预算类别");

        // 验证排序（按比率降序：Shopping 0.9, Other 0.8, Transportation 0.5）
        String[] expectedOrder = {"Shopping", "Other", "Transportation"};
        String[] actualOrder = result.keySet().toArray(new String[0]);
        assertArrayEquals(expectedOrder, actualOrder, "预算类别应按比率降序排序");

        // 验证比率值
        assertEquals(0.9, result.get("Shopping"), 0.001, "Shopping 比率应为 0.9");
        assertEquals(0.8, result.get("Other"), 0.001, "Food 比率应为 0.8");
        assertEquals(0.5, result.get("Transportation"), 0.001, "Transportation 比率应为 0.5");
    }

    // 辅助方法：创建 Budget
    private Budget createBudget(String userId, String id, String category, double amount) {
        Budget budget = new Budget();
        budget.setId(id);
        budget.setUserId(userId);
        budget.setBudgetCategory(category);
        budget.setBudgetAmount(amount);
        return budget;
    }

    // 辅助方法：创建 TransactionDto
    private TransactionDto createTransaction(String userId, String id, String type, String incomeOrExpense, String amount, LocalDateTime time) {
        TransactionDto transaction = new TransactionDto();
        transaction.setId(id);
        transaction.setUserId(userId);
        transaction.setType(type);
        transaction.setIncomeOrExpense(incomeOrExpense);
        transaction.setAmount(amount);
        transaction.setTime(time);
        transaction.setBillType(DynamicBillType.fromString(type));
        return transaction;
    }

    // 辅助方法：清理测试数据
    private void cleanupTestData() {
        // 删除特定测试用户的所有预算和交易
        List<Budget> budgets = storageService.findAll(Budget.class);
        for (Budget budget : budgets) {
            if (budget.getUserId().equals(TEST_USER)) {
                storageService.delete(budget, Budget.class);
            }
        }
        List<TransactionDto> transactions = storageService.findAll(TransactionDto.class);
        for (TransactionDto transaction : transactions) {
            if (transaction.getUserId().equals(TEST_USER)) {
                storageService.delete(transaction, TransactionDto.class);
            }
        }
    }
}