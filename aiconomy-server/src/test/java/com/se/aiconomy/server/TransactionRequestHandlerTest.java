package com.se.aiconomy.server;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.handler.TransactionRequestHandler;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.service.TransactionService;
import com.se.aiconomy.server.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRequestHandlerTest {

    private static final String TEST_USER = "test-user";
    private static final LocalDateTime TEST_TIME = LocalDateTime.of(2025, 5, 23, 10, 28); // 当前时间 2025-05-23 10:28 PDT

    private TransactionRequestHandler transactionRequestHandler;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        // 初始化 TransactionServiceImpl（无参构造函数）
        transactionService = new TransactionServiceImpl();
        // 初始化 TransactionRequestHandler
        transactionRequestHandler = new TransactionRequestHandler(transactionService);
    }

    @Test
    void testHandleAddTransactionManually_ValidInput() throws ServiceException {
        // 准备输入，类似 createTestTransaction
        String userId = TEST_USER;
        String incomeOrExpense = "支出";
        String amount = "5999.00";
        LocalDateTime time = TEST_TIME;
        String product = "笔记本电脑";
        String type = "消费";
        String accountId = "userAcc001";
        String remark = "购买新电脑";

        // 调用接口
        TransactionDto result = transactionRequestHandler.handleAddTransactionManually(
                userId, incomeOrExpense, amount, time, product, type, accountId, remark
        );

        // 验证结果
        assertNotNull(result, "交易记录不应为空");
        assertNotNull(result.getId(), "交易 ID 不应为空");
        assertEquals(userId, result.getUserId(), "用户 ID 应匹配");
        assertEquals(incomeOrExpense, result.getIncomeOrExpense(), "收支类型应匹配");
        assertEquals(amount, result.getAmount(), "金额应匹配");
        assertEquals(time, result.getTime(), "时间应匹配");
        assertEquals(product, result.getProduct(), "产品应匹配");
        assertEquals(type, result.getType(), "交易类型应匹配");
        assertEquals(accountId, result.getAccountId(), "账户 ID 应匹配");
        assertEquals(remark, result.getRemark(), "备注应匹配");
    }
}

// 临时内存实现的 TransactionDao
class InMemoryTransactionDao implements TransactionDao {
    private final List<TransactionDto> transactions = new ArrayList<>();

    @Override
    public TransactionDto create(TransactionDto transaction) {
        transactions.add(transaction);
        return transaction;
    }

    @Override
    public List<TransactionDto> findAll() {
        return new ArrayList<>(transactions);
    }

    public static InMemoryTransactionDao getInstance() {
        return new InMemoryTransactionDao();
    }
}

// TransactionDao 接口
interface TransactionDao {
    TransactionDto create(TransactionDto transaction);
    List<TransactionDto> findAll();
    static TransactionDao getInstance() {
        return InMemoryTransactionDao.getInstance();
    }
}