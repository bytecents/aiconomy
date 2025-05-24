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

/**
 * Unit tests for {@link DashboardRequestHandler}.
 * This class tests the budget spending ratio calculation logic.
 */
public class DashboardRequestHandlerTest {

    /**
     * Unique test user ID.
     */
    private static final String TEST_USER = "test-user-" + System.currentTimeMillis();

    /**
     * Fixed test time for transactions.
     */
    private static final LocalDateTime TEST_TIME = LocalDateTime.of(2025, 5, 23, 21, 55);

    private DashboardRequestHandler dashboardRequestHandler;
    private JSONStorageService storageService;

    /**
     * Initializes the test environment before each test.
     * Gets the singleton instance of JSONStorageService and initializes services.
     */
    @BeforeEach
    void setUp() {
        // Directly get the singleton instance of JSONStorageService
        storageService = JSONStorageServiceImpl.getInstance();
        BudgetServiceImpl budgetService = new BudgetServiceImpl(storageService);
        dashboardRequestHandler = new DashboardRequestHandler(null, null, budgetService);
    }

    /**
     * Cleans up test data after each test.
     */
    @AfterEach
    void tearDown() {
        // Clean up test data
        cleanupTestData();
    }

    /**
     * Tests the calculation of budget spending ratio for a user.
     * Prepares budgets and transactions, then verifies the spending ratio and order.
     *
     * @throws ServiceException if calculation fails
     */
    @Test
    void testGetBudgetSpendingRatio_Success() throws ServiceException {
        // Prepare test data
        String userId = TEST_USER;

        // Create budgets
        Budget foodBudget = createBudget(userId, "budget1", "Other", 1000.0);
        Budget shoppingBudget = createBudget(userId, "budget2", "Shopping", 500.0);
        Budget transportBudget = createBudget(userId, "budget3", "Transportation", 200.0);

        // Insert budgets
        storageService.insert(foodBudget);
        storageService.insert(shoppingBudget);
        storageService.insert(transportBudget);

        // Create transactions
        TransactionDto foodTransaction = createTransaction(userId, "tx1", "Other", "Expense", "800.0", TEST_TIME);
        TransactionDto shoppingTransaction = createTransaction(userId, "tx2", "Shopping", "Expense", "450.0", TEST_TIME);
        TransactionDto transportTransaction = createTransaction(userId, "tx3", "Transportation", "Expense", "100.0", TEST_TIME);

        // Insert transactions
        storageService.insert(foodTransaction);
        storageService.insert(shoppingTransaction);
        storageService.insert(transportTransaction);

        // Execute test method
        Map<String, Double> result = dashboardRequestHandler.getBudgetSpendingRatio(userId);

        // Verify result
        assertNotNull(result, "Result should not be null");
        assertEquals(3, result.size(), "Should contain 3 budget categories");

        // Verify order (descending by ratio: Shopping 0.9, Other 0.8, Transportation 0.5)
        String[] expectedOrder = {"Shopping", "Other", "Transportation"};
        String[] actualOrder = result.keySet().toArray(new String[0]);
        assertArrayEquals(expectedOrder, actualOrder, "Budget categories should be sorted by ratio descending");

        // Verify ratio values
        assertEquals(0.9, result.get("Shopping"), 0.001, "Shopping ratio should be 0.9");
        assertEquals(0.8, result.get("Other"), 0.001, "Other ratio should be 0.8");
        assertEquals(0.5, result.get("Transportation"), 0.001, "Transportation ratio should be 0.5");
    }

    /**
     * Helper method to create a {@link Budget} object.
     *
     * @param userId   the user ID
     * @param id       the budget ID
     * @param category the budget category
     * @param amount   the budget amount
     * @return a new Budget object
     */
    private Budget createBudget(String userId, String id, String category, double amount) {
        Budget budget = new Budget();
        budget.setId(id);
        budget.setUserId(userId);
        budget.setBudgetCategory(category);
        budget.setBudgetAmount(amount);
        return budget;
    }

    /**
     * Helper method to create a {@link TransactionDto} object.
     *
     * @param userId          the user ID
     * @param id              the transaction ID
     * @param type            the transaction type
     * @param incomeOrExpense income or expense type
     * @param amount          the transaction amount
     * @param time            the transaction time
     * @return a new TransactionDto object
     */
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

    /**
     * Helper method to clean up all test data for the test user.
     * Deletes all budgets and transactions for the test user.
     */
    private void cleanupTestData() {
        // Delete all budgets for the test user
        List<Budget> budgets = storageService.findAll(Budget.class);
        for (Budget budget : budgets) {
            if (budget.getUserId().equals(TEST_USER)) {
                storageService.delete(budget, Budget.class);
            }
        }
        // Delete all transactions for the test user
        List<TransactionDto> transactions = storageService.findAll(TransactionDto.class);
        for (TransactionDto transaction : transactions) {
            if (transaction.getUserId().equals(TEST_USER)) {
                storageService.delete(transaction, TransactionDto.class);
            }
        }
    }
}
