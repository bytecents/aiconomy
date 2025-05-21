package com.se.aiconomy.server.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.se.aiconomy.server.model.dto.TransactionDto;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    static {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));
        mapper.registerModule(javaTimeModule);
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE);
    }

    public static List<TransactionDto> readJson(String filePath) throws IOException {
        File file = new File(filePath);
        try {
            List<TransactionDto> transactions = mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, TransactionDto.class));
            System.out.println("Successfully read " + transactions.size() + " transactions from JSON file: " + filePath);
            transactions.forEach(tx -> System.out.println("Transaction time: " + tx.getTime().format(FORMATTER)));
            return transactions;
        } catch (IOException e) {
            System.err.println("Failed to read JSON file: " + filePath);
            throw e;
        }
    }
}