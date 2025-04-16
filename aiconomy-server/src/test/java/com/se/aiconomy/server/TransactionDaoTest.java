package com.se.aiconomy.server;

import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.model.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionDaoTest extends TransactionDao {
    private final List<List<Transaction>> savedBatches = new ArrayList<>();

    private RuntimeException simulateError;
    private int saveCallCount = 0;

    @Override
    public void batchSave(List<Transaction> transactions) {
        if (simulateError != null) {
            throw simulateError;
        }
        savedBatches.add(new ArrayList<>(transactions));
        saveCallCount++;
    }

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
