package com.se.aiconomy.langchain.service.classification;

import com.se.aiconomy.langchain.common.model.BillType;

public interface Assistant {
    BillType classifyTransactionFrom(String text);
}