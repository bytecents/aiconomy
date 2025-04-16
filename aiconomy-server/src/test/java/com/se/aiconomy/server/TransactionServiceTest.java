package com.se.aiconomy.server;

import com.se.aiconomy.server.common.utils.CSVUtil;
import com.se.aiconomy.server.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionServiceTest {
    private TransactionDaoTest testDao;
    private TransactionService service;

    @BeforeEach
    void setUp() {
        testDao = new TransactionDaoTest();
        service = new TransactionService(testDao); // 注入模拟的DAO
    }

//    @Test
//    void testBatchSave() throws Exception {
//        // 准备测试数据
//        List<Map<String, String>> csvData = List.of(
//                Map.of("交易时间", "2024-01-01T12:00:00", "金额", "100.00"),
//                Map.of("交易时间", "2024-01-02T14:30:00", "金额", "200.50")
//        );
//        CSVUtil.writeTestCSV("test.csv", csvData);
//
//        // 执行导入
//        List<Map<String, String>> failures = new ArrayList<>();
//        int successCount = service.importTransactions("test.csv", failures);
//
//        // 验证结果
//        assertEquals(2, successCount);
//        assertEquals(0, failures.size());
//        // 验证DAO调用
//        assertEquals(1, testDao.getSaveCallCount());
//        assertEquals(2, testDao.getSavedBatches().get(0).size());
//    }
}
