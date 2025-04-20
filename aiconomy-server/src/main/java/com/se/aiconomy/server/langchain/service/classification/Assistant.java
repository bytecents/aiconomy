package com.se.aiconomy.server.langchain.service.classification;

import com.se.aiconomy.server.langchain.common.model.DynamicBillType;

public interface Assistant {
    DynamicBillType classifyTransactionFrom(String text);
}