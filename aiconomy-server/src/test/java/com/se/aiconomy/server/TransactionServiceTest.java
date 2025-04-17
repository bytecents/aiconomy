package com.se.aiconomy.server;

import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.service.TransactionService.TransactionSearchCriteria;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionServiceTest {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceTest.class);
    private static TransactionService transactionService;
    private static TransactionDao transactionDao;
    private final String testCsvPath = Objects.requireNonNull(getClass().getClassLoader().getResource("transactions.csv")).getPath();

    // 测试用时间
    private static final LocalDateTime TEST_TIME = LocalDateTime.parse("2025-04-17T10:11:09");
    private static final String TEST_USER = "CharlesZZY";

    @BeforeAll
    static void setup() {
        transactionService = new TransactionService();
        transactionDao = TransactionDao.getInstance();
    }

    @BeforeEach
    @AfterEach
    void cleanStorage() {
        transactionDao.findAll().forEach(tx ->
            transactionDao.delete(tx));
    }

    @Test
    @Order(1)
    @DisplayName("完整字段导入验证")
    void testCompleteFieldImport() throws Exception {
        List<TransactionDto> transactionList = transactionService.importTransactions(testCsvPath);

        assertEquals(4, transactionList.size(), "成功导入记录数不符");
        assertEquals(4, transactionDao.findAll().size(), "实际存储记录数不符");
    }

    @Test
    @Order(2)
    @DisplayName("测试时间范围内的收支统计")
    void testIncomeAndExpenseStatistics() {
        // 准备测试数据
        createTestTransactions();

        Map<String, String> statistics = transactionService.getIncomeAndExpenseStatistics(
            TEST_TIME.minusDays(1),
            TEST_TIME.plusDays(1)
        );

        assertNotNull(statistics);
        assertTrue(Double.parseDouble(statistics.get("totalIncome")) > 0);
        assertTrue(Double.parseDouble(statistics.get("totalExpense")) > 0);
        assertEquals(
            Double.parseDouble(statistics.get("totalIncome")) - Double.parseDouble(statistics.get("totalExpense")),
            Double.parseDouble(statistics.get("netAmount"))
        );
    }

    @Test
    @Order(3)
    @DisplayName("测试支付方式统计")
    void testPaymentMethodStatistics() {
        // 准备测试数据
        createTestTransactions();

        Map<String, String> statistics = transactionService.getPaymentMethodStatistics();

        assertNotNull(statistics);
        assertTrue(statistics.containsKey("支付宝"));
        assertTrue(Double.parseDouble(statistics.get("支付宝")) > 0);
    }

    @Test
    @Order(4)
    @DisplayName("测试交易对手统计")
    void testCounterpartyStatistics() {
        // 准备测试数据
        createTestTransactions();

        Map<String, String> statistics = transactionService.getCounterpartyStatistics();

        assertNotNull(statistics);
        assertTrue(statistics.containsKey("京东商城"));
        assertTrue(Double.parseDouble(statistics.get("京东商城")) > 0);
    }

    @Test
    @Order(5)
    @DisplayName("测试交易状态更新")
    void testUpdateTransactionStatus() throws Exception {
        // 创建测试交易
        TransactionDto transaction = createTestTransaction();
        String newStatus = "已完成";

        TransactionDto updated = transactionService.updateTransactionStatus(transaction.getId(), newStatus);

        assertNotNull(updated);
        assertEquals(newStatus, updated.getStatus());
    }

    @Test
    @Order(6)
    @DisplayName("测试多条件搜索")
    void testSearchTransactions() {
        // 准备测试数据
        createTestTransactions();

        TransactionSearchCriteria criteria = new TransactionSearchCriteria.Builder()
            .withPaymentMethod("支付宝")
            .withIncomeOrExpense("支出")
            .withDateRange(TEST_TIME.minusDays(1), TEST_TIME.plusDays(1))
            .build();

        List<TransactionDto> results = transactionService.searchTransactions(criteria);

        assertFalse(results.isEmpty());
        results.forEach(transaction -> {
            assertEquals("支付宝", transaction.getPaymentMethod());
            assertEquals("支出", transaction.getIncomeOrExpense());
            assertTrue(transaction.getTime().isAfter(TEST_TIME.minusDays(1)));
            assertTrue(transaction.getTime().isBefore(TEST_TIME.plusDays(1)));
        });
    }

    @Test
    @Order(7)
    @DisplayName("测试删除交易记录")
    void testDeleteTransaction() throws Exception {
        // 创建测试交易
        TransactionDto transaction = createTestTransaction();

        // 删除交易
        transactionService.deleteTransaction(transaction.getId());

        // 验证删除结果
        assertTrue(transactionDao.findById(transaction.getId()).isEmpty());
    }

    @Test
    @Order(8)
    @DisplayName("测试更新不存在的交易记录")
    void testUpdateNonExistentTransaction() {
        String nonExistentId = UUID.randomUUID().toString();

        assertThrows(Exception.class, () ->
            transactionService.updateTransactionStatus(nonExistentId, "已完成"));
    }

    // 辅助方法：创建测试交易记录
    private TransactionDto createTestTransaction() {
        TransactionDto transaction = new TransactionDto();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setTime(TEST_TIME);
        transaction.setType("消费");
        transaction.setCounterparty("京东商城");
        transaction.setProduct("笔记本电脑");
        transaction.setIncomeOrExpense("支出");
        transaction.setAmount("5999.00");
        transaction.setPaymentMethod("支付宝");
        transaction.setStatus("待支付");
        transaction.setMerchantOrderId("JD" + UUID.randomUUID().toString().substring(0, 8));
        transaction.setRemark("测试交易");

        return transactionDao.create(transaction);
    }

    // 辅助方法：创建多条测试交易记录
    private void createTestTransactions() {
        // 创建支出交易
        createTestTransaction();

        // 创建收入交易
        TransactionDto incomeTransaction = new TransactionDto();
        incomeTransaction.setId(UUID.randomUUID().toString());
        incomeTransaction.setTime(TEST_TIME);
        incomeTransaction.setType("工资");
        incomeTransaction.setCounterparty("公司");
        incomeTransaction.setIncomeOrExpense("收入");
        incomeTransaction.setAmount("10000.00");
        incomeTransaction.setPaymentMethod("银行转账");
        incomeTransaction.setStatus("已完成");
        incomeTransaction.setMerchantOrderId("SAL" + UUID.randomUUID().toString().substring(0, 8));
        incomeTransaction.setRemark("月度工资");

        transactionDao.create(incomeTransaction);
    }
}