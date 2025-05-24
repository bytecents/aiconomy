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

/**
 * Service for analyzing budget information using AI assistants.
 * <p>
 * This service constructs prompts based on budget data and interacts with an AI assistant
 * to analyze the budget. It supports internationalization and variable substitution in prompts.
 * </p>
 */
public class BudgetAnalysisService {
    /**
     * Logger instance for logging information and debugging.
     */
    private static final Logger log = LoggerFactory.getLogger(BudgetAnalysisService.class);

    /**
     * The AI assistant used for budget analysis.
     */
    private final Assistant assistant;

    /**
     * Constructs a new BudgetAnalysisService and initializes the AI assistant.
     */
    public BudgetAnalysisService() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl(Configs.BASE_URL)
                .apiKey(Configs.API_KEY)
                .modelName(String.valueOf(Configs.MODEL))
                .build();
        this.assistant = AiServices.create(Assistant.class, model);
    }

    /**
     * Analyzes the given budget using the specified locale.
     *
     * @param budget the budget to analyze
     * @param locale the locale for prompt internationalization
     * @return the result of the AI budget analysis
     */
    public Budget.AIAnalysis analyzeBudget(@NotNull Budget budget, Locale locale) {
        Map<String, Object> context = buildContext(budget);
        String prompt = new I18nPrompt(new Prompt()).render(locale, context);
        log.info("Budget Analysis Prompt: {}", prompt);
        return assistant.analyzeBudgetFrom(prompt);
    }

    /**
     * Analyzes the given budget using the default locale (English).
     *
     * @param budget the budget to analyze
     * @return the result of the AI budget analysis
     */
    public Budget.AIAnalysis analyzeBudget(@NotNull Budget budget) {
        return analyzeBudget(budget, Locale.EN);
    }

    /**
     * Builds the context map for prompt variable substitution based on the given budget.
     *
     * @param budget the budget to build context from
     * @return a map containing context variables for the prompt
     */
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

    /**
     * Builds a list of context maps for each category budget.
     *
     * @param categoryBudgets the list of category budgets
     * @return a list of maps, each representing a category budget context
     */
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