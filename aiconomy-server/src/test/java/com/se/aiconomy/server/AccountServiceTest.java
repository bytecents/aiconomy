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
        account.setId("1");
        account.setUserId("user1");
        account.setBalance(1000.0);
        account.setAccountType("Savings");

        accountService.addBankAccount(account);

        Account fetchedAccount = accountService.getAccountById("1");
        Assertions.assertNotNull(fetchedAccount, "Account should be added successfully.");
        Assertions.assertEquals("1", fetchedAccount.getId());
        Assertions.assertEquals("user1", fetchedAccount.getUserId());
        Assertions.assertEquals(1000.0, fetchedAccount.getBalance());
    }

    @Test
    @Order(2)
    void testUpdateAccount() {
        Account account = new Account();
        account.setId("2");
        account.setUserId("user1");
        account.setBalance(500.0);
        account.setAccountType("Checking");

        accountService.addBankAccount(account);

        account.setBalance(1000.0);  // 更新余额
        accountService.updateAccount(account);

        Account fetchedAccount = accountService.getAccountById("2");
        Assertions.assertEquals(1000.0, fetchedAccount.getBalance(), "Account balance should be updated.");
    }

    @Test
    @Order(3)
    void testDeleteAccount() {
        Account account = new Account();
        account.setId("3");
        account.setUserId("user1");
        account.setBalance(2000.0);
        account.setAccountType("Savings");

        accountService.addBankAccount(account);
        accountService.deleteAccount("3");

        // 检查账户是否已被删除
        Assertions.assertThrows(RuntimeException.class, () -> accountService.getAccountById("3"),
                "Account should be deleted successfully.");
    }

    @Test
    @Order(4)
    void testGetAccountsByUserId() {
        Account account1 = new Account();
        account1.setId("4");
        account1.setUserId("user1");
        account1.setBalance(1000.0);
        account1.setAccountType("Checking");

        Account account2 = new Account();
        account2.setId("5");
        account2.setUserId("user1");
        account2.setBalance(2000.0);
        account2.setAccountType("Savings");

        accountService.addBankAccount(account1);
        accountService.addBankAccount(account2);

        List<Account> accounts = accountService.getAccountsByUserId("user1");

        Assertions.assertEquals(2, accounts.size(), "User should have 2 accounts.");
        Assertions.assertTrue(accounts.stream().anyMatch(acc -> acc.getId().equals("4")));
        Assertions.assertTrue(accounts.stream().anyMatch(acc -> acc.getId().equals("5")));
    }

    @Test
    @Order(5)
    void testCalculateNetWorth() {
        Account account1 = new Account();
        account1.setId("6");
        account1.setUserId("user2");
        account1.setBalance(1500.0);
        account1.setAccountType("Checking");

        accountService.addBankAccount(account1);


        double netWorth = accountService.calculateNetWorth("user2");

        Assertions.assertEquals(1500.0, netWorth, "Net worth should be calculated correctly.");
    }

    @Test
    @Order(6)
    void testCalculateMonthlySpending() {
        double monthlySpending = accountService.calculateMonthlySpending("user1");

        Assertions.assertTrue(monthlySpending >= 0, "Monthly spending should be a non-negative value.");
    }

    @Test
    @Order(7)
    void testCalculateMonthlyIncome() {

        double monthlyIncome = accountService.calculateMonthlyIncome("user1");

        Assertions.assertTrue(monthlyIncome >= 0, "Monthly income should be a non-negative value.");
    }

    @Test
    @Order(8)
    void testCalculateCreditDue() {
        Account creditAccount = new Account();
        creditAccount.setId("7");
        creditAccount.setUserId("user2");
        creditAccount.setBalance(1000.0);
        creditAccount.setAccountType("CreditCard");
        creditAccount.setCurrentDebt(500.0);

        accountService.addBankAccount(creditAccount);

        double creditDue = accountService.calculateCreditDue("user2");

        Assertions.assertEquals(500.0, creditDue, "Credit due should be calculated correctly.");
    }

    @Test
    @Order(9)
    void testGetLatestPaymentDueDate() {
        Account creditAccount = new Account();
        creditAccount.setId("8");
        creditAccount.setUserId("user2");
        creditAccount.setBalance(2000.0);
        creditAccount.setAccountType("CreditCard");
        creditAccount.setCurrentDebt(1000.0);
        creditAccount.setPaymentDueDate(LocalDateTime.of(2025, 5, 1, 0, 0));

        accountService.addBankAccount(creditAccount);

        LocalDateTime latestPaymentDueDate = accountService.getLatestPaymentDueDate("user2");

        Assertions.assertEquals(LocalDateTime.of(2025, 5, 1, 0, 0), latestPaymentDueDate,
                "The latest payment due date should be returned correctly.");
    }
}
