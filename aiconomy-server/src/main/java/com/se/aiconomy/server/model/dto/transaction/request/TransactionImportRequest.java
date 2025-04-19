package com.se.aiconomy.server.model.dto.transaction.request;

import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Transaction;
import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TransactionImportRequest extends BaseRequest {
    private List<Map<Transaction, BillType>> transactions;

    public TransactionImportRequest(String userId, List<Map<Transaction, BillType>> transactions) {
        this.userId = userId;
        this.transactions = transactions;
    }
}
