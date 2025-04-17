package com.se.aiconomy.server;

import com.se.aiconomy.server.common.utils.CSVUtils;
import com.se.aiconomy.server.model.entity.Transaction;
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
        List<Transaction> transactions = CSVUtils.readCsv(
            Objects.requireNonNull(getClass().getClassLoader().getResource("transactions.csv")).getPath(),
            Transaction.class
        );

        log.info("Transactions: {}", transactions);

        assertNotNull(transactions, "Transactions list should not be null");
        assertFalse(transactions.isEmpty(), "Transactions list should not be empty");

        Transaction transaction = transactions.getFirst();
        assertEquals("12345", transaction.getId(), "Transaction ID should be '12345'");
        assertEquals("2025-04-16T10:30", transaction.getTime().toString(), "Transaction time should be '2025-04-16T10:30'");
        assertEquals("消费", transaction.getType(), "Transaction type should be '消费'");
        assertEquals("Walmart", transaction.getCounterparty(), "Transaction counterparty should be 'Walmart'");
        assertEquals("iPhone 15", transaction.getProduct(), "Transaction product should be 'iPhone 15'");
        assertEquals("支出", transaction.getIncomeOrExpense(), "Transaction income/expense should be '支出'");
        assertEquals("5999.99", transaction.getAmount(), "Transaction amount should be '5999.99'");
        assertEquals("微信支付", transaction.getPaymentMethod(), "Transaction payment method should be '微信支付'");
        assertEquals("成功", transaction.getStatus(), "Transaction status should be '成功'");
        assertEquals("7890", transaction.getMerchantOrderId(), "Transaction merchant order ID should be '7890'");
        assertEquals("购买iPhone 15", transaction.getRemark(), "Transaction remark should be '购买iPhone 15'");
        assertTrue(transaction.getExtraFields().isEmpty(), "Extra fields should be empty if not present in the CSV");
    }
}
