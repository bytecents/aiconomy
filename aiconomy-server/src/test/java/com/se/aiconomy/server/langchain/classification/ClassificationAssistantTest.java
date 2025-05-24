package com.se.aiconomy.server.langchain.classification;

import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
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

/**
 * Unit tests for {@link TransactionClassificationService}.
 * <p>
 * This class tests the transaction classification functionality, including single and batch classification,
 * as well as classification with different locales.
 * </p>
 */
public class ClassificationAssistantTest {

    /**
     * Logger instance for this test class.
     */
    private static final Logger log = LoggerFactory.getLogger(ClassificationAssistantTest.class);

    /**
     * Tests the classification of a single transaction.
     * <p>
     * Creates a sample transaction and asserts that the classification result is not null.
     * </p>
     */
    @Test
    void testClassification() {
        TransactionClassificationService service = new TransactionClassificationService();

        Transaction transaction = new Transaction(
                "1", LocalDateTime.now(), "Expense", "Starbucks", "Cappuccino",
                "Expense", "35.5", "CNY", "Credit Card", "Success",
                "TXN123456", "userAcc001", "Coffee"
        );

        DynamicBillType billType = service.classifyTransaction(transaction);
        log.info("Bill Type: {}", billType);
        assertNotNull(billType, "Classification result should not be null");
    }

    /**
     * Tests the classification of multiple transactions in parallel and with different locales.
     * <p>
     * Asserts that the number of classification results matches the number of transactions,
     * and that each result is not null.
     * </p>
     */
    @Test
    void testParallelClassification() {
        TransactionClassificationService service = new TransactionClassificationService();
        List<Transaction> transactions = createTestTransactions();

        List<DynamicBillType> billTypes = service.classifyTransactions(transactions);
        validateClassificationResults(transactions, billTypes);

        List<DynamicBillType> billTypesCN = service.classifyTransactions(transactions, Locale.CN);
        validateClassificationResults(transactions, billTypesCN);
    }

    /**
     * Creates a list of sample transactions for testing.
     *
     * @return a list of sample {@link Transaction} objects
     */
    private ArrayList<Transaction> createTestTransactions() {

        return new ArrayList<>(List.of(
                new Transaction("1", LocalDateTime.now(), "Expense", "KFC", "Burger Combo",
                        "Expense", "45.0", "CNY", "Alipay", "Success", "TXN001", "userAcc002", "Lunch"),
                new Transaction("2", LocalDateTime.now(), "Expense", "DiDi", "Ride",
                        "Expense", "32.5", "CNY", "WeChat Pay", "Success", "TXN002", "userAcc002", "Ride home"),
                new Transaction("3", LocalDateTime.now(), "Expense", "JD", "Electronics",
                        "Expense", "999.9", "CNY", "Credit Card", "Success", "TXN003", "userAcc002", "Buy headphones"),
                new Transaction("4", LocalDateTime.now(), "Salary", "XX Company", "Monthly Salary",
                        "Income", "15000.0", "CNY", "Bank Transfer", "Success", "TXN004", "userAcc002", "March Salary")
        ));
    }

    /**
     * Validates the classification results for a list of transactions.
     * <p>
     * Asserts that the number of results matches the number of transactions,
     * and that each classification result is not null.
     * </p>
     *
     * @param transactions the list of transactions
     * @param billTypes    the list of classification results
     */
    private void validateClassificationResults(List<Transaction> transactions, List<DynamicBillType> billTypes) {
        assertEquals(transactions.size(), billTypes.size(),
                "The number of classification results should match the number of transactions");

        for (int i = 0; i < billTypes.size(); i++) {
            DynamicBillType billType = billTypes.get(i);
            Transaction transaction = transactions.get(i);

            assertNotNull(billType,
                    String.format("The classification result for transaction %s should not be null", transaction.getId()));

            log.info("Transaction: {} -> Bill Type: {}",
                    transaction.getId(), billType);
        }
    }
}
