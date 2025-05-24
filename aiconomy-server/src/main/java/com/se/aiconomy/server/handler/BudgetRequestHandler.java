package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.langchain.common.model.DynamicBillType;
import com.se.aiconomy.server.langchain.service.analysis.budget.BudgetAnalysisService;
import com.se.aiconomy.server.model.dto.budget.request.*;
import com.se.aiconomy.server.model.dto.budget.response.BudgetCategoryInfo;
import com.se.aiconomy.server.model.dto.budget.response.BudgetInfo;
import com.se.aiconomy.server.model.dto.budget.response.TotalBudgetInfo;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import com.se.aiconomy.server.langchain.common.model.Budget.AIAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Handles budget-related requests, including adding, removing, updating, retrieving, and analyzing budgets.
 */
public class BudgetRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(BudgetRequestHandler.class);
    private final BudgetService budgetService;
    private final BudgetAnalysisService budgetAnalysisService = new BudgetAnalysisService();

    /**
     * Constructs a BudgetRequestHandler with a custom BudgetService.
     *
     * @param budgetService the BudgetService to use
     */
    public BudgetRequestHandler(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * Constructs a BudgetRequestHandler with a default BudgetService implementation.
     */
    public BudgetRequestHandler() {
        JSONStorageService jsonStorageService = JSONStorageServiceImpl.getInstance();
        this.budgetService = new BudgetServiceImpl(jsonStorageService);
    }

    /**
     * Handles the request to add a new budget.
     *
     * @param request the request containing budget details
     * @return the added budget information
     */
    public BudgetInfo handleBudgetAddRequest(BudgetAddRequest request) {
        logger.info("Adding budget for category: {}", request.getBudgetCategory());
        Budget budget = new Budget();
        budget.setId(UUID.randomUUID().toString()); // Generate unique ID
        budget.setUserId(request.getUserId());
        budget.setBudgetCategory(request.getBudgetCategory());
        budget.setBudgetAmount(request.getBudgetAmount());
        budget.setAlertSettings(request.getAlertSettings());
        budget.setNotes(request.getNotes());
        try {
            budgetService.addBudget(budget);
            logger.info("Added budget for category: {}", request.getBudgetCategory());
            return convertToBudgetInfo(budget);
        } catch (Exception e) {
            logger.error("Failed to add budget: {}", e.getMessage());
            throw new RuntimeException("Add budget failed: " + e.getMessage());
        }
    }

    /**
     * Handles the request to remove a budget.
     *
     * @param request the request containing user ID and category to remove
     * @return true if the budget was removed successfully, false otherwise
     */
    public boolean handleRemoveBudgetRequest(BudgetRemoveRequest request) {
        logger.info("Removing budget with category: {}", request.getCategory());
        try {
            Budget budget = budgetService.getBudgetByCategory(request.getUserId(), request.getCategory());
            budgetService.removeBudget(budget.getId());
            return true;
        } catch (Exception e) {
            logger.error("Failed to remove budget: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Handles the request to update a budget.
     *
     * @param request the request containing updated budget details
     * @return the updated budget information
     */
    public BudgetInfo handleBudgetUpdateRequest(BudgetUpdateRequest request) {
        logger.info("Updating budget for category: {}", request.getBudgetCategory());
        try {
            // Construct Budget object (assume full update)
            Budget budget = budgetService.getBudgetByCategory(request.getUserId(), request.getBudgetCategory());

            budget.setUserId(request.getUserId());
            budget.setBudgetCategory(request.getBudgetCategory());
            budget.setBudgetAmount(request.getBudgetAmount());
            budget.setAlertSettings(request.getAlertSettings());
            budget.setNotes(request.getNotes());

            // Perform update
            budgetService.updateBudget(budget);

            logger.info("Successfully updated budget with id: {}", budget.getId());
            return convertToBudgetInfo(budget);
        } catch (Exception e) {
            logger.error("Failed to update budget: {}", e.getMessage(), e);
            throw new RuntimeException("Update budget failed: " + e.getMessage());
        }
    }

    /**
     * Handles the request to get total budget information for a user.
     *
     * @param request the request containing the user ID
     * @return the total budget information
     */
    public TotalBudgetInfo handleGetTotalBudgetRequest(BudgetTotalInfoRequest request) {
        logger.info("Getting total budget info for user: {}", request.getUserId());
        try {
            TotalBudgetInfo totalBudgetInfo = new TotalBudgetInfo();
            totalBudgetInfo.setTotalBudget(budgetService.getTotalBudget(request.getUserId()));
            totalBudgetInfo.setTotalUsedRatio(budgetService.getTotalSpent(request.getUserId()) / budgetService.getTotalBudget(request.getUserId()));
            totalBudgetInfo.setTotalSpent(budgetService.getTotalSpent(request.getUserId()));
            totalBudgetInfo.setTotalRemaining(budgetService.getTotalBudget(request.getUserId()) - budgetService.getTotalSpent(request.getUserId()));
            totalBudgetInfo.setTotalAlerts(budgetService.getTotalAlertCount(request.getUserId()));
            totalBudgetInfo.setDailyAvailableBudget(budgetService.getDailyAvailableBudget(request.getUserId()));
            totalBudgetInfo.setLeftDays(budgetService.getLeftDays(request.getUserId()));
            return totalBudgetInfo;
        } catch (Exception e) {
            logger.error("Failed to get total budget info: {}", e.getMessage(), e);
            throw new RuntimeException("Get total budget info failed: " + e.getMessage());
        }
    }

    /**
     * Handles the request to get a list of budgets by category for a user.
     *
     * @param request the request containing the user ID
     * @return the list of budget category information
     */
    public List<BudgetCategoryInfo> handleGetBudgetByCategory(BudgetCategoryInfoRequest request) {
        logger.info("Getting budgets for user: {}", request.getUserId());
        try {
            List<Budget> budgets = budgetService.getBudgetsByUserId(request.getUserId());
            List<BudgetCategoryInfo> budgetCategoryInfoList = new ArrayList<>();

            // Used to record processed categories to avoid duplicates
            Set<String> processedCategories = new HashSet<>();

            for (Budget budget : budgets) {
                String category = budget.getBudgetCategory();
                if (processedCategories.contains(category)) {
                    continue; // Already processed, skip
                }

                double budgetAmount = budgetService.getTotalBudgetByCategory(request.getUserId(), category);
                double spentAmount = budgetService.getTotalSpentByCategory(request.getUserId(), category);
                double remainingAmount = budgetAmount - spentAmount;
                double usedRatio = spentAmount / budgetAmount;

                BudgetCategoryInfo info = new BudgetCategoryInfo();
                info.setCategoryName(category);
                info.setBudgetAmount(budgetAmount);
                info.setSpentAmount(spentAmount);
                info.setRemainingAmount(remainingAmount);
                info.setUsedRatio(usedRatio);

                budgetCategoryInfoList.add(info);
                processedCategories.add(category); // Mark as processed
            }

            logger.info("Successfully retrieved budget info for user: {}", request.getUserId());
            return budgetCategoryInfoList;
        } catch (Exception e) {
            logger.error("Failed to get budgets for category: {}", e.getMessage(), e);
            throw new RuntimeException("Get budgets for category failed: " + e.getMessage());
        }
    }

    /**
     * Handles the request to analyze the budget for a user.
     *
     * @param request the request containing the user ID
     * @return the AI analysis result of the budget
     */
    public AIAnalysis handleBudgetAnalysisRequest(BudgetAnalysisRequest request) {
        String userId = request.getUserId();
        logger.info("Analyzing budget for user: {}", userId);
        try {
            List<Budget> budgets = budgetService.getBudgetsByUserId(userId);
            System.out.println("budgets = " + budgets);

            List<com.se.aiconomy.server.langchain.common.model.Budget.CategoryBudget> categoryBudgets = new ArrayList<>();
            for (Budget budget : budgets) {
                double spent = budgetService.getTotalSpentByCategory(userId, budget.getBudgetCategory());
                categoryBudgets.add(new com.se.aiconomy.server.langchain.common.model.Budget.CategoryBudget(
                        DynamicBillType.fromString(budget.getBudgetCategory()),
                        budget.getBudgetAmount(),
                        spent
                ));
            }

            com.se.aiconomy.server.langchain.common.model.Budget willBeAnalyzedBudget = new com.se.aiconomy.server.langchain.common.model.Budget();
            willBeAnalyzedBudget.setTotalBudget(budgetService.getTotalBudget(userId));
            willBeAnalyzedBudget.setSpent(budgetService.getTotalSpent(userId));
            willBeAnalyzedBudget.setAlerts(budgetService.getTotalAlertCount(userId));
            willBeAnalyzedBudget.setCategoryBudgets(categoryBudgets);

            System.out.println("willBeAnalyzedBudget = " + willBeAnalyzedBudget);
            AIAnalysis analysis = budgetAnalysisService.analyzeBudget(willBeAnalyzedBudget);
            logger.info("Successfully analyzed budget for user: {}", userId);
            return analysis;
        } catch (Exception e) {
            logger.error("Failed to analyze budget: {}", e.getMessage(), e);
            throw new RuntimeException("Analyze budget failed: " + e.getMessage());
        }
    }

    /**
     * Converts a Budget entity to a BudgetInfo DTO.
     *
     * @param budget the Budget entity
     * @return the BudgetInfo DTO
     */
    private BudgetInfo convertToBudgetInfo(Budget budget) {
        return new BudgetInfo(
                budget.getId(),
                budget.getUserId(),
                budget.getBudgetCategory(),
                budget.getBudgetAmount(),
                budget.getAlertSettings(),
                budget.getNotes()
        );
    }
}