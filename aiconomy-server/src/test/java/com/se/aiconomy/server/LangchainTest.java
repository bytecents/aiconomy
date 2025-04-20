package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.langchain.common.model.BillType;
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

public class LangchainTest {
    public static final String csvFilePath = Objects.requireNonNull(org.apache.poi.ss.formula.functions.T.class.getClassLoader().getResource("transactions.csv")).getPath();
    public static final String excelFilePath = Objects.requireNonNull(org.apache.poi.ss.formula.functions.T.class.getClassLoader().getResource("transactions.xlsx")).getPath();
    private static List<Map<Transaction, DynamicBillType>> classifiedTransactions;
    private static TransactionDao transactionDao;

    @BeforeAll
    static void setup() {
        transactionDao = TransactionDao.getInstance();
    }

    @AfterEach
    void cleanup() {
        transactionDao.findAll().forEach(tx ->
            transactionDao.delete(tx));
    }

    @Test
    void handleTransactionClassificationRequestCSVTest() throws ServiceException {
        TransactionRequestHandler transactionRequestHandler = new TransactionRequestHandler();
        TransactionClassificationRequest transactionClassificationRequest = new TransactionClassificationRequest(csvFilePath);
        classifiedTransactions = transactionRequestHandler.handleTransactionClassificationRequest(transactionClassificationRequest);
        assertEquals(4, classifiedTransactions.size(), "The number of classified transactions is not matching");
    }

    @Test
    void handleTransactionClassificationRequestExcelTest() throws ServiceException {
        TransactionRequestHandler transactionRequestHandler = new TransactionRequestHandler();
        TransactionClassificationRequest transactionClassificationRequest = new TransactionClassificationRequest(excelFilePath);
        classifiedTransactions = transactionRequestHandler.handleTransactionClassificationRequest(transactionClassificationRequest);
        assertEquals(4, classifiedTransactions.size(), "The number of classified transactions is not matching");
    }

    @Test
    void handleTransactionImportRequestTest() throws ServiceException {
        TransactionRequestHandler transactionRequestHandler = new TransactionRequestHandler();
        TransactionImportRequest request = new TransactionImportRequest("1", classifiedTransactions);
        List<Map<Transaction, DynamicBillType>> map = transactionRequestHandler.handleTransactionImportRequest(request);
        assertEquals(4, map.size(), "The number of imported transactions is not matching");
    }
}
