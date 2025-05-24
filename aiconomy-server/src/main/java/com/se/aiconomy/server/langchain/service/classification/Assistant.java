package com.se.aiconomy.server.langchain.service.classification;

import com.se.aiconomy.server.langchain.common.model.DynamicBillType;

/**
 * Interface for financial assistant classification.
 * Provides a method to classify a transaction based on input text.
 */
public interface Assistant {
    /**
     * Classifies the transaction type from the given text.
     *
     * @param text the input text describing the transaction
     * @return the classified {@link DynamicBillType}
     */
    DynamicBillType classifyTransactionFrom(String text);
}