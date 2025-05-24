package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.common.utils.ExcelUtils;
import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl.TransactionSearchCriteria;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionServiceExcelTest {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceExcelTest.class);
    private static final LocalDateTime TEST_TIME = LocalDateTime.parse("2025-04-18T10:11:09");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final String TEST_USER = "Aurelia";
    private static TransactionServiceImpl transactionService;
    private static TransactionDao transactionDao;
    private static String testExcelPath;

    private static void createTestExcelFile() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        String[] headers = {
                "id", "time", "type", "counterparty", "product", "incomeOrExpense", "amount",
                "paymentMethod", "status", "merchantOrderId", "accountId", "userId", "remark"
        };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"12345", "2025-04-16T10:30:00", "消费", "Walmart", "iPhone 15", "支出", "5999.99", "微信支付", "成功", "7890", "acc1", "AureliaSKY", "购买iPhone 15"});
        data.add(new String[]{"12346", "2025-04-17T12:45:00", "转账", "支付宝", "MacBook Pro", "收入", "12999.00", "信用卡", "成功", "7891", "acc2", "AureliaSKY", "收到MacBook款项"});
        data.add(new String[]{"12347", "2025-04-18T09:00:00", "消费", "京东", "耳机", "支出", "299.99", "支付宝", "待支付", "7892", "acc1", "AureliaSKY", "购买耳机"});
        data.add(new String[]{"12348", "2025-04-19T15:00:00", "转账", "支付宝", "电视", "收入", "4999.00", "现金", "成功", "7893", "acc2", "AureliaSKY", "收到电视款项"});

        int rowNum = 1;
        for (String[] transaction : data) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < transaction.length; i++) {
                row.createCell(i).setCellValue(transaction[i]);
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream("src/test/resources/transactions.xlsx")) {
            workbook.write(fileOut);
        }

        workbook.close();
        log.info("Excel 文件创建成功!");
    }

    @BeforeAll
    static void setup() throws IOException {
        transactionService = new TransactionServiceImpl();
        transactionDao = TransactionDao.getInstance();
        createTestExcelFile();
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
        createTestTransactions();
        Map<String, String> statistics = transactionService.getIncomeAndExpenseStatistics(
                TEST_TIME.minusDays(1), TEST_TIME.plusDays(1));

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
        createTestTransactions();
        Map<String, String> statistics = transactionService.getPaymentMethodStatistics();

        assertNotNull(statistics);
        assertTrue(statistics.containsKey("支付宝"));
        assertTrue(Double.parseDouble(statistics.get("支付宝")) > 0);
    }

    @Test
    @Order(4)
    @DisplayName("测试交易对象统计 - Excel")
    void testCounterpartyStatisticsFromExcel() {
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
    @DisplayName("测试删除交易记录 - Excel")
    void testDeleteTransactionFromExcel() throws Exception {
        TransactionDto transaction = createTestTransaction();
        transactionService.deleteTransaction(transaction.getId());

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

    @Test
    @Order(9)
    @DisplayName("测试导出交易记录到 Excel 文件")
    void testExportTransactionsToExcel() throws Exception {
        createTestTransactions();
        String exportFilePath = System.getProperty("java.io.tmpdir") + "/exported_transactions.xlsx";
        transactionService.exportTransactionsToExcel(exportFilePath);

        File exportedFile = new File(exportFilePath);
        assertTrue(exportedFile.exists(), "导出的 Excel 文件不存在");

        // 调试：读取并打印 Excel 文件内容
        try (Workbook workbook = new XSSFWorkbook(exportedFile)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                StringBuilder rowData = new StringBuilder();
                for (Cell cell : row) {
                    rowData.append(cell.toString()).append(",");
                }
                log.info("Excel Row: {}", rowData.toString());
            }
        }

        List<TransactionDto> exportedTransactions = transactionService.importTransactions(exportFilePath);
        assertEquals(2, exportedTransactions.size(), "导出的交易记录数不符");

        // 调试：打印导入的交易记录
        exportedTransactions.forEach(tx -> log.info("Imported Transaction: id={}, userId={}, remark={}",
                tx.getId(), tx.getUserId(), tx.getRemark()));

        TransactionDto firstTransaction = exportedTransactions.stream()
                .filter(tx -> "消费".equals(tx.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(firstTransaction, "未找到消费交易");
        assertEquals("京东商城", firstTransaction.getCounterparty(), "交易对手不匹配");
        assertEquals("2025-04-18T10:11:09", firstTransaction.getTime().format(FORMATTER), "交易时间不匹配");
        assertEquals(TEST_USER, firstTransaction.getUserId(), "用户 ID 不匹配");

        if (exportedFile.exists()) {
            exportedFile.delete();
        }
    }

    @Test
    @Order(10)
    @DisplayName("测试导出空交易记录到 Excel")
    void testExportEmptyTransactionsToExcel() {
        String exportFilePath = System.getProperty("java.io.tmpdir") + "/empty_transactions.xlsx";
        assertThrows(ServiceException.class,
                () -> transactionService.exportTransactionsToExcel(exportFilePath),
                "应抛出异常：无交易记录可导出");

        File exportedFile = new File(exportFilePath);
        assertFalse(exportedFile.exists(), "空交易不应创建文件");
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
        transaction.setAccountId("acc1");
        transaction.setUserId(TEST_USER);
        transaction.setRemark("测试交易");

        return transactionDao.create(transaction);
    }

    private void createTestTransactions() {
        createTestTransaction();

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
        incomeTransaction.setAccountId("acc2");
        incomeTransaction.setUserId(TEST_USER);
        incomeTransaction.setRemark("月度工资");

        transactionDao.create(incomeTransaction);
    }
}