package com.se.aiconomy.service.langchain.AIServices.classification;


public interface Assistant {
    BillType classifyTransactionFrom(String text);
}