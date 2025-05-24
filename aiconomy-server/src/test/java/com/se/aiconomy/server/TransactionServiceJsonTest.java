package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.common.utils.JsonUtils;
import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl.TransactionSearchCriteria;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionServiceJsonTest {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceJsonTest.class);
    private static final LocalDateTime TEST_TIME = LocalDateTime.parse("2025-04-17T10:11:09");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final String TEST_USER = "AureliaSKY";
    private static TransactionServiceImpl transactionService;
    private static TransactionDao transactionDao;
    private final String testJsonPath = Objects.requireNonNull(getClass().getClassLoader().getResource("transactions.json")).getPath();
    private final String testJsonString = """
        [
            {
                "id": "12345",
                "time": "2025-04-16T10:30:00",
                "type": "消费",
                "counterparty": "Walmart",
                "product": "iPhone 15",
                "incomeOrExpense": "支出",
                "amount": "5999.99",
                "paymentMethod": "微信支付",
                "status": "成功",
                "merchantOrderId": "7890",
                "accountId": "acc1",
                "remark": "购买iPhone 15"
            },
            {
                "id": "67890",
                "time": "2025-04-17T11:00:00",
                "type": "工资",
                "counterparty": "Company",
                "product": "Salary",
                "incomeOrExpense": "收入",
                "amount": "10000.00",
                "paymentMethod": "银行转账",
                "status": "已完成",
                "merchantOrderId": "SAL123",
                "accountId": "acc2",
                "remark": "月度工资"
            }
        ]
        """;

    @BeforeAll
    static void setup() {
        transactionService = new TransactionServiceImpl();
        transactionDao = TransactionDao.getInstance();
    }

    @BeforeEach
    @AfterEach
    void cleanStorage() {
        transactionDao.findAll().forEach(tx -> transactionDao.delete(tx));
    }

    @Test
    @Order(1)
    @DisplayName("完整字段导入验证（JSON 文件）")
    void testCompleteFieldImport() throws Exception {
        List<TransactionDto> transactionList = transactionService.importTransactions(testJsonPath);

        assertEquals(2, transactionList.size(), "成功导入记录数不符");
        assertEquals(2, transactionDao.findAll().size(), "实际存储记录数不符");

        transactionList.forEach(tx -> log.info("Imported transaction time: {}",
                tx.getTime().format(FORMATTER)));
    }

    @Test
    @Order(2)
    @DisplayName("测试手动添加交易记录")
    void testAddTransactionManually() throws Exception {
        String userId = TEST_USER;
        String accountId = "acc1";
        TransactionDto transaction = transactionService.addTransactionManually(
                userId, "支出", "5999.99", TEST_TIME, "iPhone 15", "消费", accountId, "测试备注");

        assertNotNull(transaction, "手动添加的交易不应为空");
        assertEquals(userId, transaction.getUserId(), "用户 ID 不匹配");
        assertEquals("支出", transaction.getIncomeOrExpense(), "收支类型不匹配");
        assertEquals("5999.99", transaction.getAmount(), "金额不匹配");
        assertEquals("2025-04-17T10:11:09", transaction.getTime().format(FORMATTER),
                "交易时间不匹配");
    }

    @Test
    @Order(3)
    @DisplayName("测试根据用户 ID 获取交易记录")
    void testGetTransactionsByUserId() throws Exception {
        createTestTransactions();
        List<TransactionDto> transactions = transactionService.getTransactionsByUserId(TEST_USER);

        assertFalse(transactions.isEmpty(), "应找到用户交易记录");
        transactions.forEach(tx -> assertEquals(TEST_USER, tx.getUserId(), "用户 ID 不匹配"));
    }

    @Test
    @Order(4)
    @DisplayName("测试根据账户 ID 获取交易记录")
    void testGetTransactionsByAccountId() throws Exception {
        createTestTransactions();
        String accountId = "acc1";
        List<TransactionDto> transactions = transactionService.getTransactionsByAccountId(accountId, TEST_USER);

        assertFalse(transactions.isEmpty(), "应找到账户交易记录");
        transactions.forEach(tx -> {
            assertEquals(accountId, tx.getAccountId(), "账户 ID 不匹配");
            assertEquals(TEST_USER, tx.getUserId(), "用户 ID 不匹配");
        });
    }

    @Test
    @Order(5)
    @DisplayName("测试时间范围内的收支统计")
    void testIncomeAndExpenseStatistics() {
        createTestTransactions();
        Map<String, String> statistics = transactionService.getIncomeAndExpenseStatistics(
                TEST_TIME.minusDays(1), TEST_TIME.plusDays(1));

        assertNotNull(statistics, "统计结果不应为空");
        assertTrue(Double.parseDouble(statistics.get("totalIncome")) > 0, "收入应大于 0");
        assertTrue(Double.parseDouble(statistics.get("totalExpense")) > 0, "支出应大于 0");
        assertEquals(
                Double.parseDouble(statistics.get("totalIncome")) - Double.parseDouble(statistics.get("totalExpense")),
                Double.parseDouble(statistics.get("netAmount")),
                "净金额计算错误");
    }

    @Test
    @Order(6)
    @DisplayName("测试支付方式统计")
    void testPaymentMethodStatistics() {
        createTestTransactions();
        Map<String, String> statistics = transactionService.getPaymentMethodStatistics();

        assertNotNull(statistics, "统计结果不应为空");
        assertTrue(statistics.containsKey("微信支付"), "应包含微信支付");
        assertTrue(Double.parseDouble(statistics.get("微信支付")) > 0, "微信支付金额应大于 0");
    }

    @Test
    @Order(7)
    @DisplayName("测试交易对手统计")
    void testCounterpartyStatistics() {
        createTestTransactions();
        Map<String, String> statistics = transactionService.getCounterpartyStatistics();

        assertNotNull(statistics, "统计结果不应为空");
        assertTrue(statistics.containsKey("Walmart"), "应包含 Walmart");
        assertTrue(Double.parseDouble(statistics.get("Walmart")) > 0, "Walmart 交易金额应大于 0");
    }

    @Test
    @Order(8)
    @DisplayName("测试多条件搜索")
    void testSearchTransactions() {
        createTestTransactions();
        TransactionSearchCriteria criteria = TransactionSearchCriteria.builder()
                .paymentMethod("微信支付")
                .incomeOrExpense("支出")
                .startTime(TEST_TIME.minusDays(1))
                .endTime(TEST_TIME.plusDays(1))
                .build();

        List<TransactionDto> results = transactionService.searchTransactions(criteria);

        assertFalse(results.isEmpty(), "搜索结果不应为空");
        results.forEach(tx -> {
            assertEquals("微信支付", tx.getPaymentMethod(), "支付方式不匹配");
            assertEquals("支出", tx.getIncomeOrExpense(), "收支类型不匹配");
            assertTrue(tx.getTime().isAfter(TEST_TIME.minusDays(1)), "时间范围不匹配");
            assertTrue(tx.getTime().isBefore(TEST_TIME.plusDays(1)), "时间范围不匹配");
        });
    }

    @Test
    @Order(9)
    @DisplayName("测试更新交易状态")
    void testUpdateTransactionStatus() throws Exception {
        TransactionDto transaction = createTestTransaction();
        String newStatus = "已完成";

        TransactionDto updated = transactionService.updateTransactionStatus(transaction.getId(), newStatus);

        assertNotNull(updated, "更新后的交易不应为空");
        assertEquals(newStatus, updated.getStatus(), "交易状态未更新");
    }

    @Test
    @Order(10)
    @DisplayName("测试删除交易记录")
    void testDeleteTransaction() throws Exception {
        TransactionDto transaction = createTestTransaction();
        transactionService.deleteTransaction(transaction.getId());

        assertTrue(transactionDao.findById(transaction.getId()).isEmpty(), "交易记录未删除");
    }

    @Test
    @Order(11)
    @DisplayName("测试更新不存在的交易记录")
    void testUpdateNonExistentTransaction() {
        String nonExistentId = UUID.randomUUID().toString();
        assertThrows(ServiceException.class,
                () -> transactionService.updateTransactionStatus(nonExistentId, "已完成"),
                "应抛出异常");
    }

    @Test
    @Order(12)
    @DisplayName("测试导出交易记录到 JSON 文件")
    void testExportTransactionsToJson() throws Exception {
        // 准备测试数据
        createTestTransactions();

        // 导出到临时文件
        String exportFilePath = System.getProperty("java.io.tmpdir") + "/exported_transactions.json";
        transactionService.exportTransactionsToJson(exportFilePath);

        // 验证导出的文件
        File exportedFile = new File(exportFilePath);
        assertTrue(exportedFile.exists(), "导出的 JSON 文件不存在");

        // 读取导出的文件并验证内容
        List<TransactionDto> exportedTransactions = JsonUtils.readJson(exportFilePath);
        assertEquals(2, exportedTransactions.size(), "导出的交易记录数不符");

        TransactionDto firstTransaction = exportedTransactions.stream()
                .filter(tx -> "12345".equals(tx.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(firstTransaction, "未找到 ID 为 12345 的交易");
        assertEquals("12345", firstTransaction.getId(), "交易 ID 不匹配");
        assertEquals("2025-04-16T10:30:00", firstTransaction.getTime().format(FORMATTER),
                "交易时间不匹配");
        assertEquals(TEST_USER, firstTransaction.getUserId(), "用户 ID 不匹配");

        // 清理临时文件
        if (exportedFile.exists()) {
            exportedFile.delete();
        }
    }

    @Test
    @Order(13)
    @DisplayName("测试导出空交易记录")
    void testExportEmptyTransactions() {
        String exportFilePath = System.getProperty("java.io.tmpdir") + "/empty_transactions.json";
        assertThrows(ServiceException.class,
                () -> transactionService.exportTransactionsToJson(exportFilePath),
                "应抛出异常：无交易记录可导出");

        File exportedFile = new File(exportFilePath);
        assertFalse(exportedFile.exists(), "空交易不应创建文件");
    }

    @Test
    @Order(12)
    @DisplayName("测试时间范围查询")
    void testGetTransactionsByDateRange() {
        createTestTransactions();
        List<TransactionDto> transactions = transactionService.getTransactionsByDateRange(
                TEST_TIME.minusDays(1), TEST_TIME.plusDays(1));

        assertFalse(transactions.isEmpty(), "时间范围内应有交易记录");
        transactions.forEach(tx -> {
            assertTrue(tx.getTime().isAfter(TEST_TIME.minusDays(1)), "时间范围不匹配");
            assertTrue(tx.getTime().isBefore(TEST_TIME.plusDays(1)), "时间范围不匹配");
        });
    }

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
        transaction.setUserId(TEST_USER);
        transaction.setRemark("测试交易");

        return transactionDao.create(transaction);
    }

    private void createTestTransactions() {
        try {
            transactionService.importTransactions(testJsonPath);
        } catch (ServiceException e) {
            log.error("Failed to import transactions from JSON", e);
            fail("导入 JSON 文件失败");
        }
    }
}