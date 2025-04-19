package com.se.aiconomy.server.langchain.service.analysis.budget;

import com.se.aiconomy.server.langchain.common.config.Configs;
import com.se.aiconomy.server.langchain.common.config.Locale;
import com.se.aiconomy.server.langchain.common.model.Budget;
import com.se.aiconomy.server.langchain.common.prompt.I18nPrompt;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetAnalysisService {
    private static final Logger log = LoggerFactory.getLogger(BudgetAnalysisService.class);
    private final Assistant assistant;

    public BudgetAnalysisService() {
        ChatLanguageModel model = OpenAiChatModel.builder()
            .baseUrl(Configs.BASE_URL)
            .apiKey(Configs.API_KEY)
            .modelName(String.valueOf(Configs.MODEL))
            .build();
        this.assistant = AiServices.create(Assistant.class, model);
    }

    public Budget.AIAnalysis analyzeBudget(@NotNull Budget budget, Locale locale) {
        Map<String, Object> context = buildContext(budget);
        String prompt = new I18nPrompt(new Prompt()).render(locale, context);
        log.info("Budget Analysis Prompt: {}", prompt);
        return assistant.analyzeBudgetFrom(prompt);
    }

    public Budget.AIAnalysis analyzeBudget(@NotNull Budget budget) {
        return analyzeBudget(budget, Locale.EN);
    }

    private Map<String, Object> buildContext(@NotNull Budget budget) {
        Map<String, Object> context = new HashMap<>();

        context.put("total_budget", budget.getTotalBudget());
        context.put("spent", budget.getSpent());
        context.put("alerts", budget.getAlerts());
        context.put("category_budgets", buildCategoryBudgetsContext(budget.getCategoryBudgets()));
        context.put("remaining_budget", budget.getLeft());

        log.info("Budget Context: {}", context);
        return context;
    }

    private List<Map<String, Object>> buildCategoryBudgetsContext(List<Budget.CategoryBudget> categoryBudgets) {
        List<Map<String, Object>> categoryContextList = new ArrayList<>();

        for (Budget.CategoryBudget categoryBudget : categoryBudgets) {
            Map<String, Object> categoryContext = new HashMap<>();
            categoryContext.put("category", categoryBudget.getCategory());
            categoryContext.put("budget", categoryBudget.getBudget());
            categoryContext.put("spent", categoryBudget.getSpent());
            categoryContext.put("left", categoryBudget.getLeft());
            categoryContext.put("percentage_used", categoryBudget.getPercentageUsed());
            categoryContext.put("over_budget", categoryBudget.getOverBudget());

            categoryContextList.add(categoryContext);
        }

        return categoryContextList;
    }
}
