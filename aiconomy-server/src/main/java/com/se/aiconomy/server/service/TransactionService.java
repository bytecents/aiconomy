package com.se.aiconomy.server.service;

import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.common.utils.CSVUtils;
import com.se.aiconomy.server.common.utils.ExcelUtils;
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
     * 获取文件的扩展名
     *
     * @param filePath 文件路径
     * @return 文件扩展名（小写）
     */
    private String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";  // 没有扩展名
        }
        return filePath.substring(dotIndex + 1).toLowerCase();  // 返回扩展名并转换为小写
    }

    /**
     * 根据文件类型导入交易记录（CSV 或 Excel）
     *
     * @param filePath 文件路径
     * @return 成功导入的交易记录列表
     * @throws IOException      文件读取异常
     * @throws ServiceException 服务异常
     */
    public List<TransactionDto> importTransactions(String filePath) throws IOException, ServiceException {
        String fileExtension = getFileExtension(filePath);  // 获取文件扩展名
        List<TransactionDto> transactions = null;

        try {
            // 根据文件扩展名判断文件类型，选择使用相应的导入工具
            if ("csv".equalsIgnoreCase(fileExtension)) {
                transactions = importFromCSV(filePath);
            } else if ("xlsx".equalsIgnoreCase(fileExtension) || "xls".equalsIgnoreCase(fileExtension)) {
                transactions = importFromExcel(filePath);
            } else {
                // 抛出异常时，确保传递消息和 cause（第二个参数）
                throw new ServiceException("Unsupported file type: " + fileExtension, null); // 无底层异常传 null
            }

            // 处理交易记录逻辑
            return processImportedTransactions(transactions);

        } catch (IOException e) {
            // 捕获 IOException 异常时传递它作为 cause
            throw new ServiceException("Failed to read file: " + filePath, e);  // 将 IOException 作为 cause
        } catch (Exception e) {
            // 捕获任何其他异常，并将其作为 cause
            throw new ServiceException("Unexpected error during import", e);  // 将 Exception 作为 cause
        }
    }


    /**
     * 从 CSV 文件导入交易记录
     *
     * @param filePath 文件路径
     * @return 导入的交易记录列表
     * @throws IOException      文件读取异常
     * @throws ServiceException 服务异常
     */
    private List<TransactionDto> importFromCSV(String filePath) throws IOException, ServiceException {
        return CSVUtils.readCsv(filePath, TransactionDto.class);
    }

    /**
     * 从 Excel 文件导入交易记录
     *
     * @param filePath 文件路径
     * @return 导入的交易记录列表
     * @throws IOException      文件读取异常
     * @throws ServiceException 服务异常
     */
    private List<TransactionDto> importFromExcel(String filePath) throws IOException, ServiceException {
        return ExcelUtils.readExcel(filePath, TransactionDto.class);
    }

    /**
     * 处理导入的交易记录
     *
     * @param transactions 导入的交易记录
     * @return 处理后的交易记录列表
     */
    private List<TransactionDto> processImportedTransactions(List<TransactionDto> transactions) throws ServiceException {
        List<TransactionDto> validTransactions = new ArrayList<>();

        // 处理每一条记录
        for (TransactionDto transaction : transactions) {
            // 生成ID（如果不存在）
            if (transaction.getId() == null || transaction.getId().isEmpty()) {
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
            try {
                batchSave(validTransactions);  // 调用 batchSave 并处理 ServiceException
                log.info("Successfully imported {} transactions", validTransactions.size());
            } catch (ServiceException e) {
                log.error("Failed to save transactions: {}", e.getMessage(), e);
                throw e;  // 重新抛出异常，或者做其他异常处理
            }
        } else {
            log.warn("No valid transactions found to import");
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
            throw new ServiceException("数据保存失败", e); // 这里传递了底层异常 e
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