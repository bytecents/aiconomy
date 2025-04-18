package com.se.aiconomy.server.common.utils;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
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
}
