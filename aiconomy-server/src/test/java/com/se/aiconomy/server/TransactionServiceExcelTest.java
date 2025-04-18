package com.se.aiconomy.server;

import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.service.TransactionService.TransactionSearchCriteria;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionServiceExcelTest {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceExcelTest.class);
    private static TransactionService transactionService;
    private static TransactionDao transactionDao;
    private static String testExcelPath;

    // 测试用时间
    private static final LocalDateTime TEST_TIME = LocalDateTime.parse("2025-04-18T10:11:09");

    /**
     * 创建 Excel 文件用于测试
     */
    private static void createTestExcelFile() throws IOException {
        // 创建工作簿和表格
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        // 创建表头
        String[] headers = {
                "id", "time", "type", "counterparty", "product", "incomeOrExpense", "amount",
                "paymentMethod", "status", "merchantOrderId", "remark"
        };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 创建交易数据
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"12345", "2025-04-16T10:30:00", "消费", "Walmart", "iPhone 15", "支出", "5999.99", "微信支付", "成功", "7890", "购买iPhone 15"});
        data.add(new String[]{"12346", "2025-04-17T12:45:00", "转账", "支付宝", "MacBook Pro", "收入", "12999.00", "信用卡", "成功", "7891", "收到MacBook款项"});
        data.add(new String[]{"12347", "2025-04-18T09:00:00", "消费", "京东", "耳机", "支出", "299.99", "支付宝", "待支付", "7892", "购买耳机"});
        data.add(new String[]{"12348", "2025-04-19T15:00:00", "转账", "支付宝", "电视", "收入", "4999.00", "现金", "成功", "7893", "收到电视款项"});

        // 将数据写入 Excel
        int rowNum = 1;
        for (String[] transaction : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < transaction.length; i++) {
                row.createCell(i).setCellValue(transaction[i]);
            }
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 写入文件
        try (FileOutputStream fileOut = new FileOutputStream("src/test/resources/transactions.xlsx")) {
            workbook.write(fileOut);
        }

        // 关闭工作簿
        workbook.close();
        log.info("Excel 文件创建成功!");
    }

    @BeforeAll
    static void setup() throws IOException {
        transactionService = new TransactionService();
        transactionDao = TransactionDao.getInstance();

        // 创建 Excel 文件
        createTestExcelFile();

        // 获取 Excel 文件路径
        testExcelPath = Objects.requireNonNull(TransactionServiceExcelTest.class.getClassLoader().getResource("transactions.xlsx")).getPath();
    }

    @BeforeEach
    @AfterEach
    void cleanStorage() {
        transactionDao.findAll().forEach(tx -> transactionDao.delete(tx));
    }

    @Test
    @Order(1)
    @DisplayName("完整字段导入验证 - Excel")
    void testCompleteFieldImportFromExcel() throws Exception {
        List<TransactionDto> transactionList = transactionService.importTransactions(testExcelPath);

        assertEquals(4, transactionList.size(), "成功导入记录数不符");
        assertEquals(4, transactionDao.findAll().size(), "实际存储记录数不符");
    }

    @Test
    @Order(2)
    @DisplayName("测试时间范围内的收支统计 - Excel")
    void testIncomeAndExpenseStatisticsFromExcel() {
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
    @DisplayName("测试支付方式统计 - Excel")
    void testPaymentMethodStatisticsFromExcel() {
        // 准备测试数据
        createTestTransactions();

        Map<String, String> statistics = transactionService.getPaymentMethodStatistics();

        assertNotNull(statistics);
        assertTrue(statistics.containsKey("支付宝"));
        assertTrue(Double.parseDouble(statistics.get("支付宝")) > 0);
    }

    @Test
    @Order(4)
    @DisplayName("测试交易对手统计 - Excel")
    void testCounterpartyStatisticsFromExcel() {
        // 准备测试数据
        createTestTransactions();

        Map<String, String> statistics = transactionService.getCounterpartyStatistics();

        assertNotNull(statistics);
        assertTrue(statistics.containsKey("京东商城"));
        assertTrue(Double.parseDouble(statistics.get("京东商城")) > 0);
    }

    @Test
    @Order(5)
    @DisplayName("测试交易状态更新 - Excel")
    void testUpdateTransactionStatusFromExcel() throws Exception {
        // 创建测试交易
        TransactionDto transaction = createTestTransaction();
        String newStatus = "已完成";

        TransactionDto updated = transactionService.updateTransactionStatus(transaction.getId(), newStatus);

        assertNotNull(updated);
        assertEquals(newStatus, updated.getStatus());
    }

    @Test
    @Order(6)
    @DisplayName("测试多条件搜索 - Excel")
    void testSearchTransactionsFromExcel() {
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
    @DisplayName("测试删除交易记录 - Excel")
    void testDeleteTransactionFromExcel() throws Exception {
        // 创建测试交易
        TransactionDto transaction = createTestTransaction();

        // 删除交易
        transactionService.deleteTransaction(transaction.getId());

        // 验证删除结果
        assertTrue(transactionDao.findById(transaction.getId()).isEmpty());
    }

    @Test
    @Order(8)
    @DisplayName("测试更新不存在的交易记录 - Excel")
    void testUpdateNonExistentTransactionFromExcel() {
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
