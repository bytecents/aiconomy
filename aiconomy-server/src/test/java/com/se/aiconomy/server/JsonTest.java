package com.se.aiconomy.server;

import com.se.aiconomy.server.common.utils.JsonUtils;
import com.se.aiconomy.server.model.dto.TransactionDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JSON reading functionality.
 * <p>
 * This class tests the ability to read a list of {@link TransactionDto} objects from a JSON file
 * using the {@link JsonUtils#readJson(String)} utility method. It verifies that the data is correctly
 * deserialized and the fields of the first transaction match the expected values.
 * </p>
 */
public class JsonTest {

    /**
     * Logger instance for logging test information.
     */
    private static final Logger log = LoggerFactory.getLogger(JsonTest.class);

    /**
     * DateTimeFormatter for formatting transaction time.
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Tests reading transactions from a JSON file and validates the deserialized data.
     *
     * @throws IOException if reading the JSON file fails
     */
    @Test
    public void testReadJson() throws IOException {
        List<TransactionDto> transactions = JsonUtils.readJson(
                Objects.requireNonNull(getClass().getClassLoader().getResource("transactions.json")).getPath()
        );

        log.info("Transactions: {}", transactions);

        assertNotNull(transactions, "Transactions list should not be null");
        assertFalse(transactions.isEmpty(), "Transactions list should not be empty");

        TransactionDto transaction = transactions.getFirst();
        assertEquals("12345", transaction.getId(), "TransactionDto ID should be '12345'");
        assertEquals("2025-04-16T10:30:00", transaction.getTime().format(FORMATTER), "TransactionDto time should be '2025-04-16T10:30:00'");
        assertEquals("消费", transaction.getType(), "TransactionDto type should be '消费'");
        assertEquals("Walmart", transaction.getCounterparty(), "TransactionDto counterparty should be 'Walmart'");
        assertEquals("iPhone 15", transaction.getProduct(), "TransactionDto product should be 'iPhone 15'");
        assertEquals("支出", transaction.getIncomeOrExpense(), "TransactionDto income/expense should be '支出'");
        assertEquals("5999.99", transaction.getAmount(), "TransactionDto amount should be '5999.99'");
        assertEquals("微信支付", transaction.getPaymentMethod(), "TransactionDto payment method should be '微信支付'");
        assertEquals("成功", transaction.getStatus(), "TransactionDto status should be '成功'");
        assertEquals("7890", transaction.getMerchantOrderId(), "TransactionDto merchant order ID should be '7890'");
        assertEquals("acc1", transaction.getAccountId(), "TransactionDto account ID should be 'acc1'");
        assertEquals("购买iPhone 15", transaction.getRemark(), "TransactionDto remark should be '购买iPhone 15'");
    }
}
