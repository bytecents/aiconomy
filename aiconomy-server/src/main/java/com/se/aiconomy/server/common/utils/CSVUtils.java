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

public class CSVUtils {

    /**
     * 将 CSV 文件转换为 Java Bean 列表
     *
     * @param filePath  CSV 文件路径
     * @param beanClass Java Bean 类
     * @param <T>       Java Bean 类型
     * @return Java Bean 列表
     * @throws IOException 读取文件时可能抛出的异常
     */
    public static <T> List<T> readCsv(String filePath, Class<T> beanClass) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return new CsvToBeanBuilder<T>(reader)
                .withType(beanClass)
                .withSkipLines(1) // 跳过标题行
                .build()
                .parse();
        }
    }

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

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

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
