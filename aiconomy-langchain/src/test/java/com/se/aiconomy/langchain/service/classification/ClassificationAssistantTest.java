package com.se.aiconomy.langchain.service.classification;

import com.se.aiconomy.langchain.common.model.Transaction;
import com.se.aiconomy.langchain.common.model.BillType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

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
}
