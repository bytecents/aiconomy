package com.se.aiconomy.server.model.dto.transaction.request;

import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for importing transactions.
 * <p>
 * This class is used to encapsulate the request for importing a batch of transactions
 * associated with a specific user and account. It extends {@link BaseRequest} and contains
 * the account ID and a list of transactions with their corresponding dynamic bill types.
 * </p>
 *
 * <ul>
 *   <li><b>accountId</b>: The ID of the account to which the transactions belong.</li>
 *   <li><b>transactions</b>: A list of maps, each mapping a {@link Transaction} to its corresponding {@link DynamicBillType}.</li>
 * </ul>
 *
 * <p>
 * Constructors are provided to support initialization with user ID, account ID, and transactions.
 * </p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TransactionImportRequest extends BaseRequest {
    /**
     * The ID of the account to which the transactions belong.
     */
    private String accountId;

    /**
     * A list of maps, each mapping a Transaction to its corresponding DynamicBillType.
     */
    private List<Map<Transaction, DynamicBillType>> transactions;

    /**
     * Constructs a TransactionImportRequest with the specified user ID and transactions.
     *
     * @param userId       the ID of the user
     * @param transactions the list of transactions with their dynamic bill types
     */
    public TransactionImportRequest(String userId, List<Map<Transaction, DynamicBillType>> transactions) {
        this.userId = userId;
        this.transactions = transactions;
    }

    /**
     * Constructs a TransactionImportRequest with the specified user ID, account ID, and transactions.
     *
     * @param userId       the ID of the user
     * @param accountId    the ID of the account
     * @param transactions the list of transactions with their dynamic bill types
     */
    public TransactionImportRequest(String userId, String accountId, List<Map<Transaction, DynamicBillType>> transactions) {
        this.userId = userId;
        this.accountId = accountId;
        this.transactions = transactions;
    }
}
