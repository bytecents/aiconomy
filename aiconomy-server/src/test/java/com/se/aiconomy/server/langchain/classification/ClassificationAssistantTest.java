package com.se.aiconomy.server.langchain.classification;

import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.langchain.service.classification.TransactionClassificationService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClassificationAssistantTest {

    private static final Logger log = LoggerFactory.getLogger(ClassificationAssistantTest.class);

    @Test
    void testClassification() {
        TransactionClassificationService service = new TransactionClassificationService();

        Transaction transaction = new Transaction(
            "1", LocalDateTime.now(), "Expense", "Starbucks", "Cappuccino",
            "Expense", "35.5", "CNY", "Credit Card", "Success",
            "TXN123456", "userAcc001", "Coffee"
        );

        BillType billType = service.classifyTransaction(transaction);
        log.info("Bill Type: {}", billType);
        assertNotNull(billType, "Classification result should not be null");
    }

    @Test
    void testParallelClassification() {
        TransactionClassificationService service = new TransactionClassificationService();
        List<Transaction> transactions = createTestTransactions();

        List<BillType> billTypes = service.classifyTransactions(transactions);
        validateClassificationResults(transactions, billTypes);

        List<BillType> billTypesCN = service.classifyTransactions(transactions, Locale.CN);
        validateClassificationResults(transactions, billTypesCN);
    }

    private ArrayList<Transaction> createTestTransactions() {

        return new ArrayList<>(List.of(
            new Transaction("1", LocalDateTime.now(), "Expense", "KFC", "Burger Combo",
                "Expense", "45.0", "CNY", "Alipay", "Success", "TXN001", "userAcc002","Lunch"),
            new Transaction("2", LocalDateTime.now(), "Expense", "DiDi", "Ride",
                "Expense", "32.5", "CNY", "WeChat Pay", "Success", "TXN002", "userAcc002","Ride home"),
            new Transaction("3", LocalDateTime.now(), "Expense", "JD", "Electronics",
                "Expense", "999.9", "CNY", "Credit Card", "Success", "TXN003", "userAcc002","Buy headphones"),
            new Transaction("4", LocalDateTime.now(), "Salary", "XX Company", "Monthly Salary",
                "Income", "15000.0", "CNY", "Bank Transfer", "Success", "TXN004", "userAcc002","March Salary")
        ));
    }

    private void validateClassificationResults(List<Transaction> transactions, List<BillType> billTypes) {
        assertEquals(transactions.size(), billTypes.size(),
            "The number of classification results should match the number of transactions");

        for (int i = 0; i < billTypes.size(); i++) {
            BillType billType = billTypes.get(i);
            Transaction transaction = transactions.get(i);

            assertNotNull(billType,
                String.format("The classification result for transaction %s should not be null", transaction.getId()));

            log.info("Transaction: {} -> Bill Type: {}",
                transaction.getId(), billType);
        }
    }
}