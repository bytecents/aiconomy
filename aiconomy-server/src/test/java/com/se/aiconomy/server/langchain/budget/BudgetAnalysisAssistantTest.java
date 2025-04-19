package com.se.aiconomy.server.langchain.budget;

import com.se.aiconomy.server.langchain.common.model.BillType;
import com.se.aiconomy.server.langchain.common.model.Budget;
import com.se.aiconomy.server.langchain.service.analysis.budget.BudgetAnalysisService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BudgetAnalysisAssistantTest {

    private static final Logger log = LoggerFactory.getLogger(BudgetAnalysisAssistantTest.class);

    @Test
    public void testBudgetAnalysis() {
        BudgetAnalysisService service = new BudgetAnalysisService();

        Budget budget = new Budget();
        budget.setTotalBudget(10000);
        budget.setSpent(5000);
        budget.setAlerts(2);
        budget.setCategoryBudgets(new ArrayList<>(
            List.of(
                new Budget.CategoryBudget(BillType.DINING, 1500, 1650),
                new Budget.CategoryBudget(BillType.EDUCATION, 1000, 800),
                new Budget.CategoryBudget(BillType.ENTERTAINMENT, 500, 300)
            )
        ));

        Budget.AIAnalysis analysis = service.analyzeBudget(budget);
        log.info("AI Analysis: {}", analysis);
        assertNotNull(analysis, "Analysis result should not be null");
    }
}
