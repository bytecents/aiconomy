package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.AccountRequestHandler;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.handler.UserRequestHandler;
import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.dto.account.request.AddAccountsRequest;
import com.se.aiconomy.server.model.dto.account.request.GetAccountsByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.GetTransactionByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionClassificationRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionImportRequest;
import com.se.aiconomy.server.model.dto.user.request.UserLoginRequest;
import com.se.aiconomy.server.model.dto.user.request.UserRegisterRequest;
import com.se.aiconomy.server.model.dto.user.response.UserInfo;
import com.se.aiconomy.server.model.entity.Account;
import com.se.aiconomy.server.service.UserService;
import com.se.aiconomy.server.service.impl.UserServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MainTest {
    private static final String TEST_EMAIL = "2022213670@bupt.cn";
    private static final String TEST_PASSWORD = "123456";
    private static JSONStorageService jsonStorageService;
    private static UserService userService;
    private static UserRequestHandler userRequestHandler;
    private static TransactionRequestHandler transactionRequestHandler;
    private static AccountRequestHandler accountRequestHandler;
    private static UserInfo userInfo;

    @BeforeAll
    static void setUp() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        userService = new UserServiceImpl(jsonStorageService);
        userRequestHandler = new UserRequestHandler(userService);
        transactionRequestHandler = new TransactionRequestHandler();
        accountRequestHandler = new AccountRequestHandler();
    }

    @Test
    @Order(1)
    void testUserRegisterOrLogin() {
        // 首先尝试登录
        try {
            UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

            userInfo = userRequestHandler.handleLoginRequest(loginRequest);
            System.out.println("User already exists, logged in successfully: " + userInfo.getEmail());
        } catch (Exception e) {
            // 如果登录失败，则进行注册
            System.out.println("Login failed, proceeding with registration...");

            UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder()
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .birthDate(LocalDate.of(1990, 1, 1))
                .currency("USD")
                .financialGoal(List.of("Save for retirement", "Buy a house"))
                .monthlyIncome(5000.0)
                .mainExpenseType(List.of("Rent", "Groceries"))
                .build();

            userInfo = userRequestHandler.handleRegisterRequest(userRegisterRequest);
            System.out.println("User registered successfully: " + userInfo.getEmail());
        }

        assertNotNull(userInfo);
        assertNotNull(userInfo.getId());
        assertEquals(TEST_EMAIL, userInfo.getEmail());
    }

    @Test
    @Order(2)
    void testTransactionClassification() throws ServiceException {
        TransactionClassificationRequest request = new TransactionClassificationRequest();
        request.setUserId(userInfo.getId());
        request.setFilePath(Objects.requireNonNull(getClass().getClassLoader().getResource("transactions.csv")).getPath());

        List<Map<Transaction, BillType>> classifiedTransactions =
            transactionRequestHandler.handleTransactionClassificationRequest(request);

        assertNotNull(classifiedTransactions);
        assertFalse(classifiedTransactions.isEmpty());
    }

    @Test
    @Order(3)
    void testTransactionImport() throws ServiceException {
        // First classify transactions
        TransactionClassificationRequest classificationRequest = new TransactionClassificationRequest();
        classificationRequest.setUserId(userInfo.getId());
        classificationRequest.setFilePath(Objects.requireNonNull(getClass().getClassLoader().getResource("transactions.csv")).getPath());
        List<Map<Transaction, BillType>> classifiedTransactions =
            transactionRequestHandler.handleTransactionClassificationRequest(classificationRequest);

        // Then import them
        TransactionImportRequest importRequest = new TransactionImportRequest();
        importRequest.setUserId(userInfo.getId());
        importRequest.setAccountId("1");
        importRequest.setTransactions(classifiedTransactions);

        List<Map<Transaction, BillType>> importedTransactions =
            transactionRequestHandler.handleTransactionImportRequest(importRequest);

        assertNotNull(importedTransactions);
        assertEquals(classifiedTransactions.size(), importedTransactions.size());
    }

    @Test
    @Order(4)
    void testGetTransactionsByUserId() throws ServiceException {
        GetTransactionByUserIdRequest request = new GetTransactionByUserIdRequest();
        request.setUserId(userInfo.getId());

        List<TransactionDto> transactions = transactionRequestHandler.handleGetTransactionsByUserId(request);

        assertNotNull(transactions);
    }

    @Test
    @Order(5)
    void testAddAccounts() throws ServiceException {
        List<Account> accountsToAdd = List.of(
            Account.builder()
                .id("1")
                .userId(userInfo.getId())
                .bankName("Bank of America")
                .accountType("Saving")
                .accountName("John's Savings Account")
                .balance(5000.00)
                .creditLimit(2000.00)
                .currentDebt(300.00)
                .paymentDueDate(LocalDateTime.of(2025, 5, 15, 23, 59))
                .minimumPayment(50.00)
                .build(),

            Account.builder()
                .id("2")
                .userId(userInfo.getId())
                .bankName("Chase")
                .accountType("Checking")
                .accountName("Alice's Checking Account")
                .balance(1000.00)
                .creditLimit(5000.00)
                .currentDebt(1200.00)
                .paymentDueDate(LocalDateTime.of(2025, 6, 1, 23, 59))
                .minimumPayment(100.00)
                .build(),

            Account.builder()
                .id("3")
                .userId(userInfo.getId())
                .bankName("Wells Fargo")
                .accountType("Credit")
                .accountName("Bob's Credit Account")
                .balance(0.00)
                .creditLimit(15000.00)
                .currentDebt(5000.00)
                .paymentDueDate(LocalDateTime.of(2025, 4, 30, 23, 59))
                .minimumPayment(250.00)
                .build()
        );

        AddAccountsRequest request = new AddAccountsRequest();
        request.setUserId(userInfo.getId());
        request.setAccounts(accountsToAdd);

        List<Account> addedAccounts = accountRequestHandler.handleAddAccountRequest(request);

        assertNotNull(addedAccounts);
        assertEquals(accountsToAdd.size(), addedAccounts.size());
    }

    @Test
    @Order(6)
    void testGetAccountsByUserId() throws ServiceException {
        GetAccountsByUserIdRequest request = new GetAccountsByUserIdRequest();
        request.setUserId(userInfo.getId());

        List<Account> accounts = accountRequestHandler.handleGetAccountsByUserIdRequest(request);

        assertNotNull(accounts);
        assertFalse(accounts.isEmpty());
    }
}