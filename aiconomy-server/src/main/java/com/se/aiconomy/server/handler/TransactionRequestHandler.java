package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.common.utils.FileUtils;
import com.se.aiconomy.server.langchain.common.model.BillType;
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

public class TransactionRequestHandler {

    private final TransactionService transactionService;

    public TransactionRequestHandler() {
        this.transactionService = new TransactionServiceImpl();
    }

    public TransactionRequestHandler(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public List<Map<Transaction, DynamicBillType>> handleTransactionClassificationRequest(TransactionClassificationRequest request) throws ServiceException {
        // 1. 从request中获取用户ID和交易记录
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

    public List<Map<Transaction, DynamicBillType>> handleTransactionImportRequest(TransactionImportRequest request) throws ServiceException {
        // 1. 从请求参数中获取用户ID和分类后的交易记录
        String userId = request.getUserId();
        String accountId = request.getAccountId();
        List<Map<Transaction, DynamicBillType>> transactions = request.getTransactions();

        // 2. 将分类后的交易记录存储到数据库
        if (accountId == null || accountId.isEmpty()) {
            return transactionService.saveTransaction(userId, transactions);
        } else {
            return transactionService.saveTransaction(userId, accountId, transactions);
        }
    }

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

    // 根据账户 ID 获取交易记录
    public List<TransactionDto> handleGetTransactionsByAccountId(String userId, String accountId) throws ServiceException {
        if (userId == null || userId.isEmpty()) {
            throw new ServiceException("User ID cannot be null or empty", null);
        }
        if (accountId == null || accountId.isEmpty()) {
            throw new ServiceException("Account ID cannot be null or empty", null);
        }
        return transactionService.getTransactionsByAccountId(accountId, userId);
    }

    // 按条件搜索交易记录
    public List<TransactionDto> handleSearchTransactions(TransactionServiceImpl.TransactionSearchCriteria criteria) throws ServiceException {
        if (criteria == null) {
            throw new ServiceException("Search criteria cannot be null", null);
        }
        return transactionService.searchTransactions(criteria);
    }

    // 导出交易记录到 CSV 文件
    public void handleExportTransactionsToCsv(String filePath) throws ServiceException {
        if (filePath == null || filePath.isEmpty()) {
            throw new ServiceException("File path cannot be null or empty", null);
        }
        transactionService.exportTransactionsToCsv(filePath);
    }

    // 导出交易记录到 Excel 文件
    public void handleExportTransactionsToExcel(String filePath) throws ServiceException {
        if (filePath == null || filePath.isEmpty()) {
            throw new ServiceException("File path cannot be null or empty", null);
        }
        transactionService.exportTransactionsToExcel(filePath);
    }

    // 更新交易状态
    public TransactionDto handleUpdateTransactionStatus(String transactionId, String status) throws ServiceException {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new ServiceException("Transaction ID cannot be null or empty", null);
        }
        if (status == null || status.isEmpty()) {
            throw new ServiceException("Status cannot be null or empty", null);
        }
        return transactionService.updateTransactionStatus(transactionId, status);
    }

    // 删除交易记录
    public void handleDeleteTransaction(String transactionId) throws ServiceException {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new ServiceException("Transaction ID cannot be null or empty", null);
        }
        transactionService.deleteTransaction(transactionId);
    }

    // 获取交易对象
    public Map<String, String> handleGetCounterpartyStatistics() {
        return transactionService.getCounterpartyStatistics();
    }

    // 手动添加交易记录
    public TransactionDto handleAddTransactionManually(String userId, String incomeOrExpense, String amount,
                                                       LocalDateTime time, String product, String type, String accountId)
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
        return transactionService.addTransactionManually(userId, incomeOrExpense, amount, time, product, type, accountId);
    }
}
