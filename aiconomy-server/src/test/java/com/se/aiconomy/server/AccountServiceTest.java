package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.model.dto.TransactionDto;
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

/**
 * Unit tests for {@link AccountService} and {@link JSONStorageService} related to account operations.
 * <p>
 * This class tests CRUD operations, account retrieval, and calculation functionalities for accounts.
 * </p>
 */
public class AccountServiceTest {
    private static final Logger log = LoggerFactory.getLogger(BudgetServiceTest.class);
    private static JSONStorageService jsonStorageService;
    private static AccountService accountService;

    /**
     * Initializes the storage service and account service before all tests.
     */
    @BeforeAll
    static void setup() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        accountService = new AccountServiceImpl(jsonStorageService);
        jsonStorageService.initializeCollection(Account.class);
        log.info("JSONStorageService initialized for Budget");
    }

    /**
     * Cleans up all accounts from storage before each test.
     */
    @BeforeEach
    void cleanUp() {
        jsonStorageService.findAll(Account.class)
                .forEach(budget -> jsonStorageService.delete(budget, Account.class));
        log.info("Cleaned up all budgets before test");
    }

    /**
     * Tests adding a new account to the storage.
     */
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

    /**
     * Tests updating an existing account in the storage.
     */
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
        Assertions.assertTrue(jsonStorageService.findById("account2", Account.class)
                .map(a -> "account2_updated".equals(a.getAccountName()))
                .orElse(false));
        log.info("Updated account2");
    }

    /**
     * Tests removing an account from the storage.
     */
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

    /**
     * Tests retrieving accounts by user ID.
     */
    @Test
    @Order(4)
    void testGetAccountsByUserId() {
        Account account1 = new Account();
        account1.setId("account4");
        account1.setUserId("user4");
        account1.setBankName("bank4");
        account1.setAccountType("credit");
        account1.setAccountName("account4");
        account1.setBalance(3000.0);
        account1.setCreditLimit(5000.0);
        account1.setCurrentDebt(1000.0);
        account1.setPaymentDueDate(LocalDateTime.now());
        account1.setMinimumPayment(100.0);

        Account account2 = new Account();
        account2.setId("account4_1");
        account2.setUserId("user4");
        account2.setBankName("bank4_1");
        account2.setAccountType("savings");
        account2.setAccountName("account4_1");
        account2.setBalance(4000.0);

        jsonStorageService.insert(account1);
        jsonStorageService.insert(account2);
        List<Account> accounts = accountService.getAccountsByUserId("user4");
        Assertions.assertEquals(2, accounts.size());
        log.info("Got accounts for user4");
    }

    /**
     * Tests calculating monthly income for a user.
     *
     * @throws ServiceException if calculation fails
     */
    @Test
    @Order(5)
    void testCalculateMonthlyIncome() throws ServiceException {
        TransactionDto transaction = new TransactionDto();
        transaction.setAmount(String.valueOf(1000.0));
        transaction.setUserId("user7");
        transaction.setTime(LocalDateTime.now());
        transaction.setIncomeOrExpense("Income");

        jsonStorageService.insert(transaction);
        accountService.calculateMonthlySpending("user7", LocalDateTime.now().getMonth());
    }

    /**
     * Tests calculating the credit card due amount for a user.
     */
    @Test
    @Order(6)
    void testCalculateCreditCardDue() {
        Account account = new Account();
        account.setId("account8");
        account.setUserId("user8");
        account.setBankName("bank8");
        account.setAccountType("credit");
        account.setAccountName("account8");
        account.setBalance(3000.0);
        account.setCreditLimit(5000.0);
        account.setCurrentDebt(1000.0);
        account.setPaymentDueDate(LocalDateTime.now());
        account.setMinimumPayment(100.0);

        jsonStorageService.insert(account);
        double creditCardDue = accountService.calculateCreditCardDue("user8");
        Assertions.assertEquals(1000.0, creditCardDue);
        log.info("Calculated credit card due for user4");
    }

    /**
     * Tests calculating the next due date for a user's credit card accounts.
     */
    @Test
    @Order(7)
    void testCalculateNextDueDate() {
        Account account1 = new Account("account9", "user9", "bank9", "credit", "account9", 1000.0, 5000.0, 1000.0, LocalDateTime.of(2026, 1, 1, 0, 0), 100.0);
        Account account2 = new Account("account10", "user9", "bank10", "credit", "account10", 2000.0, 5000.0, 1000.0, LocalDateTime.of(2026, 2, 1, 0, 0), 100.0);

        jsonStorageService.insert(account1);
        jsonStorageService.insert(account2);
        LocalDateTime nextDueDate = accountService.calculateNextDueDate("user9");
        Assertions.assertEquals(LocalDateTime.of(2026, 1, 1, 0, 0), nextDueDate);
        log.info("Calculated next due date for user9");
    }
}
