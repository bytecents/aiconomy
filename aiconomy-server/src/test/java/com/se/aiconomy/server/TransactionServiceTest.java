package com.se.aiconomy.server;

import com.se.aiconomy.server.common.utils.CSVUtil;
import com.se.aiconomy.server.model.entity.Transaction;
import com.se.aiconomy.server.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionServiceTest {
    private TransactionDaoTest testDao;
    private TransactionService service;

    @BeforeEach
    void setUp() {
        testDao = new TransactionDaoTest();
        service = new TransactionService(testDao);
    }

    @Test
    void testNormalImport() throws Exception {
        // 1. 准备测试数据（确保字段名全小写）
        List<Map<String, String>> csvData = new ArrayList<>();

        Map<String, String> validRow = new LinkedHashMap<>();
        validRow.put("time", "2024-01-01T12:00:00");
        validRow.put("type", "sale");
        validRow.put("incomeorexpense", "income"); // 必须全小写以匹配FIELD_MAPPING
        validRow.put("amount", "1000.00");
        validRow.put("status", "completed");
        validRow.put("paymentmethod", "wechat");   // 补充其他必要字段
        validRow.put("merchantorderid", "ORDER_001");
        csvData.add(validRow);

        // 2. 生成临时CSV文件
        Path csvPath = Files.createTempFile("test-transactions-", ".csv");
        CSVUtil.writeTestCSV(csvPath.toString(), csvData);

        // 3. 执行导入
        List<Map<String, String>> failures = new ArrayList<>();
        int successCount = service.importTransactions(csvPath.toString(), failures);

        // 4. 验证导入结果
        assertEquals(1, successCount, "成功导入数量不符");
        assertTrue(failures.isEmpty(), "存在未预期的失败记录: " + failures);

        // 5. 验证DAO层数据完整性
        List<Transaction> savedTransactions = testDao.getSavedBatches().get(0);
        assertEquals(1, savedTransactions.size(), "保存的记录数量不符");

        Transaction savedTx = savedTransactions.get(0);

        // 详细字段验证
        assertAll("字段验证",
                () -> assertEquals(
                        LocalDateTime.parse("2024-01-01T12:00:00"),
                        savedTx.getTime(),
                        "时间字段不匹配"
                ),
                () -> assertEquals("sale", savedTx.getType(), "交易类型不匹配"),
                () -> assertEquals("income", savedTx.getIncomeOrExpense(), "收支类型不匹配"),
                () -> assertEquals("1000.00", savedTx.getAmount(), "金额不匹配"), // 关键修复：直接比较字符串
                () -> assertEquals("completed", savedTx.getStatus(), "状态不匹配"),
                () -> assertEquals("wechat", savedTx.getPaymentMethod(), "支付方式不匹配"),
                () -> assertEquals("ORDER_001", savedTx.getMerchantOrderId(), "订单ID不匹配")
        );
    }
}
