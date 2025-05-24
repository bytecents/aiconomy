package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.common.utils.FileUtils;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.dto.transaction.request.GetTransactionByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionClassificationRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionImportRequest;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Handler for processing transaction-related requests.
 * Provides methods for classifying, importing, retrieving, searching, exporting, updating, deleting, and manually adding transactions.
 */
public class TransactionRequestHandler {

    private final TransactionService transactionService;

    /**
     * Default constructor initializing TransactionService with its implementation.
     */
    public TransactionRequestHandler() {
        this.transactionService = new TransactionServiceImpl();
    }

    /**
     * Constructor with custom TransactionService.
     *
     * @param transactionService the transaction service to use
     */
    public TransactionRequestHandler(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Handles the request to classify transactions from a file.
     *
     * @param request the transaction classification request containing user ID and file path
     * @return a list of maps pairing transactions with their dynamic bill types
     * @throws ServiceException if the file path is invalid or file type is unsupported
     */
    public List<Map<Transaction, DynamicBillType>> handleTransactionClassificationRequest(TransactionClassificationRequest request) throws ServiceException {
        // 1. Get user ID and transaction file from request
        String userId = request.getUserId();
        String filePath = request.getFilePath();

        if (filePath == null || filePath.isEmpty()) {
            throw new ServiceException("File path cannot be null or empty", null);
        }

        String fileExtension = FileUtils.getFileExtension(filePath);

        return switch (fileExtension) {
            case "csv" -> transactionService.extractTransactionFromCSV(filePath);
            case "xlsx" -> transactionService.extractTransactionFromExcel(filePath);
            default -> throw new ServiceException("Unsupported file type: " + fileExtension, null);
        };
    }

    /**
     * Handles the request to import classified transactions.
     *
     * @param request the transaction import request containing user ID, account ID, and transactions
     * @return a list of maps pairing transactions with their dynamic bill types after saving
     * @throws ServiceException if saving fails
     */
    public List<Map<Transaction, DynamicBillType>> handleTransactionImportRequest(TransactionImportRequest request) throws ServiceException {
        // 1. Get user ID, account ID, and classified transactions from request
        String userId = request.getUserId();
        String accountId = request.getAccountId();
        List<Map<Transaction, DynamicBillType>> transactions = request.getTransactions();

        // 2. Save classified transactions to the database
        if (accountId == null || accountId.isEmpty()) {
            return transactionService.saveTransaction(userId, transactions);
        } else {
            return transactionService.saveTransaction(userId, accountId, transactions);
        }
    }

    /**
     * Handles the request to get transactions by user ID.
     *
     * @param request the request containing the user ID
     * @return a list of transaction DTOs for the user
     * @throws ServiceException if the user ID is invalid
     */
    public List<TransactionDto> handleGetTransactionsByUserId(GetTransactionByUserIdRequest request) throws ServiceException {
        String userId = request.getUserId();
        if (userId == null || userId.isEmpty()) {
            throw new ServiceException("User ID cannot be null or empty", null);
        }
        List<TransactionDto> transactions;
        try {
            transactions = transactionService.getTransactionsByUserId(userId);
        } catch (ServiceException e) {
            transactions = List.of();
        }
        return transactions;
    }

    /**
     * Handles the request to get transactions by account ID.
     *
     * @param userId    the user ID
     * @param accountId the account ID
     * @return a list of transaction DTOs for the account
     * @throws ServiceException if user ID or account ID is invalid
     */
    public List<TransactionDto> handleGetTransactionsByAccountId(String userId, String accountId) throws ServiceException {
        if (userId == null || userId.isEmpty()) {
            throw new ServiceException("User ID cannot be null or empty", null);
        }
        if (accountId == null || accountId.isEmpty()) {
            throw new ServiceException("Account ID cannot be null or empty", null);
        }
        return transactionService.getTransactionsByAccountId(accountId, userId);
    }

    /**
     * Handles the request to search transactions by criteria.
     *
     * @param criteria the transaction search criteria
     * @return a list of transaction DTOs matching the criteria
     * @throws ServiceException if the criteria is null
     */
    public List<TransactionDto> handleSearchTransactions(TransactionServiceImpl.TransactionSearchCriteria criteria) throws ServiceException {
        if (criteria == null) {
            throw new ServiceException("Search criteria cannot be null", null);
        }
        return transactionService.searchTransactions(criteria);
    }

    /**
     * Handles the request to export transactions to a CSV file.
     *
     * @param filePath the file path to export to
     * @throws ServiceException if the file path is invalid
     */
    public void handleExportTransactionsToCsv(String filePath) throws ServiceException {
        if (filePath == null || filePath.isEmpty()) {
            throw new ServiceException("File path cannot be null or empty", null);
        }
        transactionService.exportTransactionsToCsv(filePath);
    }

    /**
     * Handles the request to export transactions to an Excel file.
     *
     * @param filePath the file path to export to
     * @throws ServiceException if the file path is invalid
     */
    public void handleExportTransactionsToExcel(String filePath) throws ServiceException {
        if (filePath == null || filePath.isEmpty()) {
            throw new ServiceException("File path cannot be null or empty", null);
        }
        transactionService.exportTransactionsToExcel(filePath);
    }

    /**
     * Handles the request to update the status of a transaction.
     *
     * @param transactionId the transaction ID
     * @param status        the new status
     * @return the updated transaction DTO
     * @throws ServiceException if transaction ID or status is invalid
     */
    public TransactionDto handleUpdateTransactionStatus(String transactionId, String status) throws ServiceException {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new ServiceException("Transaction ID cannot be null or empty", null);
        }
        if (status == null || status.isEmpty()) {
            throw new ServiceException("Status cannot be null or empty", null);
        }
        return transactionService.updateTransactionStatus(transactionId, status);
    }

