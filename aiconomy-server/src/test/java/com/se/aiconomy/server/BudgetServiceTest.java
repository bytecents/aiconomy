package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Unit tests for {@link BudgetService} and related budget operations.
 * <p>
 * This class tests CRUD operations, budget retrieval, and calculation functionalities for budgets.
 * </p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BudgetServiceTest {
    private static final Logger log = LoggerFactory.getLogger(Budget.class);
    private static JSONStorageService jsonStorageService;

    private static BudgetService budgetService;
    private static TransactionService transactionService;

    /**
     * Initializes the storage service, budget service, and transaction service before all tests.
     */
    @BeforeAll
    static void setup() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        budgetService = new BudgetServiceImpl(jsonStorageService);
        transactionService = new TransactionServiceImpl();
    }

    /**
     * Cleans up all budgets and transactions from storage before each test.
     */
    @BeforeEach
    void cleanUp() {
        jsonStorageService.findAll(Budget.class)
                .forEach(b -> jsonStorageService.delete(b, Budget.class));
        jsonStorageService.findAll(TransactionDto.class)
                .forEach(t -> jsonStorageService.delete(t, TransactionDto.class));
        log.info("Cleaned up all budgets before test");
    }

    /**
     * Tests adding a new budget to the storage.
     */
    @Test
    @Order(1)
    void testAddBudget() {
        Budget budget = new Budget("budget1",
                "user1",
                "Transportation",
                1000,
                0.8,
                "");
        budgetService.addBudget(budget);
        Assertions.assertEquals("budget1", budgetService.getBudgetsByUserId("user1").getFirst().getId());
    }

    /**
     * Tests updating an existing budget in the storage.
     */
    @Test
    @Order(2)
    void testUpdateBudget() {
        Budget budget = new Budget("budget2",
                "user2",
                "Transportation",
                1000,
                0.8,
                "");
        budgetService.addBudget(budget);
        budget.setBudgetAmount(2000);
        budgetService.updateBudget(budget);
        Assertions.assertEquals(2000, budgetService.getBudgetByCategory("user2", "Transportation").getBudgetAmount());
    }

    /**
     * Tests removing a budget from the storage.
     */
    @Test
    @Order(3)
    void testRemoveBudget() {
        Budget budget = new Budget("budget3",
                "user3",
                "Transportation",
                1000,
                0.8,
                "");
        budgetService.addBudget(budget);
        budgetService.removeBudget("budget3");
        Assertions.assertTrue(budgetService.getBudgetsByUserId("user3").isEmpty());
    }

    /**
     * Tests if the budget is exceeded after adding an expense transaction.
     *
     * @throws ServiceException if calculation fails
     */
    @Test
    @Order(4)
    void testIsBudgetExceeded() throws ServiceException {
        Budget budget = new Budget("budget4",
                "user4",
                "Transportation",
                1000,
                0.8,
                "");
        budgetService.addBudget(budget);
        transactionService.addTransactionManually("user4",
                "Expense",
                "2000",
                LocalDateTime.now(),
                "Train",
                "Transportation",
                "account1",
                "remarks");
        Assertions.assertTrue(budgetService.isBudgetExceeded(budget));
    }

    /**
     * Tests if the alert threshold for the budget is reached after adding an expense transaction.
     *
     * @throws ServiceException if calculation fails
     */
    @Test
    @Order(5)
    void testIsAlertBudget() throws ServiceException {
        Budget budget = new Budget("budget5",
                "user5",
                "Transportation",
                1000,
                0.8,
                "");
        budgetService.addBudget(budget);
        transactionService.addTransactionManually("user5",
                "Expense",
                "900",
                LocalDateTime.now(),
                "Train",
                "Transportation",
                "account1",
                "remarks");
        Assertions.assertTrue(budgetService.isAlertBudget(budget));
    }

    /**
     * Tests calculating the total budget for a user.
     */
    @Test
    @Order(6)
    void testGetTotalBudget() {
        Budget budget1 = new Budget("budget6_1",
                "user6",
                "Transportation",
                1000,
                0.8,
                "");
        Budget budget2 = new Budget("budget6_2",
                "user6",
                "Food",
                2000,
                0.8,
                "");
        budgetService.addBudget(budget1);
        budgetService.addBudget(budget2);
        Assertions.assertEquals(3000, budgetService.getTotalBudget("user6"));
    }

    /**
     * Tests calculating the total spent amount for a user.
     *
     * @throws ServiceException if calculation fails
     */
    @Test
    @Order(7)
    void testGetTotalSpent() throws ServiceException {
        Budget budget = new Budget("budget7",
                "user7",
                "Transportation",
                1000,
                0.8,
                "");
        budgetService.addBudget(budget);
        transactionService.addTransactionManually("user7",
                "Expense",
                "500",
                LocalDateTime.now(),
                "Train",
                "Transportation",
                "account1",
                "remarks");
        transactionService.addTransactionManually("user7",
                "Expense",
                "300",
                LocalDateTime.now(),
                "Bus",
                "Transportation",
                "account1",
                "remarks");
        Assertions.assertEquals(800, budgetService.getTotalSpent("user7"));
    }

    /**
     * Tests calculating the monthly spent amount for a user.
     *
     * @throws ServiceException if calculation fails
     */
    @Test
    @Order(8)
    void testGetMonthlySpent() throws ServiceException {
        Budget budget = new Budget("budget8",
                "user8",
                "Transportation",
                1000,
                0.8,
                "");
        budgetService.addBudget(budget);
        transactionService.addTransactionManually("user8",
                "Expense",
                "500",
                LocalDateTime.now(),
                "Train",
                "Transportation",
                "account1",
                "remarks");
        Assertions.assertEquals(500, budgetService.getMonthlySpent("user8"));
    }

    /**
     * Tests calculating the total alert count for a user.
     *
     * @throws ServiceException if calculation fails
     */
    @Test
    @Order(9)
    void testGetTotalAlertCount() throws ServiceException {
        Budget budget1 = new Budget("budget9_1",
                "user9",
                "Transportation",
                1000,
                0.8,
                "");
        Budget budget2 = new Budget("budget9_2",
                "user9",
                "Food",
                1000,
                0.8,
                "");
        budgetService.addBudget(budget1);
        budgetService.addBudget(budget2);

        transactionService.addTransactionManually("user9",
                "Expense",
                "900",
                LocalDateTime.now(),
                "Train",
                "Transportation",
                "account1",
                "remarks");
        transactionService.addTransactionManually("user9",
                "Expense",
                "900",
                LocalDateTime.now(),
                "Dinner",
                "Food",
                "account1",
                "remarks");

        Assertions.assertEquals(1, budgetService.getTotalAlertCount("user9"));
    }

    /**
     * Tests calculating the daily available budget for a user.
     *
     * @throws ServiceException if calculation fails
     */
    @Test
    @Order(10)
    void testGetDailyAvailableBudget() throws ServiceException {
        Budget budget = new Budget("budget10",
                "user10",
                "Transportation",
                1000,
                0.8,
                "");
        budgetService.addBudget(budget);
        transactionService.addTransactionManually("user10",
                "Expense",
                "400",
                LocalDateTime.now(),
                "Train",
                "Transportation",
                "account1",
                "remarks");

        double dailyAvailable = budgetService.getDailyAvailableBudget("user10");
        Assertions.assertTrue(dailyAvailable > 0);
    }

    /**
     * Tests calculating the number of days left in the budget period for a user.
     */
    @Test
    @Order(11)
    void testGetLeftDays() {
        Budget budget = new Budget("budget11",
                "user11",
                "Transportation",
                1000,
                0.8,
                "");
        budgetService.addBudget(budget);

        int leftDays = budgetService.getLeftDays("user11");
        Assertions.assertTrue(leftDays > 0);
    }

    /**
     * Tests calculating the total budget by category for a user.
     */
    @Test
    @Order(12)
    void testGetTotalBudgetByCategory() {
        Budget budget1 = new Budget("budget12_1",
                "user12",
                "Transportation",
                1000,
                0.8,
                "");
        Budget budget2 = new Budget("budget12_2",
                "user12",
                "Transportation",
                2000,
                0.8,
                "");
        budgetService.addBudget(budget1);
        budgetService.addBudget(budget2);

        Assertions.assertEquals(3000, budgetService.getTotalBudgetByCategory("user12", "Transportation"));
    }

    /**
     * Tests calculating the total spent amount by category for a user.
     *
     * @throws ServiceException if calculation fails
     */
    @Test
    @Order(13)
    void testGetTotalSpentByCategory() throws ServiceException {
        Budget budget = new Budget("budget13",
                "user13",
                "Transportation",
                1000,
                0.8,
                "");
        budgetService.addBudget(budget);

        transactionService.addTransactionManually("user13",
                "Expense",
                "300",
                LocalDateTime.now(),
                "Train",
                "Transportation",
                "account1",
                "remarks");
        transactionService.addTransactionManually("user13",
                "Expense",
                "400",
                LocalDateTime.now(),
                "Bus",
                "Transportation",
                "account1",
                "remarks");

        Assertions.assertEquals(700, budgetService.getTotalSpentByCategory("user13", "Transportation"));
    }
}
