package com.se.aiconomy.server.common.utils;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class CSVUtil {
    public static <T> List<T> parseCsv(Path filePath, Class<T> clazz,
                                       Map<String, String> columnMap) {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            // 创建映射策略
            HeaderColumnNameTranslateMappingStrategy<T> strategy =
                    new HeaderColumnNameTranslateMappingStrategy<>();
            strategy.setType(clazz);
            strategy.setColumnMapping(columnMap);

            // 配置CSV解析器
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        } catch (Exception e) {
            throw new RuntimeException("CSV解析失败: " + e.getMessage(), e);
        }
    }
}
