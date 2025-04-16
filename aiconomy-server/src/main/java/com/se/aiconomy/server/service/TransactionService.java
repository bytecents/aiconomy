package com.se.aiconomy.server.service;

import com.se.aiconomy.server.dao.TransactionDao;
import com.se.aiconomy.server.model.entity.Transaction;
import com.se.aiconomy.server.common.utils.CSVUtil;
import org.apache.commons.lang3.StringUtils;

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
        FIELD_MAPPING.put("time", "time");
        FIELD_MAPPING.put("type", "type");
        FIELD_MAPPING.put("counterparty", "counterparty");
        FIELD_MAPPING.put("product", "product");
        FIELD_MAPPING.put("incomeorexpense", "incomeOrExpense"); // CSV列名统一小写
        FIELD_MAPPING.put("amount", "amount");
        FIELD_MAPPING.put("paymentmethod", "paymentMethod");
        FIELD_MAPPING.put("status", "status");
        FIELD_MAPPING.put("merchantorderid", "merchantOrderId");
        FIELD_MAPPING.put("remark", "remark");
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

//  数据转换
private Transaction convertRowToTransaction(Map<String, String> row) throws DataConversionException {
    logger.info("正在转换记录: " + row);
    Transaction tx = new Transaction();

    for (Map.Entry<String, String> entry : row.entrySet()) {
        String csvHeader = entry.getKey();
        String value = entry.getValue();

        // 统一转换为小写匹配映射键
        String normalizedHeader = csvHeader.toLowerCase().trim();
        String fieldName = FIELD_MAPPING.get(normalizedHeader);

        if (fieldName != null) {
            try {
                setFieldValue(tx, fieldName, value);
            } catch (DataConversionException e) {
                logger.warning("字段转换失败: " + fieldName + " -> " + value);
                throw e;
            }
        } else {
            tx.addExtraField(csvHeader, value);
        }
    }

    validateRequiredFields(tx);
    return tx;
}

    // 修复2：增强 getSetterMethod 方法
    private Method getSetterMethod(String fieldName) throws DataConversionException {
        try {
            // 空值检查
            if (fieldName == null) {
                throw new DataConversionException("字段名不能为null");
            }

            String actualFieldName = FIELD_MAPPING.get(fieldName.toLowerCase());
            if (actualFieldName == null) {
                throw new DataConversionException("无效的字段映射: " + fieldName);
            }

            String methodName = "set" + StringUtils.capitalize(actualFieldName);
            return Transaction.class.getMethod(methodName, getFieldType(actualFieldName));
        } catch (NoSuchMethodException e) {
            throw new DataConversionException("找不到Setter方法: " + fieldName);
        }
    }


//    类型转换
private Object convertValue(String fieldName, String value) throws DataConversionException {
    if (value == null || value.trim().isEmpty()) return null;

    try {
        switch (fieldName) {
            case "time":
                return parseDateTime(value);
            case "amount":
                return value;  // 直接返回字符串
            case "incomeOrExpense":
            case "type":
            case "status":
                return value.trim().toLowerCase();
            default:
                return value;
        }
    } catch (DateTimeParseException e) {
        throw new DataConversionException("时间格式错误: " + value);
    }
}

//    必要字段验证
//    private void validateRequiredFields(Transaction tx)
//            throws DataConversionException
//    {
//        if (tx.getTime() == null) {
//            throw new DataConversionException("Time cannot be empty");
//        }
//        if (tx.getAmount() == null) {
//            throw new DataConversionException("Amount cannot be empty");
//        }
//    }
private void validateRequiredFields(Transaction tx) throws DataConversionException {
    if (tx.getTime() == null) {
        logger.severe("验证失败: 交易时间为空");
        throw new DataConversionException("交易时间不能为空");
    }
    if (tx.getAmount() == null) {
        logger.severe("验证失败: 金额为空");
        throw new DataConversionException("金额不能为空");
    }
    if (tx.getType() == null) { // 假设这是必填字段
        logger.severe("验证失败: 交易类型为空");
        throw new DataConversionException("交易类型不能为空");
    }
}

    private void validateTransaction(Transaction tx) throws DataConversionException {
        // 验证收支类型
        if (!Arrays.asList("income", "expense").contains(tx.getIncomeOrExpense())) {
            throw new DataConversionException("无效的收支类型: " + tx.getIncomeOrExpense());
        }

        // 验证金额是否为有效数字
        try {
            new BigDecimal(tx.getAmount());  // 检查是否为合法数字
        } catch (NumberFormatException e) {
            throw new DataConversionException("金额格式错误: " + tx.getAmount());
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
        if (fieldName == null) {
            throw new IllegalArgumentException("字段名不能为null");
        }

        switch (fieldName) {
            case "time":         return LocalDateTime.class;
            case "amount":       return String.class;
            case "incomeOrExpense":
            case "type":
            case "status":       return String.class;
            default:            return String.class;
        }
    }

    // 修改8：在关键位置添加日志
    private void setFieldValue(Transaction tx, String fieldName, String value) throws DataConversionException {
        try {
            Object convertedValue = convertValue(fieldName, value);
            Method setter = getSetterMethod(fieldName);
            setter.invoke(tx, convertedValue);

            logger.fine(String.format("字段设置成功: %s = %s", fieldName, convertedValue));
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.severe(String.format("字段设置失败: %s -> %s (%s)", fieldName, value, e.getMessage()));
            throw new DataConversionException("字段赋值失败: " + fieldName);
        }
    }

    // 修复3：增强时间解析逻辑（支持带空格和T的格式）
    private LocalDateTime parseDateTime(String value) throws DateTimeParseException {
        String normalizedValue = value.replace(" ", "T"); // 处理 "2024-01-01 12:00:00" 格式
        return LocalDateTime.parse(normalizedValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
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