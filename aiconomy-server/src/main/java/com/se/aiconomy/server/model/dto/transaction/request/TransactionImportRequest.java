package com.se.aiconomy.server.model.dto.transaction.request;

import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TransactionImportRequest extends BaseRequest {
    private String accountId;
    private List<Map<Transaction, BillType>> transactions;

    public TransactionImportRequest(String userId, List<Map<Transaction, BillType>> transactions) {
        this.userId = userId;
        this.transactions = transactions;
    }

    public TransactionImportRequest(String userId, String accountId, List<Map<Transaction, BillType>> transactions) {
        this.userId = userId;
        this.accountId = accountId;
        this.transactions = transactions;
    }
}
