package com.se.aiconomy.server.langchain.service.analysis.budget;

import com.se.aiconomy.server.langchain.common.model.Budget.AIAnalysis;

/**
 * Interface for budget analysis assistants.
 * <p>
 * Implementations of this interface provide methods to analyze budget information from text input.
 * </p>
 */
public interface Assistant {
    /**
     * Analyzes the budget information from the given text.
     *
     * @param text the input text containing budget information
     * @return the result of the AI budget analysis
     */
    AIAnalysis analyzeBudgetFrom(String text);
}