package com.se.aiconomy.server.dao;

import com.se.aiconomy.server.model.entity.Transaction;
import io.jsondb.JsonDBTemplate;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionDao {
    private final JsonDBTemplate jsonDBTemplate;

    public TransactionDao(JsonDBTemplate jsonDBTemplate) {
        this.jsonDBTemplate = jsonDBTemplate;
    }

//    初始化collection表
    private void initializeCollection() {
        try {
            jsonDBTemplate.createCollection(Transaction.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    保存或更新交易记录
    public void save(Transaction transaction) {
        if (transaction.getId() == null) {
            jsonDBTemplate.insert(transaction);
        } else {
            jsonDBTemplate.upsert(transaction);
        }
    }

//    批量保存
    public void batchSave(List<Transaction> transactions) {
        transactions.forEach(this::save);
    }

//    根据id查找记录
    public Transaction findById(String id) {
        return jsonDBTemplate.findById(id, Transaction.class);
    }

//    查询所有记录
    public List<Transaction> findAll() {
        String jxQuery = String.format("/.[%s]", "true");
        return jsonDBTemplate.find(jxQuery, Transaction.class)
                .stream()
                .sorted((t1, t2) -> t2.getTime().compareTo(t1.getTime()))
                .toList();
    }

    /**
     * 分页查询交易记录
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     */
    public List<Transaction> findPaged(int page, int pageSize) {
        String jxQuery = "/.[%s]";
        return jsonDBTemplate.find(jxQuery, Transaction.class)
                .stream()
                .sorted((t1, t2) -> t2.getTime().compareTo(t1.getTime()))
                .skip((page - 1) * (long) pageSize)
                .limit(pageSize)
                .toList();
    }

//    按照时间查询
    public List<Transaction> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        String jxQuery = String.format(
                "/.[time >= '%s' and time <= '%s']",
                start.toString(),
                end.toString()
        );
        return jsonDBTemplate.find(jxQuery, Transaction.class);
    }

//    按照类型查询
    public List<Transaction> findByType(String type) {
        String jxQuery = String.format("/.[type = '%s']", type);
        return jsonDBTemplate.find(jxQuery, Transaction.class);
    }

//  根据单号查询
    public Transaction findByMerchantOrderId(String orderId) {
        String jxQuery = String.format("/.[merchantOrderId = '%s']", orderId);
        List<Transaction> result = jsonDBTemplate.find(jxQuery, Transaction.class);
        return result.isEmpty() ? null : result.get(0);
    }

//  删除交易记录
    public void delete(String id) {
        Transaction transaction = findById(id);
        if (transaction != null) {
            jsonDBTemplate.remove(transaction, Transaction.class);
        }
    }
}
