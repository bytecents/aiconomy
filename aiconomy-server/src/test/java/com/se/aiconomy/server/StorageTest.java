package com.se.aiconomy.server;

import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for {@link JSONStorageService} with {@link TransactionDto} entity.
 * <p>
 * This class tests the basic CRUD operations and upsert functionality
 * for transaction data using the JSONStorageService implementation.
 * </p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StorageTest {
    /**
     * Logger instance for logging test information.
     */
    private static final Logger log = LoggerFactory.getLogger(StorageTest.class);

    /**
     * Test transaction ID used in test cases.
     */
    private static final String TEST_TRANSACTION_ID = "T2025041001";

    /**
     * Test merchant order ID used in test cases.
     */
    private static final String TEST_MERCHANT_ORDER_ID = "M2025041001";

    /**
     * Test transaction time used in test cases.
     */
    private static final LocalDateTime TEST_TRANSACTION_TIME = LocalDateTime.of(2025, 4, 10, 9, 23, 11);

    /**
     * JSON storage service instance used for persisting transactions.
     */
    private static JSONStorageService jsonStorageService;

    /**
     * Initializes the JSON storage service before all tests.
     */
    @BeforeAll
    static void setup() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        if (jsonStorageService.initializeCollection(TransactionDto.class)) {
            log.info("Collection initialized for TransactionDto");
        }
        log.info("JSONStorageService initialized with location: {}",
                System.getProperty("jsonStorage.location"));
    }

    /**
     * Cleans up all transaction data before each test to ensure test isolation.
     */
    @BeforeEach
    void cleanupBeforeTest() {
        jsonStorageService.findAll(TransactionDto.class)
                .forEach(instance -> jsonStorageService.delete(instance, TransactionDto.class));
        log.info("Cleaned up test data");
    }

    /**
     * Tests inserting a transaction into the storage.
     * <p>
     * Asserts that the transaction can be found after insertion and its fields match the expected values.
     * </p>
     */
    @Test
    @Order(1)
    void testInsertTransaction() {
        TransactionDto transaction = createSampleTransaction();
        TransactionDto inserted = jsonStorageService.insert(transaction);

        Optional<TransactionDto> found = jsonStorageService.findById(TEST_TRANSACTION_ID, TransactionDto.class);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(TEST_TRANSACTION_ID, found.get().getId());
        Assertions.assertEquals("iPhone 15", found.get().getProduct());
        log.info("Successfully tested transaction insertion");
    }

    /**
     * Tests updating a transaction in the storage.
     * <p>
     * Inserts a transaction, updates its amount and status, and asserts the changes are persisted.
     * </p>
     */
    @Test
    @Order(2)
    void testUpdateTransaction() {
        // First insert
        TransactionDto transaction = createSampleTransaction();
        jsonStorageService.insert(transaction);

        // Update
        transaction.setAmount("2999.99");
        transaction.setStatus("已完成");
        TransactionDto updated = jsonStorageService.update(transaction, TransactionDto.class);

        // Verify
        Optional<TransactionDto> found = jsonStorageService.findById(TEST_TRANSACTION_ID, TransactionDto.class);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("2999.99", found.get().getAmount());
        Assertions.assertEquals("已完成", found.get().getStatus());
        log.info("Successfully tested transaction update");
    }

    /**
     * Tests deleting a transaction from the storage.
     * <p>
     * Inserts and then deletes a transaction, asserting it cannot be found afterwards.
     * </p>
     */
    @Test
    @Order(3)
    void testDeleteTransaction() {
        TransactionDto transaction = createSampleTransaction();
        jsonStorageService.insert(transaction);

        jsonStorageService.delete(transaction, TransactionDto.class);

        Optional<TransactionDto> found = jsonStorageService.findById(TEST_TRANSACTION_ID, TransactionDto.class);
        Assertions.assertTrue(found.isEmpty());
        log.info("Successfully tested transaction deletion");
    }

    /**
     * Tests retrieving all transactions from the storage.
     * <p>
     * Inserts multiple transactions and asserts the total count matches the number inserted.
     * </p>
     */
    @Test
    @Order(4)
    void testFindAllTransactions() {
        // Insert multiple transactions
        TransactionDto transaction1 = createSampleTransaction();
        TransactionDto transaction2 = createSampleTransaction();
        transaction2.setId("T2025041002");
        transaction2.setMerchantOrderId("M2025041002");

        jsonStorageService.insert(transaction1);
        jsonStorageService.insert(transaction2);

        List<TransactionDto> transactions = jsonStorageService.findAll(TransactionDto.class);
        Assertions.assertEquals(2, transactions.size());
        log.info("Successfully tested finding all transactions");
    }

    /**
     * Tests upserting a transaction in the storage.
     * <p>
     * Asserts that upsert can both insert and update a transaction.
     * </p>
     */
    @Test
    @Order(5)
    void testUpsertTransaction() {
        TransactionDto transaction = createSampleTransaction();

        // Test initial insert via upsert
        jsonStorageService.upsert(transaction);
        Optional<TransactionDto> found = jsonStorageService.findById(TEST_TRANSACTION_ID, TransactionDto.class);
        Assertions.assertTrue(found.isPresent());

        // Test update via upsert
        transaction.setRemark("Updated via upsert");
        jsonStorageService.upsert(transaction);
        found = jsonStorageService.findById(TEST_TRANSACTION_ID, TransactionDto.class);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("Updated via upsert", found.get().getRemark());
        log.info("Successfully tested transaction upsert");
    }

    /**
     * Tests inserting a transaction with null fields.
     * <p>
     * Asserts that the transaction is stored and null fields remain null.
     * </p>
     */
    @Test
    @Order(6)
    void testTransactionWithNullFields() {
        TransactionDto transaction = new TransactionDto();
        transaction.setId(TEST_TRANSACTION_ID);
        transaction.setTime(TEST_TRANSACTION_TIME);
        // Deliberately leave other fields null

        jsonStorageService.insert(transaction);

        Optional<TransactionDto> found = jsonStorageService.findById(TEST_TRANSACTION_ID, TransactionDto.class);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertNull(found.get().getAmount());
        Assertions.assertNull(found.get().getStatus());
        log.info("Successfully tested transaction with null fields");
    }

    /**
     * Creates a sample {@link TransactionDto} for use in tests.
     *
     * @return a sample TransactionDto instance
     */
    private TransactionDto createSampleTransaction() {
        // Create TransactionDto object using builder pattern
        TransactionDto transaction = TransactionDto.builder()
                .time(TEST_TRANSACTION_TIME)  // Set transaction time
                .type("购物")  // Set transaction type
                .counterparty("Apple Store")  // Set counterparty
                .product("iPhone 15")  // Set product name
                .incomeOrExpense("支出")  // Set income or expense
                .amount("3999.99")  // Set amount
                .paymentMethod("信用卡")  // Set payment method
                .status("待支付")  // Set transaction status
                .merchantOrderId(TEST_MERCHANT_ORDER_ID)  // Set merchant order ID
                .accountId("userAcc001")  // Set account ID
                .billType(DynamicBillType.fromBillType(BillType.EDUCATION))  // Set bill type
                .userId("1")  // Set user ID
                .accountId("userAcc002")  // Set account ID
                .build();  // Build the object

        transaction.setId(TEST_TRANSACTION_ID);
        return transaction;
    }
}
