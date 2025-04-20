package com.se.aiconomy.server;

import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.model.dto.TransactionDto;
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
    private static final String TEST_TRANSACTION_ID = "T2025041001";
    private static final String TEST_MERCHANT_ORDER_ID = "M2025041001";
    private static final LocalDateTime TEST_TRANSACTION_TIME = LocalDateTime.of(2025, 4, 10, 9, 23, 11);
    private static JSONStorageService jsonStorageService;

    @BeforeAll
    static void setup() {
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        if (jsonStorageService.initializeCollection(TransactionDto.class)) {
            log.info("Collection initialized for TransactionDto");
        }
        log.info("JSONStorageService initialized with location: {}",
            System.getProperty("jsonStorage.location"));
    }

    @BeforeEach
    void cleanupBeforeTest() {
        jsonStorageService.findAll(TransactionDto.class)
            .forEach(instance -> jsonStorageService.delete(instance, TransactionDto.class));
        log.info("Cleaned up test data");
    }

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

    @Test
    @Order(5)
    void testUpsertTransaction() {
        TransactionDto transaction = createSampleTransaction();

        // Test_odl insert via upsert
        jsonStorageService.upsert(transaction);
        Optional<TransactionDto> found = jsonStorageService.findById(TEST_TRANSACTION_ID, TransactionDto.class);
        Assertions.assertTrue(found.isPresent());

        // Test_odl update via upsert
        transaction.setRemark("Updated via upsert");
        jsonStorageService.upsert(transaction);
        found = jsonStorageService.findById(TEST_TRANSACTION_ID, TransactionDto.class);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("Updated via upsert", found.get().getRemark());
        log.info("Successfully tested transaction upsert");
    }

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

    private TransactionDto createSampleTransaction() {
        // 使用 builder 模式创建 TransactionDto 对象
        TransactionDto transaction = TransactionDto.builder()
            .time(TEST_TRANSACTION_TIME)  // 设置交易时间
            .type("购物")  // 设置交易类型
            .counterparty("Apple Store")  // 设置交易对方
            .product("iPhone 15")  // 设置商品名称
            .incomeOrExpense("支出")  // 设置收入或支出
            .amount("3999.99")  // 设置金额
            .paymentMethod("信用卡")  // 设置支付方式
            .status("待支付")  // 设置交易状态
            .merchantOrderId(TEST_MERCHANT_ORDER_ID)  // 设置商户订单号
            .accountId("userAcc001")  // 设置账户ID
            .billType(BillType.DINING)  // 设置账单类型
            .userId("1")  // 设置用户ID
            .accountId("userAcc002")  // 设置账户ID
            .build();  // 使用 build() 方法创建对象

        transaction.setId(TEST_TRANSACTION_ID);
        return transaction;
    }

}