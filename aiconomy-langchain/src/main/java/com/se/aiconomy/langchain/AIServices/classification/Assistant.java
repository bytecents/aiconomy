package com.se.aiconomy.langchain.AIServices.classification;


public interface Assistant {
    BillType classifyTransactionFrom(String text);
}