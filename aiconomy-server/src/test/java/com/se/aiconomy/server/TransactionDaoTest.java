package com.se.aiconomy.server;

import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.model.entity.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TransactionDaoTest extends TransactionDao {
    // 记录保存的批次数据
    private final List<List<Transaction>> savedBatches = new ArrayList<>();
    // 模拟异常
    private RuntimeException simulateError;
    // 记录调用次数
    private int saveCallCount = 0;

    @Override
    public void batchSave(List<Transaction> transactions) {
        if (simulateError != null) {
            throw simulateError;
        }
        savedBatches.add(new ArrayList<>(transactions));
        saveCallCount++;
    }

    // 测试验证方法
    public List<List<Transaction>> getSavedBatches() {
        return savedBatches;
    }

    public int getSaveCallCount() {
        return saveCallCount;
    }

    public void setSimulateError(RuntimeException error) {
        this.simulateError = error;
    }
}
