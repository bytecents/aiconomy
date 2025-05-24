package com.se.aiconomy.server.dao;

import com.se.aiconomy.server.model.dto.TransactionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Data Access Object for handling TransactionDto entities.
 */
public class TransactionDao extends AbstractDao<TransactionDto> {
    private static final Logger log = LoggerFactory.getLogger(TransactionDao.class);
    private static TransactionDao instance;

    public TransactionDao() {
        super(TransactionDto.class);
    }

    /**
     * Gets singleton instance of TransactionDao
     *
     * @return TransactionDao instance
     */
    public static synchronized TransactionDao getInstance() {
        if (instance == null) {
            instance = new TransactionDao();
        }
        return instance;
    }

    // create a transtcion record
    @Override
    public TransactionDto create(TransactionDto transaction) {
        if (transaction.getTime() == null) {
            transaction.setTime(LocalDateTime.now());
        }
        return super.create(transaction);
    }

    /**
     * Find transactions by type
     *
     * @param type the transaction type
     * @return list of transactions of the specified type
     */
    public List<TransactionDto> findByType(String type) {
        log.debug("Finding transactions of type: {}", type);
        return findAll().stream()
                .filter(t -> type.equals(t.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by status
     *
     * @param status the transaction status
     * @return list of transactions with the specified status
     */
    public List<TransactionDto> findByStatus(String status) {
        log.debug("Finding transactions with status: {}", status);
        return findAll().stream()
                .filter(t -> status.equals(t.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Find transactions within a time range
     *
     * @param startTime start of the time range
     * @param endTime   end of the time range
     * @return list of transactions within the specified time range
     */
    public List<TransactionDto> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("Finding transactions between {} and {}", startTime, endTime);
        return findAll().stream()
                .filter(t -> !t.getTime().isBefore(startTime) && !t.getTime().isAfter(endTime))
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by counterparty
     *
     * @param counterparty the counterparty to search for
     * @return list of transactions with the specified counterparty
     */
    public List<TransactionDto> findByCounterparty(String counterparty) {
        log.debug("Finding transactions with counterparty: {}", counterparty);
        return findAll().stream()
                .filter(t -> counterparty.equals(t.getCounterparty()))
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by payment method
     *
     * @param paymentMethod the payment method to search for
     * @return list of transactions with the specified payment method
     */
    public List<TransactionDto> findByPaymentMethod(String paymentMethod) {
        log.debug("Finding transactions with payment method: {}", paymentMethod);
        return findAll().stream()
                .filter(t -> paymentMethod.equals(t.getPaymentMethod()))
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by merchant order ID
     *
     * @param merchantOrderId the merchant order ID to search for
     * @return list of transactions with the specified merchant order ID
     */
    public List<TransactionDto> findByMerchantOrderId(String merchantOrderId) {
        log.debug("Finding transactions with merchant order ID: {}", merchantOrderId);
        return findAll().stream()
                .filter(t -> merchantOrderId.equals(t.getMerchantOrderId()))
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by income/expense type
     *
     * @param incomeOrExpense the income/expense type ("收入" or "支出")
     * @return list of transactions of the specified income/expense type
     */
    public List<TransactionDto> findByIncomeOrExpense(String incomeOrExpense) {
        log.debug("Finding transactions of type: {}", incomeOrExpense);
        return findAll().stream()
                .filter(t -> incomeOrExpense.equals(t.getIncomeOrExpense()))
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by product name
     *
     * @param product the product name to search for
     * @return list of transactions with the specified product
     */
    public List<TransactionDto> findByProduct(String product) {
        log.debug("Finding transactions for product: {}", product);
        return findAll().stream()
                .filter(t -> product.equals(t.getProduct()))
                .collect(Collectors.toList());
    }

    /**
     * Update transaction status
     *
     * @param transactionId the ID of the transaction
     * @param newStatus     the new status to set
     * @return updated transaction if found, null otherwise
     */
    public TransactionDto updateStatus(String transactionId, String newStatus) {
        log.debug("Updating status of transaction {} to {}", transactionId, newStatus);
        return findById(transactionId).map(transaction -> {
            transaction.setStatus(newStatus);
            return update(transaction);
        }).orElse(null);
    }

    /**
     * Search transactions by keyword in remark
     *
     * @param keyword the keyword to search for in remarks
     * @return list of transactions with matching remarks
     */
    public List<TransactionDto> searchByRemark(String keyword) {
        log.debug("Searching transactions with remark containing: {}", keyword);
        return findAll().stream()
                .filter(t -> t.getRemark() != null && t.getRemark().contains(keyword))
                .collect(Collectors.toList());
    }

    public List<TransactionDto> findByUserId(String userId) {
        log.debug("Finding transactions for user ID: {}", userId);
        return findAll().stream()
                .filter(t -> userId.equals(t.getUserId()))
                .collect(Collectors.toList());
    }

    /**
     * Delete a transaction by its ID.
     *
     * @param transactionId The ID of the transaction to delete
     * @return The deleted transaction if found, null otherwise
     */
    public TransactionDto deleteTransaction(String transactionId) {
        Optional<TransactionDto> transaction = findById(transactionId);
        if (transaction.isPresent()) {
            super.delete(transaction.get());  // Use the delete method from AbstractDao
            return transaction.get();
        }
        log.warn("Transaction with ID {} not found", transactionId);
        return null;
    }

    /**
     * Update an existing transaction in the storage.
     *
     * @param transaction The transaction to update
     * @return The updated transaction
     */
    public TransactionDto updateTransaction(TransactionDto transaction) {
        Optional<TransactionDto> existingTransaction = findById(transaction.getId());
        if (existingTransaction.isPresent()) {
            return super.update(transaction);  // Use the update method from AbstractDao
        }
        log.warn("Transaction with ID {} not found", transaction.getId());
        return null;
    }

}