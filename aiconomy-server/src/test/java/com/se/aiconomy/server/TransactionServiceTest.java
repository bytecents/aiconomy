package com.se.aiconomy.server;

import com.se.aiconomy.server.model.entity.Transaction;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionServiceTest {
    private static JSONStorageService jsonStorageService;
    private static TransactionService transactionService;
    private static Path testCsvPath;

    private static final String COMPLETE_CSV = """
        time,type,incomeorexpense,amount,counterparty,product,paymentmethod,status,merchantorderid,remark,custom_field
        2024-05-20T09:30:00,电子产品采购,expense,15000.00,某供应商,办公设备,银行转账,completed,PO_2024052001,月度采购计划,紧急订单
        2024-05-20 10:15:00,客户付款,income,50000.00,某科技公司,软件服务,,pending,INV_2024052001,年度合约款,VIP客户
        """;

    @BeforeAll
    static void setup() throws IOException {
        System.setProperty("jsonStorage.location", Files.createTempDirectory("txn-test-").toString());
        jsonStorageService = JSONStorageServiceImpl.getInstance();
        transactionService = new TransactionService(jsonStorageService);
        testCsvPath = Files.createTempFile("complete-fields-", ".csv");
        Files.write(testCsvPath, COMPLETE_CSV.getBytes());
    }

    @AfterEach
    void cleanStorage() {
        // 确保清理所有现存事务
        jsonStorageService.findAll(Transaction.class)
                .stream()
                .filter(tx -> tx.getId() != null)
                .forEach(tx -> jsonStorageService.delete(tx, Transaction.class));
    }

    @Test
    @DisplayName("完整字段导入验证")
    void testCompleteFieldImport() throws Exception {
        cleanStorage();

        List<Map<String, String>> failures = new ArrayList<>();
        int successCount = transactionService.importTransactions(testCsvPath.toString(), failures);

        assertEquals(2, successCount, "成功导入记录数不符");
        assertEquals(0, failures.size(), "失败记录数应为0");
        assertEquals(2, jsonStorageService.findAll(Transaction.class).size(), "实际存储记录数不符");
    }
}