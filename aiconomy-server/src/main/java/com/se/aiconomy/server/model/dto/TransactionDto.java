package com.se.aiconomy.server.model.dto;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a transaction record.
 * <p>
 * This class encapsulates all relevant information about a transaction,
 * including identifiers, time, type, counterparty, product, income/expense
 * status, amount, payment method, status, merchant order ID, account ID,
 * user ID, remarks, and associated bill type.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: Unique identifier for the transaction.</li>
 *   <li><b>time</b>: The date and time when the transaction occurred.</li>
 *   <li><b>type</b>: The type of transaction (e.g., "consumption", "transfer").</li>
 *   <li><b>counterparty</b>: The counterparty involved in the transaction (e.g., "Walmart", "Alipay").</li>
 *   <li><b>product</b>: The name of the product involved in the transaction (e.g., "iPhone 15").</li>
 *   <li><b>incomeOrExpense</b>: Indicates whether the transaction is income or expense ("income" or "expense").</li>
 *   <li><b>amount</b>: The amount of the transaction (in yuan).</li>
 *   <li><b>paymentMethod</b>: The payment method used (e.g., "WeChat Pay", "Credit Card").</li>
 *   <li><b>status</b>: The status of the transaction (e.g., "successful", "pending payment").</li>
 *   <li><b>merchantOrderId</b>: The merchant order number.</li>
 *   <li><b>accountId</b>: The account ID associated with the transaction.</li>
 *   <li><b>userId</b>: The user ID associated with the transaction.</li>
 *   <li><b>remark</b>: Additional remarks or notes about the transaction.</li>
 *   <li><b>billType</b>: The dynamic bill type associated with the transaction.</li>
 * </ul>
 *
 * <p>
 * Lombok annotations are used to generate boilerplate code such as getters, setters,
 * constructors, toString, and builder methods.
 * </p>
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transactions", schemaVersion = "1.0")
public class TransactionDto implements Identifiable {
    /**
     * Unique identifier for the transaction.
     */
    @Id
    @CsvBindByPosition(position = 0)
    private String id;

    /**
     * The date and time when the transaction occurred.
     */
    @CsvBindByPosition(position = 1)
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time;

    /**
     * The type of transaction (e.g., "consumption", "transfer").
     */
    @CsvBindByPosition(position = 2)
    private String type;

    /**
     * The counterparty involved in the transaction (e.g., "Walmart", "Alipay").
     */
    @CsvBindByPosition(position = 3)
    private String counterparty;

    /**
     * The name of the product involved in the transaction (e.g., "iPhone 15").
     */
    @CsvBindByPosition(position = 4)
    private String product;

    /**
     * Indicates whether the transaction is income or expense ("income" or "expense").
     */
    @CsvBindByPosition(position = 5)
    private String incomeOrExpense;

    /**
     * The amount of the transaction (in yuan).
     */
    @CsvBindByPosition(position = 6)
    private String amount;

    /**
     * The payment method used (e.g., "WeChat Pay", "Credit Card").
     */
    @CsvBindByPosition(position = 7)
    private String paymentMethod;

    /**
     * The status of the transaction (e.g., "successful", "pending payment").
     */
    @CsvBindByPosition(position = 8)
    private String status;

    /**
     * The merchant order number.
     */
    @CsvBindByPosition(position = 9)
    private String merchantOrderId;

    /**
     * The account ID associated with the transaction.
     */
    @CsvBindByPosition(position = 10)
    private String accountId;

    /**
     * The user ID associated with the transaction.
     */
    @CsvBindByPosition(position = 11)
    private String userId;

    /**
     * Additional remarks or notes about the transaction.
     */
    @CsvBindByPosition(position = 12)
    private String remark;

    /**
     * The dynamic bill type associated with the transaction.
     */
    private DynamicBillType billType;
}
