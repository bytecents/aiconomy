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

/**
 * Utility class for JSON serialization and deserialization of {@link TransactionDto} objects.
 * Provides methods to read and write lists of transactions from/to JSON files,
 * with support for Java 8 date and time types.
 */
public class JsonUtils {
    /**
     * The shared {@link ObjectMapper} instance configured for Java 8 date/time support.
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * The {@link DateTimeFormatter} used for serializing and deserializing {@link LocalDateTime} fields.
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    static {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));
        mapper.registerModule(javaTimeModule);
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE);
    }

    /**
     * Reads a list of {@link TransactionDto} objects from a JSON file.
     *
     * @param filePath the path to the JSON file
     * @return a list of {@link TransactionDto} objects parsed from the file
     * @throws IOException if an I/O error occurs while reading the file
     */
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

    /**
     * Writes a list of {@link TransactionDto} objects to a JSON file.
     *
     * @param filePath     the path to the output JSON file
     * @param transactions the list of transactions to write
     * @throws IOException if an I/O error occurs while writing the file
     */
    public static void writeJson(String filePath, List<TransactionDto> transactions) throws IOException {
        File file = new File(filePath);
        try {
            mapper.writeValue(file, transactions);
            System.out.println("Successfully wrote " + transactions.size() + " transactions to JSON file: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to write JSON file: " + filePath);
            throw e;
        }
    }
}