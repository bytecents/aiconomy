package com.se.aiconomy.langchain.service.classification;

import com.se.aiconomy.langchain.common.config.Locale;
import com.se.aiconomy.langchain.common.model.Transaction;
import com.se.aiconomy.langchain.common.model.BillType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClassificationAssistantTest {

    private static final Logger log = LoggerFactory.getLogger(ClassificationAssistantTest.class);

    @Test
    void testClassification() {
        TransactionClassificationService service = new TransactionClassificationService();

        Transaction transaction = new Transaction(
            LocalDateTime.now(), "消费", "Starbucks", "Cappuccino",
            "支出", "35.5", "信用卡", "成功",
            "TXN123456", "M123456", "早餐"
        );

        transaction.addExtraField("loyaltyPoints", 10);
        transaction.addExtraField("geoLocation", "Shanghai, China");

        BillType billType = service.classifyTransaction(transaction);
        log.info("Bill Type: {}", billType);
    }

    @Test
    void testParallelClassification() {
        TransactionClassificationService service = new TransactionClassificationService();
        ArrayList<Transaction> transactions = createTestTransactions();

        ArrayList<BillType> billTypes = service.classifyTransactions(transactions);
        validateClassificationResults(transactions, billTypes);

        ArrayList<BillType> billTypesCN = service.classifyTransactions(transactions, Locale.CN);
        validateClassificationResults(transactions, billTypesCN);
    }

    private ArrayList<Transaction> createTestTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();

        // 餐饮交易
        transactions.add(new Transaction(
            LocalDateTime.now(), "消费", "KFC", "汉堡套餐",
            "支出", "45.0", "支付宝", "成功",
            "TXN001", "M001", "午餐"
        ));

        // 交通交易
        transactions.add(new Transaction(
            LocalDateTime.now(), "消费", "滴滴出行", "快车",
            "支出", "32.5", "微信支付", "成功",
            "TXN002", "M002", "打车回家"
        ));

        // 购物交易
        transactions.add(new Transaction(
            LocalDateTime.now(), "消费", "京东", "电子产品",
            "支出", "999.9", "信用卡", "成功",
            "TXN003", "M003", "购买耳机"
        ));

        // 工资收入
        transactions.add(new Transaction(
            LocalDateTime.now(), "工资", "XX公司", "月薪",
            "收入", "15000.0", "银行转账", "成功",
            "TXN004", "M004", "3月工资"
        ));

        for (Transaction transaction : transactions) {
            transaction.addExtraField("processTime", LocalDateTime.now().toString());
            transaction.addExtraField("deviceId", "TEST-DEVICE-001");
        }

        return transactions;
    }

    private void validateClassificationResults(ArrayList<Transaction> transactions, ArrayList<BillType> billTypes) {
        assertEquals(transactions.size(), billTypes.size(),
            "分类结果数量应该与交易数量相同");

        for (int i = 0; i < billTypes.size(); i++) {
            BillType billType = billTypes.get(i);
            Transaction transaction = transactions.get(i);

            assertNotNull(billType,
                String.format("交易 %s 的分类结果不应为空", transaction.getTransactionId()));

            log.info("Transaction: {} -> Bill Type: {}",
                transaction.getTransactionId(), billType);
        }
    }
}