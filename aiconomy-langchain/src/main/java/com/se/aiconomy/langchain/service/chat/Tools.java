package com.se.aiconomy.langchain.service.chat;

import com.se.aiconomy.langchain.common.model.BillType;
import com.se.aiconomy.langchain.common.model.Budget;
import com.se.aiconomy.langchain.common.model.Transaction;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tools {

    private static final Logger log = LoggerFactory.getLogger(Tools.class);

    // TODO: 等待 server 获取真正的用户交易数据
    @Tool("Get transactions by userId")
    public List<Transaction> getUserTransactions(@P("userId") String userId) {
        log.info("Tool Executed: Fetching transactions for userId: {}", userId);

        ArrayList<Transaction> transactions = new ArrayList<>(List.of(
            new Transaction(LocalDateTime.now(), "Expense", "KFC", "Burger Combo",
                "Expense", "45.0", "Alipay", "Success", "TXN001", "M001", "Lunch"),
            new Transaction(LocalDateTime.now(), "Expense", "DiDi", "Ride",
                "Expense", "32.5", "WeChat Pay", "Success", "TXN002", "M002", "Ride home"),
            new Transaction(LocalDateTime.now(), "Expense", "JD", "Electronics",
                "Expense", "999.9", "Credit Card", "Success", "TXN003", "M003", "Buy headphones"),
            new Transaction(LocalDateTime.now(), "Salary", "XX Company", "Monthly Salary",
                "Income", "15000.0", "Bank Transfer", "Success", "TXN004", "M004", "March Salary")
        ));

        for (Transaction transaction : transactions) {
            transaction.addExtraField("processTime", LocalDateTime.now().toString());
            transaction.addExtraField("deviceId", "TEST-DEVICE-001");
        }

        log.info("Fetched {} transactions for userId: {}", transactions.size(), userId);

        return transactions;
    }

    // TODO: 等待 server 获取真正的用户预算数据
    @Tool("Get user budget information by userId")
    public Budget getUserBudget(@P("userId") String userId) {
        log.info("Tool Executed: Fetching budget for userId: {}", userId);

        Budget budget = new Budget();
        budget.setTotalBudget(10000);
        budget.setSpent(5000);
        budget.setAlerts(2);
        budget.setCategoryBudgets(new ArrayList<>(
            List.of(
                new Budget.CategoryBudget(BillType.DINING, 1500, 1650),
                new Budget.CategoryBudget(BillType.EDUCATION, 1000, 800),
                new Budget.CategoryBudget(BillType.ENTERTAINMENT, 500, 300)
            )
        ));

        return budget;
    }
}
