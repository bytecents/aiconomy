package com.se.aiconomy.common.utils;

import org.apache.commons.csv.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
}
