package com.se.aiconomy.server;

import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.AccountService;
import com.se.aiconomy.server.service.impl.AccountServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class AccountServiceTest {
    private static final Logger log = LoggerFactory.getLogger(BudgetServiceTest.class);
    private static JSONStorageService jsonStorageService;
    private static AccountService accountService;

    @BeforeAll
    static void setup() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        accountService = new AccountServiceImpl(jsonStorageService);
        jsonStorageService.initializeCollection(Account.class);
        log.info("JSONStorageService initialized for Budget");
    }

    @BeforeEach
    void cleanUp() {
        jsonStorageService.findAll(Account.class)
                .forEach(budget -> jsonStorageService.delete(budget, Account.class));
        log.info("Cleaned up all budgets before test");
    }

    @Test
    @Order(1)
    void testAddAccount() {
        Account account = new Account();
        account.setId("account1");
        account.setUserId("user1");
        account.setBankName("bank1");
        account.setAccountType("checking");
        account.setAccountName("account1");
        account.setBalance(1000.0);

        jsonStorageService.insert(account);
        Assertions.assertTrue(jsonStorageService.findById("account1", Account.class).isPresent());
        log.info("Added account1");
    }

    @Test
    @Order(2)
    void testUpdateAccount() {
        Account account = new Account();
        account.setId("account2");
        account.setUserId("user2");
        account.setBankName("bank2");
        account.setAccountType("savings");
        account.setAccountName("account2");
        account.setBalance(2000.0);

        jsonStorageService.insert(account);
        account.setAccountName("account2_updated");
        jsonStorageService.update(account, Account.class);
        Assertions.assertEquals("account2_updated", jsonStorageService.findById("account2", Account.class).get().getAccountName());
        log.info("Updated account2");
    }

    @Test
    @Order(3)
    void testRemoveAccount() {
        Account account = new Account();
        account.setId("account3");

        jsonStorageService.insert(account);
        jsonStorageService.delete(account, Account.class);
        Assertions.assertFalse(jsonStorageService.findById("account3", Account.class).isPresent());
        log.info("Removed account3");
    }

    @Test
    @Order(4)
    void testCalculateCreditCardDue() {
        Account account = new Account();
        account.setId("account4");
        account.setUserId("user4");
        account.setBankName("bank4");
        account.setAccountType("credit");
        account.setAccountName("account4");
        account.setBalance(3000.0);
        account.setCreditLimit(5000.0);
        account.setCurrentDebt(1000.0);
        account.setPaymentDueDate(LocalDateTime.now());
        account.setMinimumPayment(100.0);

        jsonStorageService.insert(account);
        double creditCardDue = accountService.calculateCreditCardDue("user4");
        Assertions.assertEquals(1000.0, creditCardDue);
        log.info("Calculated credit card due for user4");
    }
}
