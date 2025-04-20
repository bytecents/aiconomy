package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.common.utils.FileUtils;
import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.model.dto.transaction.request.GetTransactionByUserIdRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionClassificationRequest;
import com.se.aiconomy.server.model.dto.transaction.request.TransactionImportRequest;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;

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

    public List<Map<Transaction, BillType>> handleTransactionClassificationRequest(TransactionClassificationRequest request) throws ServiceException {
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

    public List<Map<Transaction, BillType>> handleTransactionImportRequest(TransactionImportRequest request) throws ServiceException {
        // 1. 从请求参数中获取用户ID和分类后的交易记录
        String userId = request.getUserId();
        String accountId = request.getAccountId();
        List<Map<Transaction, BillType>> transactions = request.getTransactions();

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
}
