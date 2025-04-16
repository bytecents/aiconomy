package com.se.aiconomy.server.common.utils;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import java.io.FileReader;
import java.io.IOException;
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
}
