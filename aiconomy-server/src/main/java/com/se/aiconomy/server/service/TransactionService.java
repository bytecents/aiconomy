package com.se.aiconomy.server.service;

import com.se.aiconomy.langchain.common.model.Transaction;
import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.common.utils.CSVUtils;
import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.model.dto.TransactionDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 交易记录服务类，提供交易记录的业务逻辑处理
 */
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final TransactionDao transactionDao;

    public TransactionService() {
        this.transactionDao = TransactionDao.getInstance();
    }

    /**
     * 执行CSV文件导入
     *
     * @param filePath CSV文件路径
     * @return 成功导入的记录列表
     * @throws IOException      文件读取异常
     * @throws ServiceException 服务异常
     */
    public List<TransactionDto> importTransactions(String filePath) throws IOException, ServiceException {
        List<TransactionDto> transactions;
        List<TransactionDto> validTransactions = new ArrayList<>();

        try {
            // 读取CSV文件，转换为Transaction对象列表
            transactions = CSVUtils.readCsv(filePath, TransactionDto.class);
            log.info("Read {} records from CSV file: {}", transactions.size(), filePath);

            // 处理每一条记录
            for (TransactionDto transaction : transactions) {
                // 生成ID（如果不存在）
                if (StringUtils.isBlank(transaction.getId())) {
                    transaction.setId(UUID.randomUUID().toString());
                }

                // 设置时间（如果不存在）
                if (transaction.getTime() == null) {
                    transaction.setTime(LocalDateTime.now());
                }

                // 添加到有效记录列表
                validTransactions.add(transaction);
                log.debug("Valid transaction processed: {}", transaction);
            }

            // 批量保存有效交易记录
            if (!validTransactions.isEmpty()) {
                batchSave(validTransactions);
                log.info("Successfully imported {} transactions out of {} total records",
                    validTransactions.size(), transactions.size());
            } else {
                log.warn("No valid transactions found to import");
            }

        } catch (IOException e) {
            log.error("Failed to read CSV file: {}", filePath, e);
            throw new IOException("Failed to read CSV file: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during import: {}", e.getMessage(), e);
            throw new ServiceException("Failed to import transactions: " + e.getMessage(), e);
        }

        return validTransactions;
    }

    /**
     * 批量保存交易记录
     */
    private void batchSave(List<TransactionDto> transactions) throws ServiceException {
        try {
            for (TransactionDto transaction : transactions) {
                transactionDao.create(transaction);
            }
        } catch (Exception e) {
            log.error("Failed to save transactions: {}", e.getMessage(), e);
            throw new ServiceException("数据保存失败", e);
        }
    }

    /**
     * 获取指定时间范围内的交易记录
     */
    public List<TransactionDto> getTransactionsByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return transactionDao.findByTimeRange(startTime, endTime);
    }

    /**
     * 获取指定时间范围内的收支统计
     */
    public Map<String, String> getIncomeAndExpenseStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<TransactionDto> transactions = getTransactionsByDateRange(startTime, endTime);

        double totalIncome = transactions.stream()
            .filter(t -> "收入".equals(t.getIncomeOrExpense()))
            .mapToDouble(t -> Double.parseDouble(t.getAmount()))
            .sum();

        double totalExpense = transactions.stream()
            .filter(t -> "支出".equals(t.getIncomeOrExpense()))
            .mapToDouble(t -> Double.parseDouble(t.getAmount()))
            .sum();

        Map<String, String> statistics = new HashMap<>();
        statistics.put("totalIncome", String.format("%.2f", totalIncome));
        statistics.put("totalExpense", String.format("%.2f", totalExpense));
        statistics.put("netAmount", String.format("%.2f", totalIncome - totalExpense));

        return statistics;
    }

    /**
     * 获取按支付方式分组的交易统计
     */
    public Map<String, String> getPaymentMethodStatistics() {
        List<TransactionDto> allTransactions = transactionDao.findAll();

        return allTransactions.stream()
            .filter(t -> t.getPaymentMethod() != null)
            .collect(Collectors.groupingBy(
                TransactionDto::getPaymentMethod,
                Collectors.collectingAndThen(
                    Collectors.summingDouble(t -> Double.parseDouble(t.getAmount())),
                    sum -> String.format("%.2f", sum)
                )
            ));
    }

    /**
     * 按交易对手统计交易金额
     */
    public Map<String, String> getCounterpartyStatistics() {
        List<TransactionDto> allTransactions = transactionDao.findAll();

        return allTransactions.stream()
            .filter(t -> t.getCounterparty() != null)
            .collect(Collectors.groupingBy(
                TransactionDto::getCounterparty,
                Collectors.collectingAndThen(
                    Collectors.summingDouble(t -> Double.parseDouble(t.getAmount())),
                    sum -> String.format("%.2f", sum)
                )
            ));
    }

    /**
     * 更新交易记录状态
     */
    public TransactionDto updateTransactionStatus(String transactionId, String newStatus) throws ServiceException {
        TransactionDto updated = transactionDao.updateStatus(transactionId, newStatus);
        if (updated == null) {
            throw new ServiceException("Transaction not found with ID: " + transactionId, null);
        }
        return updated;
    }

    /**
     * 搜索交易记录
     * 支持按多个条件组合搜索
     */
    public List<TransactionDto> searchTransactions(TransactionSearchCriteria criteria) {
        List<TransactionDto> allTransactions = transactionDao.findAll();

        return allTransactions.stream()
            .filter(criteria::matches)
            .collect(Collectors.toList());
    }

    /**
     * 删除交易记录
     */
    public void deleteTransaction(String transactionId) throws ServiceException {
        Optional<TransactionDto> transaction = transactionDao.findById(transactionId);
        if (transaction.isPresent()) {
            transactionDao.delete(transaction.get());
        } else {
            throw new ServiceException("Transaction not found with ID: " + transactionId, null);
        }
    }

    /**
     * 内部类：交易搜索条件
     */
    @Getter
    @Setter
    public static class TransactionSearchCriteria {
        private String type;
        private String counterparty;
        private String paymentMethod;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String incomeOrExpense;
        private String product;

        public boolean matches(TransactionDto transaction) {
            return (type == null || type.equals(transaction.getType())) &&
                (counterparty == null || counterparty.equals(transaction.getCounterparty())) &&
                (paymentMethod == null || paymentMethod.equals(transaction.getPaymentMethod())) &&
                (status == null || status.equals(transaction.getStatus())) &&
                (incomeOrExpense == null || incomeOrExpense.equals(transaction.getIncomeOrExpense())) &&
                (product == null || product.equals(transaction.getProduct())) &&
                (startTime == null || !transaction.getTime().isBefore(startTime)) &&
                (endTime == null || !transaction.getTime().isAfter(endTime));
        }

        public static class Builder {
            private final TransactionSearchCriteria criteria = new TransactionSearchCriteria();

            public Builder withType(String type) {
                criteria.type = type;
                return this;
            }

            public Builder withCounterparty(String counterparty) {
                criteria.counterparty = counterparty;
                return this;
            }

            public Builder withPaymentMethod(String paymentMethod) {
                criteria.paymentMethod = paymentMethod;
                return this;
            }

            public Builder withStatus(String status) {
                criteria.status = status;
                return this;
            }

            public Builder withDateRange(LocalDateTime startTime, LocalDateTime endTime) {
                criteria.startTime = startTime;
                criteria.endTime = endTime;
                return this;
            }

            public Builder withIncomeOrExpense(String incomeOrExpense) {
                criteria.incomeOrExpense = incomeOrExpense;
                return this;
            }

            public Builder withProduct(String product) {
                criteria.product = product;
                return this;
            }

            public TransactionSearchCriteria build() {
                return criteria;
            }
        }
    }
}