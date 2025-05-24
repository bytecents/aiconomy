package com.se.aiconomy.server.service;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TransactionService {
    List<Map<Transaction, DynamicBillType>> classifyTransactions(List<TransactionDto> transactions);

    List<Map<Transaction, DynamicBillType>> extractTransactionFromCSV(String filePath) throws ServiceException;

    List<Map<Transaction, DynamicBillType>> extractTransactionFromExcel(String filePath) throws ServiceException;

    List<Map<Transaction, DynamicBillType>> extractTransactionFromJson(String jsonString) throws ServiceException;

    List<Map<Transaction, DynamicBillType>> saveTransaction(String userId, List<Map<Transaction, DynamicBillType>> classifiedTransactions) throws ServiceException;

    List<Map<Transaction, DynamicBillType>> saveTransaction(String userId, String accountId, List<Map<Transaction, DynamicBillType>> classifiedTransactions) throws ServiceException;

    List<TransactionDto> getTransactionsByUserId(String userId) throws ServiceException;

    List<TransactionDto> getTransactionsByAccountId(String accountId, String userId) throws ServiceException;

    void exportTransactionsToJson(String filePath) throws ServiceException;

    void exportTransactionsToCsv(String filePath) throws ServiceException;

    void exportTransactionsToExcel(String filePath) throws ServiceException;

    List<TransactionDto> searchTransactions(TransactionServiceImpl.TransactionSearchCriteria criteria);

    TransactionDto updateTransactionStatus(String transactionId, String newStatus) throws ServiceException;

    void deleteTransaction(String transactionId) throws ServiceException;

    Map<String, String> getCounterpartyStatistics();

    TransactionDto addTransactionManually(String userId, String incomeOrExpense, String amount,
                                          LocalDateTime time, String product, String type, String accountId, String remark) throws ServiceException;
}
