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

/**
 * Utility class for reading from and writing to Excel files.
 */
public class ExcelUtils {

    /**
     * The date-time formatter used for formatting the time field in the Excel file.
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Reads an Excel file and converts its rows into a list of Java beans.
     *
     * @param filePath  the path to the Excel file
     * @param beanClass the class of the Java bean
     * @param <T>       the type of the Java bean
     * @return a list of Java beans parsed from the Excel file
     * @throws IOException if an I/O error occurs while reading the file
     */
    public static <T> List<T> readExcel(String filePath, Class<T> beanClass) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);  // Read the first sheet
            Iterator<Row> rowIterator = sheet.iterator();
            List<T> resultList = new ArrayList<>();

            // Get header row
            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            // Iterate over data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                T beanInstance = createBeanFromRow(row, beanClass, headers);
                resultList.add(beanInstance);
            }

            return resultList;
        }
    }

    /**
     * Creates a Java bean instance from a row in the Excel table.
     *
     * @param row       the current row
     * @param beanClass the type of the Java bean
     * @param headers   the list of header column names
     * @param <T>       the type of the Java bean
     * @return a Java bean instance
     * @throws IOException if an error occurs during bean creation
     */
    private static <T> T createBeanFromRow(Row row, Class<T> beanClass, List<String> headers) throws IOException {
        try {
            T beanInstance = beanClass.getDeclaredConstructor().newInstance();

            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                Cell cell = row.getCell(i);
                String header = headers.get(i).trim();

                // Get the field of the bean
                Field field;
                try {
                    field = beanClass.getDeclaredField(header);
                } catch (NoSuchFieldException e) {
                    continue;  // Skip if the field does not exist
                }

                field.setAccessible(true);

                // Set the field value according to the cell type
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case STRING:
                            if (field.getType() == LocalDateTime.class) {
                                // If the field is LocalDateTime, parse the date string
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
                                // If the cell is a date, convert to LocalDateTime
                                LocalDateTime dateTime = cell.getDateCellValue().toInstant()
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDateTime();
                                field.set(beanInstance, dateTime);
                            } else {
                                // If the cell is numeric, set directly
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
     * Reads an Excel file and converts its rows into a list of Java beans using a column mapping.
     *
     * @param filePath      the path to the Excel file
     * @param beanClass     the class of the Java bean
     * @param columnMapping a map from Excel column names to bean property names
     * @param <T>           the type of the Java bean
     * @return a list of Java beans parsed from the Excel file
     * @throws IOException if an I/O error occurs while reading the file
     */
    public static <T> List<T> readExcelWithMapping(String filePath, Class<T> beanClass, Map<String, String> columnMapping) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);  // Read the first sheet
            Iterator<Row> rowIterator = sheet.iterator();
            List<T> resultList = new ArrayList<>();

            // Get header row
            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            // Iterate over data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                T beanInstance = createBeanFromRowWithMapping(row, beanClass, headers, columnMapping);
                resultList.add(beanInstance);
            }

            return resultList;
        }
    }

    /**
     * Creates a Java bean instance from a row in the Excel table using column mapping.
     *
     * @param row           the current row
     * @param beanClass     the type of the Java bean
     * @param headers       the list of header column names
     * @param columnMapping the column mapping (Excel column name -> Java field name)
     * @param <T>           the type of the Java bean
     * @return a Java bean instance
     * @throws IOException if an error occurs during bean creation
     */
    private static <T> T createBeanFromRowWithMapping(Row row, Class<T> beanClass, List<String> headers, Map<String, String> columnMapping) throws IOException {
        try {
            T beanInstance = beanClass.getDeclaredConstructor().newInstance();
            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                Cell cell = row.getCell(i);
                String header = headers.get(i).trim();

                // If column mapping exists, get the target field name from mapping
                String fieldName = columnMapping.getOrDefault(header, header);

                // Get the field of the bean
                Field field;
                try {
                    field = beanClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    continue;
                }

                field.setAccessible(true);

                // Set the field value according to the cell type
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

    /**
     * Writes a list of TransactionDto objects to an Excel file.
     *
     * @param filePath     the path to the output Excel file
     * @param transactions the list of transactions to write
     * @throws IOException if an I/O error occurs while writing the file
     */
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
                row.createCell(11).setCellValue(tx.getUserId());
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