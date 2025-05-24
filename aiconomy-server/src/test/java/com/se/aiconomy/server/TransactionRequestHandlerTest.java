package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link TransactionRequestHandler}.
 * <p>
 * This class tests the manual addition of transactions via the TransactionRequestHandler,
 * ensuring that the handler correctly processes valid input and returns the expected TransactionDto.
 * </p>
 */
class TransactionRequestHandlerTest {

    /**
     * Test user ID used in test cases.
     */
    private static final String TEST_USER = "test-user";

    /**
     * Test transaction time used in test cases.
     */
    private static final LocalDateTime TEST_TIME = LocalDateTime.of(2025, 5, 23, 10, 28); // Current time: 2025-05-23 10:28 PDT

    /**
     * TransactionRequestHandler instance under test.
     */
    private TransactionRequestHandler transactionRequestHandler;

    /**
     * Initializes the TransactionRequestHandler and TransactionService before each test.
     */
    @BeforeEach
    void setUp() {
        TransactionService transactionService = new TransactionServiceImpl();
        transactionRequestHandler = new TransactionRequestHandler(transactionService);
    }

    /**
     * Tests the manual addition of a transaction with valid input.
     * <p>
     * Asserts that the returned TransactionDto is not null and all fields match the input values.
     * </p>
     *
     * @throws ServiceException if the service fails to add the transaction
     */
    @Test
    void testHandleAddTransactionManually_ValidInput() throws ServiceException {
        String userId = TEST_USER;
        String incomeOrExpense = "支出";
        String amount = "5999.00";
        LocalDateTime time = TEST_TIME;
        String product = "笔记本电脑";
        String type = "消费";
        String accountId = "userAcc001";
        String remark = "购买新电脑";

        // Call the handler method
        TransactionDto result = transactionRequestHandler.handleAddTransactionManually(
                userId, incomeOrExpense, amount, time, product, type, accountId, remark
        );

        // Verify the result
        assertNotNull(result, "Transaction record should not be null");
        assertNotNull(result.getId(), "Transaction ID should not be null");
        assertEquals(userId, result.getUserId(), "User ID should match");
        assertEquals(incomeOrExpense, result.getIncomeOrExpense(), "Income or expense type should match");
        assertEquals(amount, result.getAmount(), "Amount should match");
        assertEquals(time, result.getTime(), "Time should match");
        assertEquals(product, result.getProduct(), "Product should match");
        assertEquals(type, result.getType(), "Transaction type should match");
        assertEquals(accountId, result.getAccountId(), "Account ID should match");
        assertEquals(remark, result.getRemark(), "Remark should match");
    }
}
