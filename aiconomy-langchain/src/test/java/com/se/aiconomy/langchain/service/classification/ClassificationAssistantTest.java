package com.se.aiconomy.langchain.service.classification;

import com.se.aiconomy.langchain.common.config.Locale;
import com.se.aiconomy.langchain.common.model.Transaction;
import com.se.aiconomy.langchain.common.model.BillType;
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
            LocalDateTime.now(), "Expense", "Starbucks", "Cappuccino",
            "Expense", "35.5", "Credit Card", "Success",
            "TXN123456", "M123456", "Breakfast"
        );

        transaction.addExtraField("loyaltyPoints", 10);
        transaction.addExtraField("geoLocation", "Shanghai, China");

        BillType billType = service.classifyTransaction(transaction);
        log.info("Bill Type: {}", billType);
        assertNotNull(billType, "Classification result should not be null");
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
        ArrayList<Transaction> transactions = new ArrayList<>(List.of(
            new Transaction(LocalDateTime.now(), "Expense", "KFC", "Burger Combo",
                "Expense", "45.0", "Alipay", "Success", "TXN001", "M001", "Lunch"),
            new Transaction(LocalDateTime.now(), "Expense", "DiDi", "Ride",
                "Expense", "32.5", "WeChat Pay", "Success", "TXN002", "M002", "Ride home"),
            new Transaction(LocalDateTime.now(), "Expense", "JD", "Electronics",
                "Expense", "999.9", "Credit Card", "Success", "TXN003", "M003", "Buy headphones"),
            new Transaction(LocalDateTime.now(), "Salary", "XX Company", "Monthly Salary",
                "Income", "15000.0", "Bank Transfer", "Success", "TXN004", "M004", "March Salary")
        ));

        for (Transaction transaction : transactions) {
            transaction.addExtraField("processTime", LocalDateTime.now().toString());
            transaction.addExtraField("deviceId", "TEST-DEVICE-001");
        }

        return transactions;
    }

    private void validateClassificationResults(ArrayList<Transaction> transactions, ArrayList<BillType> billTypes) {
        assertEquals(transactions.size(), billTypes.size(),
            "The number of classification results should match the number of transactions");

        for (int i = 0; i < billTypes.size(); i++) {
            BillType billType = billTypes.get(i);
            Transaction transaction = transactions.get(i);

            assertNotNull(billType,
                String.format("The classification result for transaction %s should not be null", transaction.getTransactionId()));

            log.info("Transaction: {} -> Bill Type: {}",
                transaction.getTransactionId(), billType);
        }
    }
}