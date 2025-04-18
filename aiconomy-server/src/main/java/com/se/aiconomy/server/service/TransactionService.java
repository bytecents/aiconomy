package com.se.aiconomy.server.service;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.TransactionDto;

import java.util.List;
import java.util.Map;

public interface TransactionService {
    List<Map<Transaction, BillType>> classifyTransactions(List<TransactionDto> transactions);

    List<Map<Transaction, BillType>> extractTransactionFromCSV(String filePath) throws ServiceException;

    List<Map<Transaction, BillType>> extractTransactionFromExcel(String filePath) throws ServiceException;

    List<Map<Transaction, BillType>> extractTransactionFromJson(String jsonString) throws ServiceException;

    List<Map<Transaction, BillType>> saveTransaction(String userId, List<Map<Transaction, BillType>> classifiedTransactions) throws ServiceException;

    List<Transaction> getTransactionsByUserId(String userId) throws ServiceException;
}
