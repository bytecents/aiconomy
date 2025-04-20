package com.se.aiconomy.server;

import com.se.aiconomy.server.common.utils.CSVUtils;
import com.se.aiconomy.server.model.dto.TransactionDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class CSVTest {

    private static final Logger log = LoggerFactory.getLogger(CSVTest.class);

    @Test
    public void testReadCsv() throws IOException {
        List<TransactionDto> transactions = CSVUtils.readCsv(
            Objects.requireNonNull(getClass().getClassLoader().getResource("transactions.csv")).getPath(),
            TransactionDto.class
        );

        log.info("Transactions: {}", transactions);

        assertNotNull(transactions, "Transactions list should not be null");
        assertFalse(transactions.isEmpty(), "Transactions list should not be empty");

        TransactionDto transaction = transactions.getFirst();
        assertEquals("12345", transaction.getId(), "TransactionDto ID should be '12345'");
        assertEquals("2025-04-16T10:30", transaction.getTime().toString(), "TransactionDto time should be '2025-04-16T10:30'");
        assertEquals("消费", transaction.getType(), "TransactionDto type should be '消费'");
        assertEquals("Walmart", transaction.getCounterparty(), "TransactionDto counterparty should be 'Walmart'");
        assertEquals("iPhone 15", transaction.getProduct(), "TransactionDto product should be 'iPhone 15'");
        assertEquals("支出", transaction.getIncomeOrExpense(), "TransactionDto income/expense should be '支出'");
        assertEquals("5999.99", transaction.getAmount(), "TransactionDto amount should be '5999.99'");
        assertEquals("微信支付", transaction.getPaymentMethod(), "TransactionDto payment method should be '微信支付'");
        assertEquals("成功", transaction.getStatus(), "TransactionDto status should be '成功'");
        assertEquals("7890", transaction.getMerchantOrderId(), "TransactionDto merchant order ID should be '7890'");
//        assertEquals("购买iPhone 15", transaction.getRemark(), "TransactionDto remark should be '购买iPhone 15'");
    }
}
