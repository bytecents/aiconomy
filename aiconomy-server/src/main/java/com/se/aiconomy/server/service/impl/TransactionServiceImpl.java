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
 * Service implementation for handling transaction records and related business logic.
 */
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionDao transactionDao;

    /**
     * Default constructor initializing the TransactionDao instance.
     */
    public TransactionServiceImpl() {
        this.transactionDao = TransactionDao.getInstance();
    }

    /**
     * Classifies a list of TransactionDto objects using the TransactionClassificationService.
     *
     * @param transactions the list of TransactionDto to classify
     * @return a list of maps pairing Transaction and DynamicBillType
     */
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

    /**
     * Extracts and classifies transactions from a CSV file.
     *
     * @param filePath the path to the CSV file
     * @return a list of classified transactions
     * @throws ServiceException if reading the file fails
     */
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

    /**
     * Extracts and classifies transactions from an Excel file.
     *
     * @param filePath the path to the Excel file
     * @return a list of classified transactions
     * @throws ServiceException if reading the file fails
     */
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

    /**
     * @param jsonString the JSON string containing transactions
     * @return a list of classified transactions
     * @throws ServiceException if extraction fails
     */
    @Override
    public List<Map<Transaction, DynamicBillType>> extractTransactionFromJson(String jsonString) throws ServiceException {
        return null;
    }

    /**
     * Saves classified transactions for a user.
     *
     * @param userId                 the user ID
     * @param classifiedTransactions the list of classified transactions
     * @return the saved classified transactions
     * @throws ServiceException if saving fails
     */
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

    /**
     * Saves classified transactions for a user and account.
     *
     * @param userId                 the user ID
     * @param accountId              the account ID
     * @param classifiedTransactions the list of classified transactions
     * @return the saved classified transactions
     * @throws ServiceException if saving fails
     */
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

    /**
     * Retrieves all transactions for a given user ID.
     *
     * @param userId the user ID
     * @return a list of TransactionDto
     * @throws ServiceException if no transactions are found
     */
    @Override
    public List<TransactionDto> getTransactionsByUserId(String userId) throws ServiceException {
        List<TransactionDto> transactionDtos = transactionDao.findByUserId(userId);
        if (transactionDtos == null || transactionDtos.isEmpty()) {
            throw new ServiceException("No transactions found for user ID: " + userId, null);
        }

        return transactionDtos;
    }

    /**
     * Imports transactions from a file (CSV, Excel, or JSON) based on file extension.
     *
     * @param filePath the file path
     * @return the list of imported transactions
     * @throws ServiceException if import fails
     */
    public List<TransactionDto> importTransactions(String filePath) throws ServiceException {
        String fileExtension = getFileExtension(filePath);
        List<TransactionDto> transactions;

        try {
            if ("csv".equalsIgnoreCase(fileExtension)) {
                transactions = readCSV(filePath);
            } else if ("xlsx".equalsIgnoreCase(fileExtension) || "xls".equalsIgnoreCase(fileExtension)) {
                transactions = readExcel(filePath);
            } else if ("json".equalsIgnoreCase(fileExtension)) {
                transactions = readJson(filePath);
            } else {
                throw new ServiceException("Unsupported file type: " + fileExtension, null);
            }

            return processImportedTransactions(transactions);

        } catch (IOException e) {
            throw new ServiceException("Failed to read file: " + filePath, e);
        } catch (Exception e) {
            throw new ServiceException("Unexpected error during import", e);
        }
    }

    /**
     * Reads transactions from a CSV file.
     *
     * @param filePath the file path
     * @return the list of TransactionDto
     * @throws IOException if reading fails
     */
    private List<TransactionDto> readCSV(String filePath) throws IOException {
        return CSVUtils.readCsv(filePath, TransactionDto.class);
    }

    /**
     * Reads transactions from an Excel file.
     *
     * @param filePath the file path
     * @return the list of TransactionDto
     * @throws IOException      if reading fails
     * @throws ServiceException if reading fails
     */
    private List<TransactionDto> readExcel(String filePath) throws IOException, ServiceException {
        return ExcelUtils.readExcel(filePath, TransactionDto.class);
    }

    /**
     * Reads transactions from a JSON file.
     *
     * @param filePath the file path
     * @return the list of TransactionDto
     * @throws IOException if reading fails
     */
    private List<TransactionDto> readJson(String filePath) throws IOException {
        return JsonUtils.readJson(filePath);
    }

    /**
     * Exports all transactions to a JSON file.
     *
     * @param filePath the file path to export to
     * @throws ServiceException if export fails
     */
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

    /**
     * Exports all transactions to a CSV file.
     *
     * @param filePath the file path to export to
     * @throws ServiceException if export fails
     */
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

    /**
     * Exports all transactions to an Excel file.
     *
     * @param filePath the file path to export to
     * @throws ServiceException if export fails
     */
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
     * Processes imported transactions, generating IDs and timestamps if missing, and saves them.
     *
     * @param transactions the list of imported transactions
     * @return the list of valid transactions
     * @throws ServiceException if saving fails
     */
    private List<TransactionDto> processImportedTransactions(List<TransactionDto> transactions) throws ServiceException {
        List<TransactionDto> validTransactions = new ArrayList<>();

        for (TransactionDto transaction : transactions) {
            if (transaction.getId() == null || transaction.getId().isEmpty()) {
                transaction.setId(UUID.randomUUID().toString());
            }

            if (transaction.getTime() == null) {
                transaction.setTime(LocalDateTime.now());
            }

            validTransactions.add(transaction);
            log.debug("Valid transaction processed: {}", transaction);
        }

        if (!validTransactions.isEmpty()) {
            try {
                batchSave(validTransactions);
                log.info("Successfully imported {} transactions", validTransactions.size());
            } catch (ServiceException e) {
                log.error("Failed to save transactions: {}", e.getMessage(), e);
                throw e;
            }
        } else {
            log.warn("No valid transactions found to import");
        }

        return validTransactions;
    }

    /**
     * Batch saves a list of transactions.
     *
     * @param transactions the list of transactions to save
     * @throws ServiceException if saving fails
     */
    private void batchSave(List<TransactionDto> transactions) throws ServiceException {
        try {
            for (TransactionDto transaction : transactions) {
                transactionDao.upsert(transaction);
            }
        } catch (Exception e) {
            log.error("Failed to save transactions: {}", e.getMessage(), e);
            throw new ServiceException("Data save failed", e);
        }
    }

    /**
     * Retrieves transactions within a specified date range.
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return the list of transactions in the date range
     */
    public List<TransactionDto> getTransactionsByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return transactionDao.findByTimeRange(startTime, endTime);
    }

    /**
     * Gets income and expense statistics within a specified date range.
     *
     * @param startTime the start time
     * @param endTime   the end time
     * @return a map containing totalIncome, totalExpense, and netAmount
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
     * Gets transaction statistics grouped by payment method.
     *
     * @return a map of payment method to total amount
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
     * Gets transaction statistics grouped by counterparty.
     *
     * @return a map of counterparty to total amount
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
     * Updates the status of a transaction.
     *
     * @param transactionId the transaction ID
     * @param newStatus     the new status
     * @return the updated TransactionDto
     * @throws ServiceException if the transaction is not found
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
     * Searches for transactions matching the given criteria.
     *
     * @param criteria the search criteria
     * @return a list of matching transactions
     */
    @Override
    public List<TransactionDto> searchTransactions(TransactionSearchCriteria criteria) {
        List<TransactionDto> allTransactions = transactionDao.findAll();

        return allTransactions.stream()
                .filter(criteria::matches)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a transaction by its ID.
     *
     * @param transactionId the transaction ID
     * @throws ServiceException if the transaction is not found
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
     * Retrieves all transactions associated with a specific accountId and userId.
     *
     * @param accountId the account ID
     * @param userId    the user ID
     * @return a list of TransactionDto for the account and user
     * @throws ServiceException if no transactions are found
     */
    public List<TransactionDto> getTransactionsByAccountId(String accountId, String userId) throws ServiceException {

        List<TransactionDto> allTransactions = transactionDao.findAll();

        List<TransactionDto> filteredTransactions = allTransactions.stream()
                .filter(transaction -> accountId.equals(transaction.getAccountId()) && userId.equals(transaction.getUserId()))
                .collect(Collectors.toList());

        if (filteredTransactions.isEmpty()) {
            throw new ServiceException("No transactions found for accountId: " + accountId + " and userId: " + userId, null);
        }

        return filteredTransactions;
    }

    /**
     * Adds a transaction manually.
     *
     * @param userId          the user ID
     * @param incomeOrExpense income or expense type
     * @param amount          the transaction amount
     * @param time            the transaction time
     * @param product         the product
     * @param type            the transaction type
     * @param accountId       the account ID
     * @param remark          remarks
     * @return the created TransactionDto
     * @throws ServiceException if required fields are missing
     */
    @Override
    public TransactionDto addTransactionManually(String userId, String incomeOrExpense, String amount,
                                                 LocalDateTime time, String product, String type, String accountId, String remark)
            throws ServiceException {
        if (userId == null || incomeOrExpense == null || amount == null || type == null || accountId == null) {
            throw new ServiceException("Missing required fields for transaction", null);
        }

        TransactionDto transaction = new TransactionDto();
        transaction.setUserId(userId);
        transaction.setIncomeOrExpense(incomeOrExpense);
        transaction.setAmount(amount);
        transaction.setTime(time != null ? time : LocalDateTime.now());
        transaction.setProduct(product);
        transaction.setType(type);
        transaction.setAccountId(accountId);
        transaction.setBillType(DynamicBillType.fromString(type));
        transaction.setRemark(remark);

        return transactionDao.create(transaction);
    }

    /**
     * Inner class representing search criteria for transactions.
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

        /**
         * Checks if a transaction matches the search criteria.
         *
         * @param transaction the transaction to check
         * @return true if the transaction matches, false otherwise
         */
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