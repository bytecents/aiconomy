package com.se.aiconomy.server.service;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * TransactionService interface provides methods for transaction classification, extraction,
 * storage, retrieval, export, searching, and statistics for user transactions.
 */
public interface TransactionService {

    /**
     * Classifies a list of transactions.
     *
     * @param transactions the list of transaction DTOs to classify
     * @return a list of maps, each mapping a Transaction to its DynamicBillType
     */
    List<Map<Transaction, DynamicBillType>> classifyTransactions(List<TransactionDto> transactions);

    /**
     * Extracts and classifies transactions from a CSV file.
     *
     * @param filePath the path to the CSV file
     * @return a list of maps, each mapping a Transaction to its DynamicBillType
     * @throws ServiceException if extraction or classification fails
     */
    List<Map<Transaction, DynamicBillType>> extractTransactionFromCSV(String filePath) throws ServiceException;

    /**
     * Extracts and classifies transactions from an Excel file.
     *
     * @param filePath the path to the Excel file
     * @return a list of maps, each mapping a Transaction to its DynamicBillType
     * @throws ServiceException if extraction or classification fails
     */
    List<Map<Transaction, DynamicBillType>> extractTransactionFromExcel(String filePath) throws ServiceException;

    /**
     * Extracts and classifies transactions from a JSON string.
     *
     * @param jsonString the JSON string containing transaction data
     * @return a list of maps, each mapping a Transaction to its DynamicBillType
     * @throws ServiceException if extraction or classification fails
     */
    List<Map<Transaction, DynamicBillType>> extractTransactionFromJson(String jsonString) throws ServiceException;

    /**
     * Saves classified transactions for a user.
     *
     * @param userId                 the user ID
     * @param classifiedTransactions the list of classified transactions to save
     * @return the saved transactions as a list of maps
     * @throws ServiceException if saving fails
     */
    List<Map<Transaction, DynamicBillType>> saveTransaction(String userId, List<Map<Transaction, DynamicBillType>> classifiedTransactions) throws ServiceException;

    /**
     * Saves classified transactions for a user and a specific account.
     *
     * @param userId                 the user ID
     * @param accountId              the account ID
     * @param classifiedTransactions the list of classified transactions to save
     * @return the saved transactions as a list of maps
     * @throws ServiceException if saving fails
     */
    List<Map<Transaction, DynamicBillType>> saveTransaction(String userId, String accountId, List<Map<Transaction, DynamicBillType>> classifiedTransactions) throws ServiceException;

    /**
     * Retrieves all transactions for a user.
     *
     * @param userId the user ID
     * @return a list of transaction DTOs
     * @throws ServiceException if retrieval fails
     */
    List<TransactionDto> getTransactionsByUserId(String userId) throws ServiceException;

    /**
     * Retrieves all transactions for a specific account and user.
     *
     * @param accountId the account ID
     * @param userId    the user ID
     * @return a list of transaction DTOs
     * @throws ServiceException if retrieval fails
     */
    List<TransactionDto> getTransactionsByAccountId(String accountId, String userId) throws ServiceException;

    /**
     * Exports all transactions to a JSON file.
     *
     * @param filePath the path to the output JSON file
     * @throws ServiceException if export fails
     */
    void exportTransactionsToJson(String filePath) throws ServiceException;

    /**
     * Exports all transactions to a CSV file.
     *
     * @param filePath the path to the output CSV file
     * @throws ServiceException if export fails
     */
    void exportTransactionsToCsv(String filePath) throws ServiceException;

    /**
     * Exports all transactions to an Excel file.
     *
     * @param filePath the path to the output Excel file
     * @throws ServiceException if export fails
     */
    void exportTransactionsToExcel(String filePath) throws ServiceException;

    /**
     * Searches for transactions based on the given criteria.
     *
     * @param criteria the search criteria
     * @return a list of transaction DTOs matching the criteria
     */
    List<TransactionDto> searchTransactions(TransactionServiceImpl.TransactionSearchCriteria criteria);

    /**
     * Updates the status of a transaction.
     *
     * @param transactionId the ID of the transaction to update
     * @param newStatus     the new status to set
     * @return the updated transaction DTO
     * @throws ServiceException if update fails
     */
    TransactionDto updateTransactionStatus(String transactionId, String newStatus) throws ServiceException;

    /**
     * Deletes a transaction by its ID.
     *
     * @param transactionId the ID of the transaction to delete
     * @throws ServiceException if deletion fails
     */
    void deleteTransaction(String transactionId) throws ServiceException;

    /**
     * Retrieves counterparty statistics for transactions.
     *
     * @return a map of counterparty names to statistics
     */
    Map<String, String> getCounterpartyStatistics();

    /**
     * Adds a transaction manually for a user.
     *
     * @param userId          the user ID
     * @param incomeOrExpense indicates if the transaction is income or expense
     * @param amount          the transaction amount
     * @param time            the transaction time
     * @param product         the product or description
     * @param type            the transaction type
     * @param accountId       the account ID
     * @param remark          additional remarks
     * @return the created transaction DTO
     * @throws ServiceException if creation fails
     */
    TransactionDto addTransactionManually(String userId, String incomeOrExpense, String amount,
                                          LocalDateTime time, String product, String type, String accountId, String remark) throws ServiceException;
}
