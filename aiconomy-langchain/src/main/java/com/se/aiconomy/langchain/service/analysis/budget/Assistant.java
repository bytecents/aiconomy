package com.se.aiconomy.langchain.service.analysis.budget;

import com.se.aiconomy.langchain.common.model.Budget.AIAnalysis;

public interface Assistant {
    AIAnalysis analyzeBudgetFrom(String text);
}
