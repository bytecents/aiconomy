package com.se.aiconomy.server;

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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BudgetServiceTest {
    private static final Logger log = LoggerFactory.getLogger(Budget.class);
    private static JSONStorageService jsonStorageService;

    private static BudgetService budgetService;

    @BeforeAll
    static void setup() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        TransactionService transactionService = new TransactionServiceImpl();
        budgetService = new BudgetServiceImpl(jsonStorageService, transactionService);
    }

    @BeforeEach
    void cleanUp() {
        jsonStorageService.findAll(Budget.class)
                .forEach(b -> jsonStorageService.delete(b, Budget.class));
        log.info("Cleaned up all budgets before test");
    }

    @Test
    @Order(1)
    void testAddBudget() {
        Budget budget = new Budget();
        budget.setId("budget1");
        budget.setUserId("user1");
        budget.setBudgetCategory("Shopping");
        budget.setBudgetAmount(1000.0);
        budget.setAlertSettings(0.8);
        budget.setNotes("");
        budgetService.addBudget(budget);
        Assertions.assertTrue(jsonStorageService.findAll(Budget.class)
                .stream()
                .anyMatch(b -> b.getId().equals(budget.getId())));
        log.info("Successfully added budget");
    }

    @Test
    @Order(2)
    void testUpdateBudget() {
        Budget budget = new Budget();
        budget.setId("budget1");
        budget.setUserId("user1");
        budget.setBudgetCategory("Shopping");
        budget.setBudgetAmount(1000.0);
        budget.setAlertSettings(0.8);
        budget.setNotes("");
        jsonStorageService.insert(budget);
        budget.setBudgetAmount(1500.0);
        budgetService.updateBudget(budget);
        Assertions.assertTrue(jsonStorageService.findAll(Budget.class)
                .stream()
                .anyMatch(b -> b.getId().equals(budget.getId()) && b.getBudgetAmount() == 1500.0));
        log.info("Successfully updated budget");
    }

    @Test
    @Order(3)
    void testRemoveBudget() {
        budgetService.removeBudget("budget1");
        Assertions.assertFalse(jsonStorageService.findAll(Budget.class)
                .stream()
                .anyMatch(b -> b.getId().equals("budget1")));
        log.info("Successfully removed budget");
    }
}
