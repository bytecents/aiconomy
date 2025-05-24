package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionClassificationRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionImportRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for transaction classification and import functionalities using Langchain.
 * <p>
 * This class tests the ability to classify transactions from CSV and Excel files,
 * and to import classified transactions into the system.
 * </p>
 */
public class LangchainTest {

    /**
     * The file path for the CSV file containing transaction data.
     */
    public static final String csvFilePath = Objects.requireNonNull(org.apache.poi.ss.formula.functions.T.class.getClassLoader().getResource("transactions.csv")).getPath();

    /**
     * The file path for the Excel file containing transaction data.
     */
    public static final String excelFilePath = Objects.requireNonNull(org.apache.poi.ss.formula.functions.T.class.getClassLoader().getResource("transactions.xlsx")).getPath();

    /**
     * The list of classified transactions, each represented as a map from Transaction to DynamicBillType.
     */
    private static List<Map<Transaction, DynamicBillType>> classifiedTransactions;

    /**
     * The TransactionDao instance used for accessing transaction data.
     */
    private static TransactionDao transactionDao;

    /**
     * Initializes the TransactionDao instance before all tests.
     */
    @BeforeAll
    static void setup() {
        transactionDao = TransactionDao.getInstance();
    }

    /**
     * Cleans up all transactions in the database after each test.
     */
    @AfterEach
    void cleanup() {
        transactionDao.findAll().forEach(tx ->
                transactionDao.delete(tx));
    }

    /**
     * Tests the classification of transactions from a CSV file.
     *
     * @throws ServiceException if the classification process fails
     */
    @Test
    void handleTransactionClassificationRequestCSVTest() throws ServiceException {
        TransactionRequestHandler transactionRequestHandler = new TransactionRequestHandler();
        TransactionClassificationRequest transactionClassificationRequest = new TransactionClassificationRequest(csvFilePath);
        classifiedTransactions = transactionRequestHandler.handleTransactionClassificationRequest(transactionClassificationRequest);
        assertEquals(4, classifiedTransactions.size(), "The number of classified transactions is not matching");
    }

    /**
     * Tests the classification of transactions from an Excel file.
     *
     * @throws ServiceException if the classification process fails
     */
    @Test
    void handleTransactionClassificationRequestExcelTest() throws ServiceException {
        TransactionRequestHandler transactionRequestHandler = new TransactionRequestHandler();
        TransactionClassificationRequest transactionClassificationRequest = new TransactionClassificationRequest(excelFilePath);
        classifiedTransactions = transactionRequestHandler.handleTransactionClassificationRequest(transactionClassificationRequest);
        assertEquals(4, classifiedTransactions.size(), "The number of classified transactions is not matching");
    }

    /**
     * Tests the import of classified transactions into the system.
     *
     * @throws ServiceException if the import process fails
     */
    @Test
    void handleTransactionImportRequestTest() throws ServiceException {
        TransactionRequestHandler transactionRequestHandler = new TransactionRequestHandler();
        TransactionImportRequest request = new TransactionImportRequest("1", classifiedTransactions);
        List<Map<Transaction, DynamicBillType>> map = transactionRequestHandler.handleTransactionImportRequest(request);
        assertEquals(4, map.size(), "The number of imported transactions is not matching");
    }
}
