package com.se.aiconomy.server.service.impl;


import com.se.aiconomy.server.common.exception.ServiceException;
import com.se.aiconomy.server.common.utils.CSVUtils;
import com.se.aiconomy.server.common.utils.ExcelUtils;
import com.se.aiconomy.server.common.utils.JsonUtils;
import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.langchain.service.classification.TransactionClassificationService;
import com.se.aiconomy.server.model.dto.TransactionDto;
import com.se.aiconomy.server.service.TransactionService;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.se.aiconomy.server.common.utils.FileUtils.getFileExtension;

/**
 * 交易记录服务类，提供交易记录的业务逻辑处理
 */
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionDao transactionDao;

    public TransactionServiceImpl() {
        this.transactionDao = TransactionDao.getInstance();
    }

    @Override
    public List<Map<Transaction, DynamicBillType>> classifyTransactions(List<TransactionDto> transactions) {
        List<Transaction> transactionList = transactions.stream()
                .map(transactionDto -> new Transaction(
                        transactionDto.getId(),
                        transactionDto.getTime(),
                        transactionDto.getType(),
                        transactionDto.getCounterparty(),
                        transactionDto.getProduct(),
                        transactionDto.getIncomeOrExpense(),
                        transactionDto.getAmount(),
                        "CNY",
                        transactionDto.getPaymentMethod(),
                        transactionDto.getStatus(),
                        transactionDto.getProduct(),
                        transactionDto.getAccountId(),
                        transactionDto.getRemark()
                ))
                .toList();

        List<Map<Transaction, DynamicBillType>> classifiedTransactions = new ArrayList<>();
        List<DynamicBillType> billTypes = new TransactionClassificationService().classifyTransactions(transactionList);
        System.out.println(billTypes);
        for (int i = 0; i < transactionList.size(); i++) {
            Map<Transaction, DynamicBillType> classifiedTransaction = new HashMap<>();
            classifiedTransaction.put(transactionList.get(i), billTypes.get(i));
            classifiedTransactions.add(classifiedTransaction);
        }
        log.info("Classified transactions: {}", classifiedTransactions);
        return classifiedTransactions;
    }

    @Override
    public List<Map<Transaction, DynamicBillType>> extractTransactionFromCSV(String filePath) throws ServiceException {
        List<TransactionDto> transactions;
        try {
            transactions = CSVUtils.readCsv(filePath, TransactionDto.class);
        } catch (IOException e) {
            throw new ServiceException("Failed to read CSV file: " + filePath, e);
        }

        return classifyTransactions(transactions);
    }

    @Override
    public List<Map<Transaction, DynamicBillType>> extractTransactionFromExcel(String filePath) throws ServiceException {
        List<TransactionDto> transactions;
        try {
            transactions = ExcelUtils.readExcel(filePath, TransactionDto.class);
        } catch (IOException e) {
            throw new ServiceException("Failed to read Excel file: " + filePath, e);
        }

        return classifyTransactions(transactions);
    }

    // TODO: 实现从 JSON 字符串中提取交易记录的方法
    @Override
    public List<Map<Transaction, DynamicBillType>> extractTransactionFromJson(String jsonString) throws ServiceException {
        return null;
    }

    @Override
    public List<Map<Transaction, DynamicBillType>> saveTransaction(String userId, List<Map<Transaction, DynamicBillType>> classifiedTransactions) throws ServiceException {
        List<TransactionDto> transactionDtos = new ArrayList<>();

        for (Map<Transaction, DynamicBillType> transactionBillTypeMap : classifiedTransactions) {
            for (Map.Entry<Transaction, DynamicBillType> entry : transactionBillTypeMap.entrySet()) {
                Transaction transaction = entry.getKey();
                DynamicBillType billType = entry.getValue();

                TransactionDto transactionDto = TransactionDto.builder()
                        .id(transaction.getId())
                        .time(transaction.getTime())
                        .type(transaction.getType())
                        .counterparty(transaction.getCounterparty())
                        .product(transaction.getProduct())
                        .incomeOrExpense(transaction.getIncomeOrExpense())
                        .amount(transaction.getAmount())
                        .paymentMethod(transaction.getPaymentMethod())
                        .status(transaction.getStatus())
                        .merchantOrderId(transaction.getMerchantOrderId())
                        .accountId(transaction.getAccountId())
                        .billType(billType)
                        .userId(userId)
                        .accountId(transaction.getAccountId())
                        .build();

                transactionDtos.add(transactionDto);
            }
        }

        processImportedTransactions(transactionDtos);

        return classifiedTransactions;
    }

    @Override
    public List<Map<Transaction, DynamicBillType>> saveTransaction(String userId, String accountId, List<Map<Transaction, DynamicBillType>> classifiedTransactions) throws ServiceException {
        List<TransactionDto> transactionDtos = new ArrayList<>();

        for (Map<Transaction, DynamicBillType> transactionBillTypeMap : classifiedTransactions) {
            for (Map.Entry<Transaction, DynamicBillType> entry : transactionBillTypeMap.entrySet()) {
                Transaction transaction = entry.getKey();
                DynamicBillType billType = entry.getValue();

                TransactionDto transactionDto = TransactionDto.builder()
                        .id(transaction.getId())
                        .time(transaction.getTime())
                        .type(transaction.getType())
                        .counterparty(transaction.getCounterparty())
                        .product(transaction.getProduct())
                        .incomeOrExpense(transaction.getIncomeOrExpense())
                        .amount(transaction.getAmount())
                        .paymentMethod(transaction.getPaymentMethod())
                        .status(transaction.getStatus())
                        .merchantOrderId(transaction.getMerchantOrderId())
                        .accountId(transaction.getAccountId())
                        .billType(billType)
                        .userId(userId)
                        .accountId(accountId)
                        .build();

                transactionDtos.add(transactionDto);
            }
        }

        processImportedTransactions(transactionDtos);

        return classifiedTransactions;
    }

    @Override
    public List<TransactionDto> getTransactionsByUserId(String userId) throws ServiceException {
        List<TransactionDto> transactionDtos = transactionDao.findByUserId(userId);
        if (transactionDtos == null || transactionDtos.isEmpty()) {
            throw new ServiceException("No transactions found for user ID: " + userId, null);
        }

        return transactionDtos;
    }

    /**
     * 根据文件类型导入交易记录（CSV 或 Excel 或 Json）
     *
     * @param filePath 文件路径
     * @return 成功导入的交易记录列表
     * @throws ServiceException 服务异常
     */
    public List<TransactionDto> importTransactions(String filePath) throws ServiceException {
        String fileExtension = getFileExtension(filePath);  // 获取文件扩展名
        List<TransactionDto> transactions;

        try {
            // 根据文件扩展名判断文件类型，选择使用相应的导入工具
            if ("csv".equalsIgnoreCase(fileExtension)) {
                transactions = readCSV(filePath);
            } else if ("xlsx".equalsIgnoreCase(fileExtension) || "xls".equalsIgnoreCase(fileExtension)) {
                transactions = readExcel(filePath);
            } else if ("json".equalsIgnoreCase(fileExtension)) {
                transactions = readJson(filePath);
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
     * @throws IOException 文件读取异常
     */
    private List<TransactionDto> readCSV(String filePath) throws IOException {
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
    private List<TransactionDto> readExcel(String filePath) throws IOException, ServiceException {
        return ExcelUtils.readExcel(filePath, TransactionDto.class);
    }

    //    从Json文件中导入交易记录
    private List<TransactionDto> readJson(String filePath) throws IOException {
        return JsonUtils.readJson(filePath);
    }

    @Override
    public void exportTransactionsToJson(String filePath) throws ServiceException {
        List<TransactionDto> transactions = transactionDao.findAll();
        if (transactions.isEmpty()) {
            log.warn("No transactions found to export");
            throw new ServiceException("No transactions available to export", null);
        }

        try {
            JsonUtils.writeJson(filePath, transactions);
            log.info("Successfully exported {} transactions to JSON file: {}", transactions.size(), filePath);
        } catch (IOException e) {
            log.error("Failed to export transactions to JSON file: {}", filePath, e);
            throw new ServiceException("Failed to export transactions to JSON file: " + filePath, e);
        }
    }

    @Override
    public void exportTransactionsToCsv(String filePath) throws ServiceException {
        List<TransactionDto> transactions = transactionDao.findAll();
        if (transactions.isEmpty()) {
            log.warn("No transactions found to export");
            throw new ServiceException("No transactions available to export", null);
        }

        try {
            CSVUtils.writeCsv(filePath, transactions);
            log.info("Successfully exported {} transactions to CSV file: {}", transactions.size(), filePath);
        } catch (IOException e) {
            log.error("Failed to export transactions to CSV file: {}", filePath, e);
            throw new ServiceException("Failed to export transactions to CSV file: " + filePath, e);
        }
    }

    @Override
    public void exportTransactionsToExcel(String filePath) throws ServiceException {
        List<TransactionDto> transactions = transactionDao.findAll();
        if (transactions.isEmpty()) {
            log.warn("No transactions found to export");
            throw new ServiceException("No transactions available to export", null);
        }

        try {
            ExcelUtils.writeExcel(filePath, transactions);
            log.info("Successfully exported {} transactions to Excel file: {}", transactions.size(), filePath);
        } catch (IOException e) {
            log.error("Failed to export transactions to Excel file: {}", filePath, e);
            throw new ServiceException("Failed to export transactions to Excel file: " + filePath, e);
        }
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
                transactionDao.upsert(transaction);
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
        List<TransactionDto> transactions = transactionDao.findAll();
        Map<String, Double> counterpartyTotals = new HashMap<>();

        for (TransactionDto tx : transactions) {
            String counterparty = tx.getCounterparty();
            if (counterparty != null && !counterparty.isEmpty()) {
                double amount = Double.parseDouble(tx.getAmount());
                counterpartyTotals.merge(counterparty, amount, Double::sum);
            }
        }

        Map<String, String> statistics = new HashMap<>();
        counterpartyTotals.forEach((key, value) -> statistics.put(key, value.toString()));
        return statistics;
    }

    /**
     * 更新交易记录状态
     */
    @Override
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
    @Override
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
     * 根据 accountId 获取所有的交易记录
     *
     * @param accountId 账户 ID, userId 用户Id
     * @return 返回与 accountId 关联的所有交易记录的 TransactionDto 列表
     * @throws ServiceException 如果未找到相关交易记录
     */
    public List<TransactionDto> getTransactionsByAccountId(String accountId, String userId) throws ServiceException {

        // 获取所有交易记录
        List<TransactionDto> allTransactions = transactionDao.findAll();

        // 过滤出 accountId 符合条件的交易记录
        List<TransactionDto> filteredTransactions = allTransactions.stream()
                .filter(transaction -> accountId.equals(transaction.getAccountId()) && userId.equals(transaction.getUserId())) // 根据 accountId 和 userId 过滤
                .collect(Collectors.toList());

        if (filteredTransactions.isEmpty()) {
            throw new ServiceException("No transactions found for accountId: " + accountId + " and userId: " + userId, null);
        }

        return filteredTransactions;
    }

    //手动添加交易记录
//    public TransactionDto addTransactionManually(String userId, String incomeOrExpense, String amount,
//                                                 LocalDateTime time, String product, String type, String accountId)
//            throws ServiceException {
//
//        if (userId == null || incomeOrExpense == null || amount == null || type == null || accountId == null) {
//            throw new ServiceException("Missing required fields for transaction", null);
//        }
//
//        TransactionDto transaction = new TransactionDto();
//        transaction.setUserId(userId);
//        transaction.setIncomeOrExpense(incomeOrExpense);
//        transaction.setAmount(amount);
//        transaction.setTime(time != null ? time : LocalDateTime.now());
//        transaction.setProduct(product);
//        transaction.setType(type);
//        transaction.setAccountId(accountId);
//
//        return transactionDao.create(transaction);
//    }
    @Override
    public TransactionDto addTransactionManually(String userId, String incomeOrExpense, String amount,
                                                 LocalDateTime time, String product, String type, String accountId, String remark)
            throws ServiceException {

        if (userId == null || userId.isEmpty()) {
            throw new ServiceException("User ID cannot be null or empty", null);
        }
        if (incomeOrExpense == null || incomeOrExpense.isEmpty()) {
            throw new ServiceException("Income or expense cannot be null or empty", null);
        }
        if (amount == null || amount.isEmpty()) {
            throw new ServiceException("Amount cannot be null or empty", null);
        }
        if (time == null) {
            throw new ServiceException("Transaction time cannot be null", null);
        }
        if (product == null || product.isEmpty()) {
            throw new ServiceException("Product cannot be null or empty", null);
        }
        if (type == null || type.isEmpty()) {
            throw new ServiceException("Transaction type cannot be null or empty", null);
        }
        if (accountId == null || accountId.isEmpty()) {
            throw new ServiceException("Account ID cannot be null or empty", null);
        }

        // 创建 TransactionDto
        TransactionDto transaction = new TransactionDto();
        transaction.setId(UUID.randomUUID().toString()); // 设置唯一 ID
        transaction.setUserId(userId);
        transaction.setIncomeOrExpense(incomeOrExpense);
        transaction.setAmount(amount);
        transaction.setTime(time);
        transaction.setProduct(product);
        transaction.setType(type);
        transaction.setAccountId(accountId);
        transaction.setRemark(remark);
        // 其他字段允许为空，设置为 null
        transaction.setCounterparty(null);
        transaction.setPaymentMethod(null);
        transaction.setStatus(null);
        transaction.setMerchantOrderId(null);

        return transactionDao.create(transaction);
    }

    /**
     * 内部类：交易搜索条件
    */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
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
    }
}