package com.se.aiconomy.server.common.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CSVUtil {
    /**
     * Read a CSV file and return a List<Map<String, String>>, supports unknown headers
     *
     * @param filePath  CSV file path
     * @param delimiter CSV delimiter (e.g., `,`, `;`, `\t`)
     * @param hasHeader Whether the CSV file has a header
     * @return Parsed data list
     */
    public static List<Map<String, String>> readCSV(String filePath, char delimiter, boolean hasHeader) throws IOException {
        List<Map<String, String>> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            CSVFormat format = hasHeader ?
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withDelimiter(delimiter).withTrim() :
                    CSVFormat.DEFAULT.withDelimiter(delimiter).withTrim();

            try (CSVParser csvParser = new CSVParser(reader, format)) {
                List<String> headers = new ArrayList<>();

                // If there is no header, generate default column names (Column1, Column2, ...)
                if (!hasHeader) {
                    int columnCount = csvParser.getRecords().getFirst().size(); // Get the number of columns
                    for (int i = 0; i < columnCount; i++) {
                        headers.add("Column" + (i + 1));
                    }
                } else {
                    headers.addAll(csvParser.getHeaderNames()); // Get CSV headers
                }

                // Parse CSV data
                for (CSVRecord record : csvParser) {
                    Map<String, String> row = new LinkedHashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        row.put(headers.get(i), record.get(i));
                    }
                    result.add(row);
                }
            }
        }
        return result;
    }

    //    测试用
    public static void writeTestCSV(String filePath, List<Map<String, String>> data) throws IOException {
        if (data.isEmpty()) return;

        // 获取表头
        String[] headers = data.get(0).keySet().toArray(new String[0]);

        // 写入CSV文件
        Files.write(Paths.get(filePath), (
                String.join(",", headers) + "\n" + // 表头行
                        data.stream()
                                .map(row -> String.join(",", row.values()))
                                .reduce("", (a, b) -> a + b + "\n")
        ).getBytes());
    }
}
