package com.se.aiconomy.server.common.utils;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.se.aiconomy.server.model.dto.TransactionDto;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading from and writing to CSV files.
 */
public class CSVUtils {

    /**
     * The date-time formatter used for formatting the time field in the CSV.
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Reads a CSV file and converts its rows into a list of Java beans.
     *
     * @param filePath  the path to the CSV file
     * @param beanClass the class of the Java bean
     * @param <T>       the type of the Java bean
     * @return a list of Java beans parsed from the CSV file
     * @throws IOException if an I/O error occurs while reading the file
     */
    public static <T> List<T> readCsv(String filePath, Class<T> beanClass) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return new CsvToBeanBuilder<T>(reader)
                    .withType(beanClass)
                    .withSkipLines(1) // Skip the header row
                    .build()
                    .parse();
        }
    }

    /**
     * Reads a CSV file and converts its rows into a list of Java beans using a column mapping.
     *
     * @param filePath      the path to the CSV file
     * @param beanClass     the class of the Java bean
     * @param columnMapping a map from CSV column names to bean property names
     * @param <T>           the type of the Java bean
     * @return a list of Java beans parsed from the CSV file
     * @throws IOException if an I/O error occurs while reading the file
     */
    public static <T> List<T> readCsvWithMapping(String filePath, Class<T> beanClass, Map<String, String> columnMapping) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            HeaderColumnNameTranslateMappingStrategy<T> strategy = new HeaderColumnNameTranslateMappingStrategy<>();
            strategy.setType(beanClass);
            strategy.setColumnMapping(columnMapping);

            return new CsvToBeanBuilder<T>(reader)
                    .withMappingStrategy(strategy)
                    .withSkipLines(1)
                    .build()
                    .parse();
        }
    }

    /**
     * Writes a list of TransactionDto objects to a CSV file.
     *
     * @param filePath     the path to the output CSV file
     * @param transactions the list of transactions to write
     * @throws IOException if an I/O error occurs while writing the file
     */
    public static void writeCsv(String filePath, List<TransactionDto> transactions) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath), ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            // Write header
            String[] header = {
                    "id", "time", "type", "counterparty", "product", "incomeOrExpense", "amount",
                    "paymentMethod", "status", "merchantOrderId", "accountId", "userId", "remark"
            };
            writer.writeNext(header);

            // Write transaction data
            for (TransactionDto tx : transactions) {
                List<String> row = new ArrayList<>();
                row.add(tx.getId());
                row.add(tx.getTime() != null ? tx.getTime().format(FORMATTER) : "");
                row.add(tx.getType());
                row.add(tx.getCounterparty());
                row.add(tx.getProduct());
                row.add(tx.getIncomeOrExpense());
                row.add(tx.getAmount());
                row.add(tx.getPaymentMethod());
                row.add(tx.getStatus());
                row.add(tx.getMerchantOrderId());
                row.add(tx.getAccountId());
                row.add(tx.getUserId());
                row.add(tx.getRemark());
                writer.writeNext(row.toArray(new String[0]));
            }

            System.out.println("Successfully wrote " + transactions.size() + " transactions to CSV file: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to write CSV file: " + filePath);
            throw e;
        }
    }
}