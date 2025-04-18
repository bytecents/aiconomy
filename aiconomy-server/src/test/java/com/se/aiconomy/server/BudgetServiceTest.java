package com.se.aiconomy.server;

import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BudgetServiceTest {
    private static final Logger log = LoggerFactory.getLogger(BudgetServiceTest.class);
    private static JSONStorageService jsonStorageService;
    private static BudgetService budgetService;

    private static final String TEST_BUDGET_ID = "B2025041101";
    private static final String TEST_USER_ID = "U20250411";

    @BeforeAll
    static void setup() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        budgetService = new BudgetServiceImpl(jsonStorageService);
        jsonStorageService.initializeCollection(Budget.class);
        log.info("JSONStorageService initialized for Budget");
    }

    @BeforeEach
    void cleanUp() {
        jsonStorageService.findAll(Budget.class)
                .forEach(budget -> jsonStorageService.delete(budget, Budget.class));
        log.info("Cleaned up all budgets before test");
    }

    @Test
    @Order(1)
    void testAddBudget() {
        Budget budget = createSampleBudget();
        budgetService.addBudget(budget);

        Optional<Budget> found = jsonStorageService.findById(TEST_BUDGET_ID, Budget.class);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(TEST_BUDGET_ID, found.get().getId());
        Assertions.assertEquals("餐饮", found.get().getBudgetCategory());
        log.info("Successfully tested addBudget");
    }

    @Test
    @Order(2)
    void testUpdateBudget() {
        Budget budget = createSampleBudget();
        budgetService.addBudget(budget);

        budget.setBudgetAmount(5000.0);
        budgetService.updateBudget(budget);

        Budget updated = budgetService.getBudget(TEST_BUDGET_ID);
        Assertions.assertEquals(5000.0, updated.getBudgetAmount());
        log.info("Successfully tested updateBudget");
    }

    @Test
    @Order(3)
    void testGetBudget() {
        Budget budget = createSampleBudget();
        budgetService.addBudget(budget);

        Budget found = budgetService.getBudget(TEST_BUDGET_ID);
        Assertions.assertNotNull(found);
        Assertions.assertEquals(TEST_BUDGET_ID, found.getId());
        log.info("Successfully tested getBudget");
    }

    @Test
    @Order(4)
    void testRemoveBudget() {
        Budget budget = createSampleBudget();
        budgetService.addBudget(budget);

        budgetService.removeBudget(TEST_BUDGET_ID);
        Optional<Budget> found = jsonStorageService.findById(TEST_BUDGET_ID, Budget.class);
        Assertions.assertTrue(found.isEmpty());
        log.info("Successfully tested removeBudget");
    }

    @Test
    @Order(5)
    void testGetBudgetsByUserId() {
        Budget budget1 = createSampleBudget();
        Budget budget2 = createSampleBudget();
        budget2.setId("B2025041102");

        budgetService.addBudget(budget1);
        budgetService.addBudget(budget2);

        List<Budget> userBudgets = budgetService.getBudgetsByUserId(TEST_USER_ID);
        Assertions.assertEquals(2, userBudgets.size());
        log.info("Successfully tested getBudgetsByUserId");
    }

    @Test
    @Order(6)
    void testIsBudgetExceeded() {
        Budget budget = createSampleBudget();
        Assertions.assertTrue(budgetService.isBudgetExceeded(budget, 3000.0));
        Assertions.assertFalse(budgetService.isBudgetExceeded(budget, 1000.0));
        log.info("Successfully tested isBudgetExceeded");
    }

    private Budget createSampleBudget() {
        Budget budget = new Budget();
        budget.setId(TEST_BUDGET_ID);
        budget.setUserId(TEST_USER_ID);
        budget.setBudgetCategory("餐饮");
        budget.setBudgetAmount(2000.0);
        return budget;
    }
}
