package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl.TransactionSearchCriteria;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionServiceCSVTest {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceCSVTest.class);
    private static final LocalDateTime TEST_TIME = LocalDateTime.parse("2025-04-17T10:11:09");
    private static final String TEST_USER = "Aurelia";
    private static TransactionServiceImpl transactionService;
    private static TransactionDao transactionDao;
    private final String testCsvPath = Objects.requireNonNull(getClass().getClassLoader().getResource("transactions.csv")).getPath();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeAll
    static void setup() {
        transactionService = new TransactionServiceImpl();
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

        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder()
                .paymentMethod("支付宝")
                .incomeOrExpense("支出")
                .startTime(TEST_TIME.minusDays(1))
                .endTime(TEST_TIME.plusDays(1))
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

    @Test
    @Order(9)
    @DisplayName("测试根据 accountId 查找所有交易记录")
    void testGetTransactionsByAccountId() throws ServiceException {
        createTestTransactions();
        String accountId = "userAcc002";
        List<TransactionDto> transactions = transactionService.getTransactionsByAccountId(accountId, TEST_USER);

        assertNotNull(transactions, "交易记录为空");
        assertFalse(transactions.isEmpty(), "没有找到任何交易记录");

        transactions.forEach(transaction -> {
            assertEquals(accountId, transaction.getAccountId(), "accountId 不匹配");
            assertEquals(TEST_USER, transaction.getUserId(), "userId 不匹配");
        });

        transactions.forEach(transaction -> log.info("Found transaction with accountId {}: {}", accountId, transaction));
    }

    @Test
    @Order(10)
    @DisplayName("测试导出交易记录到 CSV 文件")
    void testExportTransactionsToCsv() throws Exception {
        createTestTransactions();
        String exportFilePath = System.getProperty("java.io.tmpdir") + "/exported_transactions.csv";
        transactionService.exportTransactionsToCsv(exportFilePath);

        File exportedFile = new File(exportFilePath);
        assertTrue(exportedFile.exists(), "导出的 CSV 文件不存在");

        List<TransactionDto> exportedTransactions = transactionService.importTransactions(exportFilePath);
        assertEquals(2, exportedTransactions.size(), "导出的交易记录数不符");

        TransactionDto firstTransaction = exportedTransactions.stream()
                .filter(tx -> "消费".equals(tx.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(firstTransaction, "未找到消费交易");
        assertEquals("京东商城", firstTransaction.getCounterparty(), "交易对象不匹配");
        assertEquals("2025-04-17T10:11:09", firstTransaction.getTime().format(FORMATTER), "交易时间不匹配");
        assertEquals(TEST_USER, firstTransaction.getUserId(), "用户 ID 不匹配");

        if (exportedFile.exists()) {
            exportedFile.delete();
        }
    }

    @Test
    @Order(11)
    @DisplayName("测试导出空交易记录到 CSV")
    void testExportEmptyTransactionsToCsv() {
        String exportFilePath = System.getProperty("java.io.tmpdir") + "/empty_transactions.csv";
        assertThrows(ServiceException.class,
                () -> transactionService.exportTransactionsToCsv(exportFilePath),
                "应抛出异常：无交易记录可导出");

        File exportedFile = new File(exportFilePath);
        assertFalse(exportedFile.exists(), "空交易不应创建文件");
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
        transaction.setAccountId("userAcc001");
        transaction.setUserId("Aurelia");
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
        incomeTransaction.setAccountId("userAcc002");
        incomeTransaction.setRemark("月度工资");
        incomeTransaction.setUserId("Aurelia");

        transactionDao.create(incomeTransaction);
    }
}