package com.se.aiconomy.server.dao;

import com.se.aiconomy.server.model.dto.TransactionDto;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link TransactionDao}.
 * <p>
 * This class tests the CRUD and query operations of the TransactionDao using the actual JSONStorageService.
 * Each test ensures the correctness of transaction creation, update, deletion, and various query methods.
 * Test data is cleaned up before and after each test to ensure isolation.
 * </p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionDaoTest {
    private static final Logger log = LoggerFactory.getLogger(TransactionDaoTest.class);
    private static final LocalDateTime TEST_TIME = LocalDateTime.parse("2025-04-17T10:05:11");
    private static TransactionDao transactionDao;
    private static TransactionDto testTransaction;

    /**
     * Initializes the TransactionDao instance before all tests.
     * Asserts that the collection exists.
     */
    @BeforeAll
    static void setUp() {
        transactionDao = TransactionDao.getInstance();
        assertTrue(transactionDao.collectionExists());
    }

    /**
     * Final cleanup after all tests to ensure all data is removed.
     */
    @AfterAll
    static void cleanup() {
        // Ensure all data is cleaned up
        try {
            TransactionDao.getInstance().findAll()
                    .forEach(tx -> TransactionDao.getInstance().delete(tx));
            log.info("Final test data cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error in final cleanup", e);
        }
    }

    /**
     * Cleans up all existing transactions and prepares test data before each test.
     */
    @BeforeEach
    void cleanupBeforeTest() {
        // Remove all existing data
        transactionDao.findAll().forEach(tx ->
                transactionDao.delete(tx));

        // Prepare test data
        prepareTestData();
    }

    /**
     * Prepares a sample transaction for testing and persists it.
     * Asserts that the created transaction has a non-null ID.
     */
    void prepareTestData() {
        testTransaction = new TransactionDto();
        testTransaction.setTime(TEST_TIME);
        testTransaction.setType("消费");
        testTransaction.setCounterparty("京东商城");
        testTransaction.setProduct("笔记本电脑");
        testTransaction.setIncomeOrExpense("支出");
        testTransaction.setAmount("5999.00");
        testTransaction.setPaymentMethod("支付宝");
        testTransaction.setStatus("待支付");
        testTransaction.setMerchantOrderId("JD123456789");
        testTransaction.setRemark("618活动购买");

        // Create test data directly
        testTransaction = transactionDao.create(testTransaction);
        assertNotNull(testTransaction.getId(), "Test transaction should have an ID after creation");
    }

    /**
     * Tests creating a new transaction.
     * Asserts that the created transaction has the expected properties.
     */
    @Test
    @Order(1)
    @DisplayName("测试创建交易记录")
    void testCreateTransaction() {
        TransactionDto newTransaction = new TransactionDto();
        newTransaction.setTime(TEST_TIME);
        newTransaction.setType("消费");
        newTransaction.setAmount("100.00");

        TransactionDto created = transactionDao.create(newTransaction);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(TEST_TIME, created.getTime());
        assertEquals("消费", created.getType());
        assertEquals("100.00", created.getAmount());
    }

    /**
     * Tests finding a transaction by its ID.
     * Asserts that the found transaction matches the test data.
     */
    @Test
    @Order(2)
    @DisplayName("测试根据ID查找交易记录")
    void testFindById() {
        Optional<TransactionDto> found = transactionDao.findById(testTransaction.getId());

        assertTrue(found.isPresent());
        assertEquals(testTransaction.getMerchantOrderId(), found.get().getMerchantOrderId());
    }

    /**
     * Tests updating the status of a transaction.
     * Asserts that the status is updated correctly.
     */
    @Test
    @Order(3)
    @DisplayName("测试更新交易状态")
    void testUpdateStatus() {
        TransactionDto updated = transactionDao.updateStatus(testTransaction.getId(), "已支付");

        assertNotNull(updated);
        assertEquals("已支付", updated.getStatus());
    }

    /**
     * Tests querying transactions by payment method.
     * Asserts that the test transaction is included in the result.
     */
    @Test
    @Order(4)
    @DisplayName("测试按支付方式查询")
    void testFindByPaymentMethod() {
        List<TransactionDto> transactions = transactionDao.findByPaymentMethod("支付宝");

        assertFalse(transactions.isEmpty());
        assertTrue(transactions.stream()
                .anyMatch(t -> t.getId().equals(testTransaction.getId())));
    }

    /**
     * Tests querying transactions within a time range.
     * Asserts that the test transaction is included in the result.
     */
    @Test
    @Order(5)
    @DisplayName("测试按时间范围查询")
    void testFindByTimeRange() {
        LocalDateTime startTime = TEST_TIME.minusHours(1);
        LocalDateTime endTime = TEST_TIME.plusHours(1);

        List<TransactionDto> transactions = transactionDao.findByTimeRange(startTime, endTime);

        assertFalse(transactions.isEmpty());
        assertTrue(transactions.stream()
                .anyMatch(t -> t.getId().equals(testTransaction.getId())));
    }

    /**
     * Tests querying transactions by counterparty (merchant).
     * Asserts that the test transaction is included in the result.
     */
    @Test
    @Order(6)
    @DisplayName("测试按商户查询")
    void testFindByCounterparty() {
        List<TransactionDto> transactions = transactionDao.findByCounterparty("京东商城");

        assertFalse(transactions.isEmpty());
        assertTrue(transactions.stream()
                .anyMatch(t -> t.getId().equals(testTransaction.getId())));
    }

    /**
     * Tests querying transactions by income or expense type.
     * Asserts that the test transaction is included in the result.
     */
    @Test
    @Order(7)
    @DisplayName("测试按收支类型查询")
    void testFindByIncomeOrExpense() {
        List<TransactionDto> transactions = transactionDao.findByIncomeOrExpense("支出");

        assertFalse(transactions.isEmpty());
        assertTrue(transactions.stream()
                .anyMatch(t -> t.getId().equals(testTransaction.getId())));
    }

    /**
     * Tests querying transactions by product name.
     * Asserts that the test transaction is included in the result.
     */
    @Test
    @Order(8)
    @DisplayName("测试按商品名称查询")
    void testFindByProduct() {
        List<TransactionDto> transactions = transactionDao.findByProduct("笔记本电脑");

        assertFalse(transactions.isEmpty());
        assertTrue(transactions.stream()
                .anyMatch(t -> t.getId().equals(testTransaction.getId())));
    }

    /**
     * Tests querying transactions by merchant order ID.
     * Asserts that the test transaction is included in the result.
     */
    @Test
    @Order(9)
    @DisplayName("测试按商户订单号查询")
    void testFindByMerchantOrderId() {
        List<TransactionDto> transactions = transactionDao.findByMerchantOrderId("JD123456789");

        assertFalse(transactions.isEmpty());
        assertTrue(transactions.stream()
                .anyMatch(t -> t.getId().equals(testTransaction.getId())));
    }

    /**
     * Tests searching transactions by remark keyword.
     * Asserts that the test transaction is included in the result.
     */
    @Test
    @Order(10)
    @DisplayName("测试备注关键字搜索")
    void testSearchByRemark() {
        List<TransactionDto> transactions = transactionDao.searchByRemark("618");

        assertFalse(transactions.isEmpty());
        assertTrue(transactions.stream()
                .anyMatch(t -> t.getId().equals(testTransaction.getId())));
    }

    /**
     * Tests creating a transaction without specifying the time.
     * Asserts that the created transaction has a valid time set automatically.
     */
    @Test
    @Order(11)
    @DisplayName("测试创建没有时间的交易记录")
    void testCreateTransactionWithoutTime() {
        TransactionDto transactionWithoutTime = new TransactionDto();
        transactionWithoutTime.setType("消费");
        transactionWithoutTime.setAmount("100.00");

        TransactionDto created = transactionDao.create(transactionWithoutTime);

        assertNotNull(created);
        assertNotNull(created.getTime());
        // Verify that the time is within a reasonable range
        assertTrue(created.getTime().isAfter(LocalDateTime.now().minusMinutes(1)));
        assertTrue(created.getTime().isBefore(LocalDateTime.now().plusMinutes(1)));
    }

    /**
     * Tests finding a non-existent transaction by ID.
     * Asserts that the result is empty.
     */
    @Test
    @Order(12)
    @DisplayName("测试查询不存在的交易记录")
    void testFindNonExistentTransaction() {
        // Clean all data to ensure a clean test environment
        transactionDao.findAll().forEach(tx -> transactionDao.delete(tx));

        Optional<TransactionDto> notFound = transactionDao.findById("non-existent-id");
        assertFalse(notFound.isPresent());
    }

    /**
     * Cleans up all transactions after each test.
     */
    @AfterEach
    void cleanupAfterTest() {
        // Clean up test data
        transactionDao.findAll().forEach(tx ->
                transactionDao.delete(tx));
    }
}
