package com.se.aiconomy.server.service;

import com.se.aiconomy.server.model.entity.Transaction;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Logger;

public class TransactionService {
    private final Logger logger = Logger.getLogger(TransactionService.class.getName());

    // CSV列名 -> Transaction字段映射
    private static final Map<String, String> FIELD_MAPPING = new HashMap<>();

    static {
        FIELD_MAPPING.put("time", "time");
        FIELD_MAPPING.put("type", "type");
        FIELD_MAPPING.put("incomeorexpense", "incomeOrExpense");
        FIELD_MAPPING.put("amount", "amount");
        FIELD_MAPPING.put("counterparty", "counterparty");
        FIELD_MAPPING.put("product", "product");
        FIELD_MAPPING.put("paymentmethod", "paymentMethod");
        FIELD_MAPPING.put("status", "status");
        FIELD_MAPPING.put("merchantorderid", "merchantOrderId");
        FIELD_MAPPING.put("remark", "remark");
    }

    // 日期时间格式
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private JSONStorageService jsonStorageService;

    public TransactionService(JSONStorageService jsonStorageService) {
        this.jsonStorageService = jsonStorageService;
    }

    /**
     * 执行CSV文件导入
     *
     * @param filePath       CSV文件路径
     * @param failureRecords 输出参数：存储失败记录的原始数据及错误原因
     * @return 成功导入的记录数量
     */
    public int importTransactions(String filePath, List<Map<String, String>> failureRecords)
            throws IOException {
        List<Transaction> successList = new ArrayList<>();
        List<Map<String, String>> csvData = CSVUtil.readCSV(filePath, ',', true);

        for (Map<String, String> row : csvData) {
            try {
                Transaction tx = convertRowToTransaction(row);
                validateTransaction(tx);
                successList.add(tx);
            } catch (DataConversionException e) {
                logFailure(row, e.getMessage(), failureRecords);
            }
        }

        if (!successList.isEmpty()) {
            batchSave(successList);
            logger.info(() -> String.format(
                    "成功导入 %d 条记录，失败 %d 条",
                    successList.size(),
                    failureRecords.size()
            ));
        }
        return successList.size();
    }

    //================ 核心数据转换逻辑 ================//
    private Transaction convertRowToTransaction(Map<String, String> row)
            throws DataConversionException {
        logger.info("转换记录: " + row);
        Transaction tx = new Transaction();

        // 处理映射字段
        for (Map.Entry<String, String> entry : row.entrySet()) {
            String csvHeader = entry.getKey().toLowerCase().trim();
            String value = entry.getValue();

            if (FIELD_MAPPING.containsKey(csvHeader)) {
                String fieldName = FIELD_MAPPING.get(csvHeader);
                setFieldValue(tx, fieldName, value);
            } else {
                tx.addExtraField(csvHeader, value); // 非映射字段存储为额外字段
            }
        }

        validateRequiredFields(tx);
        return tx;
    }

    //================ 必填字段校验 ================//
    private void validateRequiredFields(Transaction tx)
            throws DataConversionException {
        List<String> missingFields = new ArrayList<>();

        // 检查所有必填字段
        if (tx.getTime() == null) missingFields.add("time");
        if (isBlank(tx.getType())) missingFields.add("type");
        if (isBlank(tx.getIncomeOrExpense())) missingFields.add("incomeOrExpense");
        if (isBlank(tx.getAmount())) missingFields.add("amount");

        if (!missingFields.isEmpty()) {
            String errorMsg = "必填字段缺失: " + String.join(", ", missingFields);
            logger.severe(errorMsg);
            throw new DataConversionException(errorMsg);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    //================ 数据校验 ================//
    private void validateTransaction(Transaction tx)
            throws DataConversionException {
        // 校验收支类型值域
        if (!Arrays.asList("income", "expense").contains(tx.getIncomeOrExpense().toLowerCase())) {
            throw new DataConversionException("无效的收支类型: " + tx.getIncomeOrExpense());
        }

        // 验证金额格式（允许字符串但必须是数字）
        try {
            new BigDecimal(tx.getAmount());
        } catch (NumberFormatException e) {
            throw new DataConversionException("金额格式错误: " + tx.getAmount());
        }
    }

    //================ 类型转换 ================//
    private Object convertValue(String fieldName, String value)
            throws DataConversionException {
        if (value == null || value.trim().isEmpty()) return null;

        try {
            switch (fieldName) {
                case "time":
                    return parseDateTime(value.trim());
                case "incomeOrExpense":
                    return value.trim().toLowerCase(); // 统一存储为小写
                case "amount":
                case "type":
                default:
                    return value.trim(); // 其他字段保持原始字符串
            }
        } catch (DateTimeParseException e) {
            throw new DataConversionException("时间格式错误: " + value);
        }
    }

    //================ 工具方法 ================//
    private LocalDateTime parseDateTime(String value)
            throws DateTimeParseException {
        // 兼容带空格的时间格式（如 "2024-01-01 12:00:00"）
        String normalized = value.replace(" ", "T");
        return LocalDateTime.parse(normalized, dateTimeFormatter);
    }

    private void setFieldValue(Transaction tx, String fieldName, String value)
            throws DataConversionException {
        try {
            Object convertedValue = convertValue(fieldName, value);
            Method setter = getSetterMethod(fieldName);
            setter.invoke(tx, convertedValue);
            logger.fine("字段设置成功: " + fieldName + " = " + convertedValue);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new DataConversionException("字段赋值失败: " + fieldName);
        }
    }

    private Method getSetterMethod(String fieldName)
            throws DataConversionException {
        try {
            String methodName = "set" + StringUtils.capitalize(fieldName);
            return Transaction.class.getMethod(methodName, getFieldType(fieldName));
        } catch (NoSuchMethodException e) {
            throw new DataConversionException("无效字段: " + fieldName);
        }
    }

    private Class<?> getFieldType(String fieldName) {
        switch (fieldName) {
            case "time":
                return LocalDateTime.class;
            default:
                return String.class; // 其他字段均为String类型
        }
    }

    //================ 批量保存 ================//
    private void batchSave(List<Transaction> transactions) {
        try {
            // 使用 JSONStorageService 的批量插入方法
            transactions.forEach(tx -> jsonStorageService.upsert(tx));
        } catch (Exception e) {
            logger.severe("数据保存失败: " + e.getMessage());
            throw new ServiceException("数据保存失败", e);
        }
    }

    //================ 失败日志 ================//
    private void logFailure(Map<String, String> row, String reason,
                            List<Map<String, String>> failureRecords) {
        // 记录结构化错误信息
        Map<String, String> failureEntry = new LinkedHashMap<>(row);
        failureEntry.put("error", reason);
        failureRecords.add(failureEntry);

        // 输出可读日志
        String logMsg = String.format(
                "记录导入失败 | 原因: %s | 数据: %s",
                reason,
                String.join(", ", row.values())
        );
        logger.warning(logMsg);
    }

    //================ 异常定义 ================//
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