    /**
     * Handles the request to delete a transaction.
     *
     * @param transactionId the transaction ID
     * @throws ServiceException if the transaction ID is invalid
     */
    public void handleDeleteTransaction(String transactionId) throws ServiceException {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new ServiceException("Transaction ID cannot be null or empty", null);
        }
        transactionService.deleteTransaction(transactionId);
    }

    /**
     * Handles the request to get counterparty statistics.
     *
     * @return a map of counterparty statistics
     */
    public Map<String, String> handleGetCounterpartyStatistics() {
        return transactionService.getCounterpartyStatistics();
    }

    /**
     * Handles the request to manually add a transaction.
     *
     * @param userId          the user ID
     * @param incomeOrExpense income or expense type
     * @param amount          the transaction amount
     * @param time            the transaction time
     * @param product         the product name
     * @param type            the transaction type
     * @param accountId       the account ID
     * @param remark          additional remarks
     * @return the added transaction DTO
     * @throws ServiceException if any required parameter is invalid
     */
    public TransactionDto handleAddTransactionManually(String userId, String incomeOrExpense, String amount,
                                                       LocalDateTime time, String product, String type, String accountId, String remark)
            throws ServiceException {
        if (userId == null || userId.isEmpty()) {
            throw new ServiceException("User ID cannot be null or empty", null);
        }
        if (incomeOrExpense == null || incomeOrExpense.isEmpty()) {
            throw new ServiceException("Income or expense cannot be null or empty", null);
        }
        if (amount == null || amount.isEmpty()) {
            throw new ServiceException("Amount cannot be null or empty", null);
        }
        if (time == null) {
            throw new ServiceException("Transaction time cannot be null", null);
        }
        if (product == null || product.isEmpty()) {
            throw new ServiceException("Product cannot be null or empty", null);
        }
        if (type == null || type.isEmpty()) {
            throw new ServiceException("Transaction type cannot be null or empty", null);
        }
        if (accountId == null || accountId.isEmpty()) {
            throw new ServiceException("Account ID cannot be null or empty", null);
        }
        return transactionService.addTransactionManually(userId, incomeOrExpense, amount, time, product, type, accountId, remark);
    }
}