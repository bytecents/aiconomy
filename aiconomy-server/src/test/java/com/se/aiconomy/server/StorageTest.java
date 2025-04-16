package com.se.aiconomy.server;

import com.se.aiconomy.server.model.entity.Transaction;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StorageTest {
    private static final Logger log = LoggerFactory.getLogger(StorageTest.class);
    private static JSONStorageService jsonStorageService;

    private static final String TEST_TRANSACTION_ID = "T2025041001";
    private static final String TEST_MERCHANT_ORDER_ID = "M2025041001";
    private static final LocalDateTime TEST_TRANSACTION_TIME = LocalDateTime.of(2025, 4, 10, 9, 23, 11);

    @BeforeAll
    static void setup() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        if (jsonStorageService.initializeCollection(Transaction.class)) {
            log.info("Collection initialized for Transaction");
        }
        log.info("JSONStorageService initialized with location: {}",
            System.getProperty("jsonStorage.location"));
    }

    @BeforeEach
    void cleanupBeforeTest() {
        jsonStorageService.findAll(Transaction.class)
            .forEach(instance -> jsonStorageService.delete(instance, Transaction.class));
        log.info("Cleaned up test data");
    }

    @Test
    @Order(1)
    void testInsertTransaction() {
        Transaction transaction = createSampleTransaction();
        Transaction inserted = jsonStorageService.insert(transaction);

        Optional<Transaction> found = jsonStorageService.findById(TEST_TRANSACTION_ID, Transaction.class);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(TEST_TRANSACTION_ID, found.get().getId());
        Assertions.assertEquals("iPhone 15", found.get().getProduct());
        log.info("Successfully tested transaction insertion");
    }

    @Test
    @Order(2)
    void testUpdateTransaction() {
        // First insert
        Transaction transaction = createSampleTransaction();
        jsonStorageService.insert(transaction);

        // Update
        transaction.setAmount("2999.99");
        transaction.setStatus("已完成");
        Transaction updated = jsonStorageService.update(transaction, Transaction.class);

        // Verify
        Optional<Transaction> found = jsonStorageService.findById(TEST_TRANSACTION_ID, Transaction.class);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("2999.99", found.get().getAmount());
        Assertions.assertEquals("已完成", found.get().getStatus());
        log.info("Successfully tested transaction update");
    }

    @Test
    @Order(3)
    void testDeleteTransaction() {
        Transaction transaction = createSampleTransaction();
        jsonStorageService.insert(transaction);

        jsonStorageService.delete(transaction, Transaction.class);

        Optional<Transaction> found = jsonStorageService.findById(TEST_TRANSACTION_ID, Transaction.class);
        Assertions.assertTrue(found.isEmpty());
        log.info("Successfully tested transaction deletion");
    }

    @Test
    @Order(4)
    void testFindAllTransactions() {
        // Insert multiple transactions
        Transaction transaction1 = createSampleTransaction();
        Transaction transaction2 = createSampleTransaction();
        transaction2.setId("T2025041002");
        transaction2.setMerchantOrderId("M2025041002");

        jsonStorageService.insert(transaction1);
        jsonStorageService.insert(transaction2);

        List<Transaction> transactions = jsonStorageService.findAll(Transaction.class);
        Assertions.assertEquals(2, transactions.size());
        log.info("Successfully tested finding all transactions");
    }

    @Test
    @Order(5)
    void testUpsertTransaction() {
        Transaction transaction = createSampleTransaction();

        // Test_odl insert via upsert
        jsonStorageService.upsert(transaction);
        Optional<Transaction> found = jsonStorageService.findById(TEST_TRANSACTION_ID, Transaction.class);
        Assertions.assertTrue(found.isPresent());

        // Test_odl update via upsert
        transaction.setRemark("Updated via upsert");
        jsonStorageService.upsert(transaction);
        found = jsonStorageService.findById(TEST_TRANSACTION_ID, Transaction.class);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("Updated via upsert", found.get().getRemark());
        log.info("Successfully tested transaction upsert");
    }

    @Test
    @Order(6)
    void testExtraFields() {
        Transaction transaction = createSampleTransaction();

        // Add extra fields
        transaction.addExtraField("promotionCode", "SPRING2025");
        transaction.addExtraField("customerLevel", "VIP");
        transaction.addExtraField("discountRate", 0.85);

        jsonStorageService.insert(transaction);

        // Verify extra fields
        Optional<Transaction> found = jsonStorageService.findById(TEST_TRANSACTION_ID, Transaction.class);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("SPRING2025", found.get().getExtraField("promotionCode"));
        Assertions.assertEquals("VIP", found.get().getExtraField("customerLevel"));
        Assertions.assertEquals(0.85, found.get().getExtraField("discountRate"));
        log.info("Successfully tested extra fields");
    }

    @Test
    @Order(7)
    void testTransactionWithNullFields() {
        Transaction transaction = new Transaction();
        transaction.setId(TEST_TRANSACTION_ID);
        transaction.setTime(TEST_TRANSACTION_TIME);
        // Deliberately leave other fields null

        jsonStorageService.insert(transaction);

        Optional<Transaction> found = jsonStorageService.findById(TEST_TRANSACTION_ID, Transaction.class);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertNull(found.get().getAmount());
        Assertions.assertNull(found.get().getStatus());
        log.info("Successfully tested transaction with null fields");
    }

    private Transaction createSampleTransaction() {
        Transaction transaction = new Transaction(
            TEST_TRANSACTION_TIME,
            "购物",
            "Apple Store",
            "iPhone 15",
            "支出",
            "3999.99",
            "信用卡",
            "待支付",
            TEST_TRANSACTION_ID,
            TEST_MERCHANT_ORDER_ID,
            "首次购买iPhone"
        );
        transaction.setId(TEST_TRANSACTION_ID); // 设置Identifiable接口要求的Id
        return transaction;
    }
}