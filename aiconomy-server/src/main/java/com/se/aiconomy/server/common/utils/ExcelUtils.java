package com.se.aiconomy.server.common.utils;


import com.se.aiconomy.server.model.dto.TransactionDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelUtils {

    /**
     * 将 Excel 文件转换为 Java Bean 列表
     *
     * @param filePath  Excel 文件路径
     * @param beanClass Java Bean 类
     * @param <T>       Java Bean 类型
     * @return Java Bean 列表
     * @throws IOException 读取文件时可能抛出的异常
     */
    public static <T> List<T> readExcel(String filePath, Class<T> beanClass) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);  // 读取第一个Sheet
            Iterator<Row> rowIterator = sheet.iterator();
            List<T> resultList = new ArrayList<>();

            // 获取标题行
            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            // 遍历数据行
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                T beanInstance = createBeanFromRow(row, beanClass, headers);
                resultList.add(beanInstance);
            }

            return resultList;
        }
    }

    /**
     * 根据 Excel 表格中的行数据创建 Java Bean 实例
     *
     * @param row       当前行
     * @param beanClass Java Bean 类型
     * @param headers   表头列名列表
     * @param <T>       Java Bean 类型
     * @return Java Bean 实例
     * @throws IOException
     */
    private static <T> T createBeanFromRow(Row row, Class<T> beanClass, List<String> headers) throws IOException {
        try {
            T beanInstance = beanClass.getDeclaredConstructor().newInstance();

            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                Cell cell = row.getCell(i);
                String header = headers.get(i).trim();

                // 获取 Bean 的字段
                Field field;
                try {
                    field = beanClass.getDeclaredField(header);
                } catch (NoSuchFieldException e) {
                    continue;  // 如果没有这个字段，跳过
                }

                field.setAccessible(true);

                // 根据单元格类型设置字段值
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case STRING:
                            if (field.getType() == LocalDateTime.class) {
                                // 如果字段是 LocalDateTime 类型，解析日期字符串
                                String dateString = cell.getStringCellValue();
                                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                                LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
                                field.set(beanInstance, dateTime);
                            } else {
                                field.set(beanInstance, cell.getStringCellValue());
                            }
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                // 如果是日期格式的单元格，转换为 LocalDateTime
                                LocalDateTime dateTime = cell.getDateCellValue().toInstant()
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDateTime();
                                field.set(beanInstance, dateTime);
                            } else {
                                // 如果是数字类型，直接设置
                                if (field.getType() == Double.class) {
                                    field.set(beanInstance, cell.getNumericCellValue());
                                } else {
                                    field.set(beanInstance, cell.getNumericCellValue());
                                }
                            }
                            break;
                        case BOOLEAN:
                            field.set(beanInstance, cell.getBooleanCellValue());
                            break;
                        default:
                            break;
                    }
                }
            }
            return beanInstance;
        } catch (Exception e) {
            throw new IOException("Error creating bean from Excel row", e);
        }
    }

    /**
     * 将 Excel 文件转化为 Java Bean 列表，支持列映射
     *
     * @param filePath      Excel 文件路径
     * @param beanClass     Java Bean 类
     * @param columnMapping 映射关系 (Excel列名 -> Java字段名)
     * @param <T>           Java Bean 类型
     * @return Java Bean 列表
     * @throws IOException 读取文件时可能抛出的异常
     */
    public static <T> List<T> readExcelWithMapping(String filePath, Class<T> beanClass, Map<String, String> columnMapping) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);  // 读取第一个Sheet
            Iterator<Row> rowIterator = sheet.iterator();
            List<T> resultList = new ArrayList<>();

            // 获取标题行
            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            // 遍历数据行
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                T beanInstance = createBeanFromRowWithMapping(row, beanClass, headers, columnMapping);
                resultList.add(beanInstance);
            }

            return resultList;
        }
    }

    /**
     * 根据 Excel 表格中的行数据和列映射创建 Java Bean 实例
     *
     * @param row           当前行
     * @param beanClass     Java Bean 类型
     * @param headers       表头列名列表
     * @param columnMapping 列映射关系 (Excel列名 -> Java字段名)
     * @param <T>           Java Bean 类型
     * @return Java Bean 实例
     * @throws IOException
     */
    private static <T> T createBeanFromRowWithMapping(Row row, Class<T> beanClass, List<String> headers, Map<String, String> columnMapping) throws IOException {
        try {
            T beanInstance = beanClass.getDeclaredConstructor().newInstance();
            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                Cell cell = row.getCell(i);
                String header = headers.get(i).trim();

                // 如果列映射存在，则根据映射获取目标字段名
                String fieldName = columnMapping.getOrDefault(header, header);

                // 获取 Bean 的字段
                Field field;
                try {
                    field = beanClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    continue;  // 如果没有这个字段，跳过
                }

                field.setAccessible(true);

                // 根据单元格类型设置字段值
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case STRING:
                            field.set(beanInstance, cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                field.set(beanInstance, cell.getDateCellValue());
                            } else {
                                field.set(beanInstance, cell.getNumericCellValue());
                            }
                            break;
                        case BOOLEAN:
                            field.set(beanInstance, cell.getBooleanCellValue());
                            break;
                        default:
                            break;
                    }
                }
            }
            return beanInstance;
        } catch (Exception e) {
            throw new IOException("Error creating bean from Excel row with mapping", e);
        }
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static void writeExcel(String filePath, List<TransactionDto> transactions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            // Write header
            String[] headers = {
                    "id", "time", "type", "counterparty", "product", "incomeOrExpense", "amount",
                    "paymentMethod", "status", "merchantOrderId", "accountId", "userId", "remark"
            };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Write transaction data
            int rowNum = 1;
            for (TransactionDto tx : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(tx.getId());
                row.createCell(1).setCellValue(tx.getTime() != null ? tx.getTime().format(FORMATTER) : "");
                row.createCell(2).setCellValue(tx.getType());
                row.createCell(3).setCellValue(tx.getCounterparty());
                row.createCell(4).setCellValue(tx.getProduct());
                row.createCell(5).setCellValue(tx.getIncomeOrExpense());
                row.createCell(6).setCellValue(tx.getAmount());
                row.createCell(7).setCellValue(tx.getPaymentMethod());
                row.createCell(8).setCellValue(tx.getStatus());
                row.createCell(9).setCellValue(tx.getMerchantOrderId());
                row.createCell(10).setCellValue(tx.getAccountId());
                row.createCell(12).setCellValue(tx.getRemark());
            }
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            System.out.println("Successfully wrote " + transactions.size() + " transactions to Excel file: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to write Excel file: " + filePath);
            throw e;
        }
    }
}
