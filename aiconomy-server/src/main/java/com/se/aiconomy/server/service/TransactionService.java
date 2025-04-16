package com.se.aiconomy.server.service;

import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.model.entity.Transaction;
import com.se.aiconomy.server.common.utils.CSVUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionService {
    private Logger logger = Logger.getLogger(TransactionService.class.getName());

    // 映射 CSV列名 -> Transaction字段名
    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();
    static {
        FIELD_MAPPING.put("Time", "time");
        FIELD_MAPPING.put("Type", "type");
        FIELD_MAPPING.put("Counterparty", "counterparty");
        FIELD_MAPPING.put("Product", "product");
        FIELD_MAPPING.put("IncomeOrExpense", "incomeOrExpense");
        FIELD_MAPPING.put("Amount", "amount");
        FIELD_MAPPING.put("PaymentMethod", "paymentMethod");
        FIELD_MAPPING.put("Status", "status");
        FIELD_MAPPING.put("MerchantOrderId", "merchantOrderId");
        FIELD_MAPPING.put("Remark", "remark");
    }

    // 日期时间格式
    private DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private TransactionDao transactionDao;

    public TransactionService(){}

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    /**
     * 执行CSV文件导入
     * @param filePath CSV文件路径
     * @param failureRecords 输出参数：存储失败记录的原始数据
     * @return 成功导入的记录数量
     * @throws IOException 文件读取异常
     */
    public int importTransactions(String filePath, List<Map<String, String>> failureRecords)
            throws IOException
    {
        List<Transaction> successList = new ArrayList<>();
        List<Map<String, String>> csvData = CSVUtil.readCSV(filePath, ',', true);

        for (Map<String, String> row : csvData) {
            try {
                Transaction tx = convertRowToTransaction(row);
                validateTransaction(tx);
                successList.add(tx);
            } catch (DataConversionException e) {
                logFailure(row, e.getMessage());
                failureRecords.add(row);
            }
        }

        if (!successList.isEmpty()) {
            batchSave(successList);
            logger.info(() -> String.format(
                    "Import %d records successfully，%d records failed.",
                    successList.size(),
                    failureRecords.size()
            ));
        }
        return successList.size();
    }

//   反射字段映射
    private void setFieldValue(Transaction tx, String fieldName, String value)
            throws DataConversionException
    {
        try {
            Method setter = getSetterMethod(fieldName);
            Object convertedValue = convertValue(fieldName, value);
            setter.invoke(tx, convertedValue);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new DataConversionException("Field assignment failed: " + fieldName);
        }
    }

//  数据转换
    private Transaction convertRowToTransaction(Map<String, String> row)
            throws DataConversionException
    {
        Transaction tx = new Transaction();

        for (Map.Entry<String, String> entry : row.entrySet()) {
            String csvHeader = entry.getKey();
            String value = entry.getValue();

            String fieldName = FIELD_MAPPING.get(csvHeader);
            if (fieldName != null) {
                try {
                    setFieldValue(tx, fieldName, value);
                } catch (DataConversionException e) {
                    e.printStackTrace();
                }
            } else {
                tx.addExtraField(csvHeader, value);
            }
        }

        // 校验必要字段（如交易时间、金额）
        validateRequiredFields(tx);
        return tx;
    }

//    获取字段setter方法
    private Method getSetterMethod(String fieldName) throws DataConversionException {
        String methodName = "set" + capitalize(fieldName);
        try {
            return Transaction.class.getMethod(methodName, getFieldType(fieldName));
        } catch (NoSuchMethodException e) {
            throw new DataConversionException("Invalid field mapping: " + fieldName);
        }
    }

//    类型转换
    private Object convertValue(String fieldName, String value)
            throws DataConversionException
    {
        if (value == null || value.isEmpty()) return null;

        try {
            switch (fieldName) {
                case "Time":
                    return parseDateTime(value);
                case "Amount":
                    return validateAmount(value);
                default:
                    return value;
            }
        } catch (DateTimeParseException e) {
            throw new DataConversionException("Time format error: " + value);
        } catch (NumberFormatException e) {
            throw new DataConversionException("Amount format error: " + value);
        }
    }

//    必要字段验证
    private void validateRequiredFields(Transaction tx)
            throws DataConversionException
    {
        if (tx.getTime() == null) {
            throw new DataConversionException("Time cannot be empty");
        }
        if (tx.getAmount() == null) {
            throw new DataConversionException("Amount cannot be empty");
        }
    }


    private void validateTransaction(Transaction tx)
            throws DataConversionException
    {
        // 验证收支类型
        if (!Arrays.asList("Income", "Expese").contains(tx.getIncomeOrExpense())) {
            throw new DataConversionException("Invalid income and expense type: " + tx.getIncomeOrExpense());
        }

        // 验证金额有效性
        try {
            new BigDecimal(tx.getAmount());
        } catch (NumberFormatException e) {
            throw new DataConversionException("Incorrect amount format: " + tx.getAmount());
        }
    }

//  批量保存
    private void batchSave(List<Transaction> transactions) {
        try {
            transactionDao.batchSave(transactions);
        } catch (Exception e) {
            logger.severe(() -> String.format(
                    "Data saving failed",
                    e.getMessage(),
                    transactions.size()
            ));
            throw new ServiceException("Data saving failed", e);
        }
    }

//日志记录
    private void logFailure(Map<String, String> row, String reason) {
        String logMessage = String.format(
                "Records import failed",
                reason,
                String.join(", ", row.values())
        );
        logger.log(Level.WARNING, logMessage);
    }

//    大写
    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private Class<?> getFieldType(String fieldName) {
        switch (fieldName) {
            case "Time": return LocalDateTime.class;
            default:     return String.class;
        }
    }

    private LocalDateTime parseDateTime(String value) throws DateTimeParseException {
        return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
    }

    private String validateAmount(String value) throws NumberFormatException {
        return new BigDecimal(value).toString();
    }

    public static class DataConversionException extends Exception {
        public DataConversionException(String message) {
            super(message);
        }
    }

    public static class ServiceException extends RuntimeException {
        public ServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}