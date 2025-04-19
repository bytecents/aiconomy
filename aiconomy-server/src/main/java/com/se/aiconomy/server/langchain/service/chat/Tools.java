package com.se.aiconomy.server.langchain.service.chat;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Tools {

    private static final Logger log = LoggerFactory.getLogger(Tools.class);

    @Tool("Get transactions by userId")
    public List<Transaction> getUserTransactions(@P("userId") String userId) {
        log.info("Tool Executed: Fetching transactions for userId: {}", userId);

        TransactionService transactionService = new TransactionServiceImpl();
        List<Transaction> transactions;
        try {
            transactions = transactionService.getTransactionsByUserId(userId);
        } catch (ServiceException e) {
            transactions = List.of();
        }

        log.info("Fetched {} transactions for userId: {}", transactions.size(), userId);

        return transactions;
    }

    @Tool("Get user budget information by userId")
    public List<Budget> getUserBudget(@P("userId") String userId) {
        log.info("Tool Executed: Fetching budget for userId: {}", userId);

        JSONStorageService jsonStorageService = JSONStorageServiceImpl.getInstance();
        BudgetService budgetService = new BudgetServiceImpl(jsonStorageService);

        return budgetService.getBudgetsByUserId(userId);
    }
}
