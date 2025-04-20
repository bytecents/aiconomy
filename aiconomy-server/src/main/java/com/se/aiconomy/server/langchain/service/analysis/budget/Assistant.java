package com.se.aiconomy.server.langchain.service.analysis.budget;

import com.se.aiconomy.server.langchain.common.model.Budget.AIAnalysis;

public interface Assistant {
    AIAnalysis analyzeBudgetFrom(String text);
}
