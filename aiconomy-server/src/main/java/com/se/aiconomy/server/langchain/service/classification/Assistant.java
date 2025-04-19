package com.se.aiconomy.server.langchain.service.classification;

import com.se.aiconomy.server.langchain.common.model.BillType;

public interface Assistant {
    BillType classifyTransactionFrom(String text);
}