package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
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

/**
 * Unit tests for transaction import, export, and statistics using Excel files.
 * <p>
 * This class tests the import and export of transaction data to and from Excel files,
 * as well as various statistics and CRUD operations on transactions.
 * </p>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionServiceExcelTest {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceExcelTest.class);
    private static final LocalDateTime TEST_TIME = LocalDateTime.parse("2025-04-18T10:11:09");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final String TEST_USER = "Aurelia";
    private static TransactionServiceImpl transactionService;
    private static TransactionDao transactionDao;
    private static String testExcelPath;

    /**
     * Creates a test Excel file with sample transaction data.
     *
     * @throws IOException if file creation fails
     */
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
        log.info("Excel file created successfully!");
    }

    /**
     * Initializes the test environment before all tests.
     *
     * @throws IOException if Excel file creation fails
     */
    @BeforeAll
    static void setup() throws IOException {
        transactionService = new TransactionServiceImpl();
        transactionDao = TransactionDao.getInstance();
        createTestExcelFile();
        testExcelPath = Objects.requireNonNull(TransactionServiceExcelTest.class.getClassLoader().getResource("transactions.xlsx")).getPath();
    }

    /**
     * Cleans up the transaction storage before and after each test.
     */
    @BeforeEach
    @AfterEach
    void cleanStorage() {
        transactionDao.findAll().forEach(tx -> transactionDao.delete(tx));
    }

    /**
     * Tests importing transactions from an Excel file with all fields.
     *
     * @throws Exception if import fails
     */
    @Test
    @Order(1)
    @DisplayName("Complete field import validation - Excel")
    void testCompleteFieldImportFromExcel() throws Exception {
        List<TransactionDto> transactionList = transactionService.importTransactions(testExcelPath);

        assertEquals(4, transactionList.size(), "Number of successfully imported records does not match");
        assertEquals(4, transactionDao.findAll().size(), "Number of records in storage does not match");
    }

    /**
     * Tests income and expense statistics within a time range using Excel data.
     */
    @Test
    @Order(2)
    @DisplayName("Test income and expense statistics within time range - Excel")
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

    /**
     * Tests payment method statistics using Excel data.
     */
    @Test
    @Order(3)
    @DisplayName("Test payment method statistics - Excel")
    void testPaymentMethodStatisticsFromExcel() {
        createTestTransactions();
        Map<String, String> statistics = transactionService.getPaymentMethodStatistics();

        assertNotNull(statistics);
        assertTrue(statistics.containsKey("支付宝"));
        assertTrue(Double.parseDouble(statistics.get("支付宝")) > 0);
    }

    /**
     * Tests counterparty statistics using Excel data.
     */
    @Test
    @Order(4)
    @DisplayName("Test counterparty statistics - Excel")
    void testCounterpartyStatisticsFromExcel() {
        createTestTransactions();
        Map<String, String> statistics = transactionService.getCounterpartyStatistics();

        assertNotNull(statistics);
        assertTrue(statistics.containsKey("京东商城"));
        assertTrue(Double.parseDouble(statistics.get("京东商城")) > 0);
    }

    /**
     * Tests updating the status of a transaction using Excel data.
     *
     * @throws Exception if update fails
     */
    @Test
    @Order(5)
    @DisplayName("Test transaction status update - Excel")
    void testUpdateTransactionStatusFromExcel() throws Exception {
        TransactionDto transaction = createTestTransaction();
        String newStatus = "已完成";

        TransactionDto updated = transactionService.updateTransactionStatus(transaction.getId(), newStatus);

        assertNotNull(updated);
        assertEquals(newStatus, updated.getStatus());
    }

    /**
     * Tests searching for transactions with multiple criteria using Excel data.
     */
    @Test
    @Order(6)
    @DisplayName("Test multi-criteria search - Excel")
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

    /**
     * Tests deleting a transaction using Excel data.
     *
     * @throws Exception if deletion fails
     */
    @Test
    @Order(7)
    @DisplayName("Test delete transaction - Excel")
    void testDeleteTransactionFromExcel() throws Exception {
        TransactionDto transaction = createTestTransaction();
        transactionService.deleteTransaction(transaction.getId());

        assertTrue(transactionDao.findById(transaction.getId()).isEmpty());
    }

    /**
     * Tests updating a non-existent transaction using Excel data.
     */
    @Test
    @Order(8)
    @DisplayName("Test update non-existent transaction - Excel")
    void testUpdateNonExistentTransactionFromExcel() {
        String nonExistentId = UUID.randomUUID().toString();
        assertThrows(Exception.class, () ->
                transactionService.updateTransactionStatus(nonExistentId, "已完成"));
    }

    /**
     * Tests exporting transactions to an Excel file.
     *
     * @throws Exception if export or import fails
     */
    @Test
    @Order(9)
    @DisplayName("Test export transactions to Excel file")
    void testExportTransactionsToExcel() throws Exception {
        createTestTransactions();
        String exportFilePath = System.getProperty("java.io.tmpdir") + "/exported_transactions.xlsx";
        transactionService.exportTransactionsToExcel(exportFilePath);

        File exportedFile = new File(exportFilePath);
        assertTrue(exportedFile.exists(), "Exported Excel file does not exist");

        // Debug: Read and print Excel file content
        try (Workbook workbook = new XSSFWorkbook(exportedFile)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                StringBuilder rowData = new StringBuilder();
                for (Cell cell : row) {
                    rowData.append(cell.toString()).append(",");
                }
                log.info("Excel Row: {}", rowData);
            }
        }

        List<TransactionDto> exportedTransactions = transactionService.importTransactions(exportFilePath);
        assertEquals(2, exportedTransactions.size(), "Number of exported transactions does not match");

        // Debug: Print imported transactions
        exportedTransactions.forEach(tx -> log.info("Imported Transaction: id={}, userId={}, remark={}",
                tx.getId(), tx.getUserId(), tx.getRemark()));

        TransactionDto firstTransaction = exportedTransactions.stream()
                .filter(tx -> "消费".equals(tx.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(firstTransaction, "Consumption transaction not found");
        assertEquals("京东商城", firstTransaction.getCounterparty(), "Counterparty does not match");
        assertEquals("2025-04-18T10:11:09", firstTransaction.getTime().format(FORMATTER), "Transaction time does not match");
        assertEquals(TEST_USER, firstTransaction.getUserId(), "User ID does not match");

        if (exportedFile.exists()) {
            exportedFile.delete();
        }
    }

    /**
     * Tests exporting empty transactions to an Excel file.
     * Asserts that an exception is thrown and no file is created when there are no transactions to export.
     */
    @Test
    @Order(10)
    @DisplayName("Test export empty transactions to Excel")
    void testExportEmptyTransactionsToExcel() {
        String exportFilePath = System.getProperty("java.io.tmpdir") + "/empty_transactions.xlsx";
        assertThrows(ServiceException.class,
                () -> transactionService.exportTransactionsToExcel(exportFilePath),
                "Exception should be thrown: no transactions to export");

        File exportedFile = new File(exportFilePath);
        assertFalse(exportedFile.exists(), "No file should be created for empty transactions");
    }

    /**
     * Helper method: Creates a test transaction and stores it.
     *
     * @return the created TransactionDto
     */
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

    /**
     * Helper method: Creates multiple test transactions (one expense and one income).
     */
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
