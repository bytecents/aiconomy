package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.model.dto.budget.request.*;
import com.se.aiconomy.server.model.dto.budget.response.BudgetCategoryInfo;
import com.se.aiconomy.server.model.dto.budget.response.BudgetInfo;
import com.se.aiconomy.server.model.dto.budget.response.TotalBudgetInfo;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.BudgetService;
import com.se.aiconomy.server.service.impl.BudgetServiceImpl;
import com.se.aiconomy.server.storage.service.JSONStorageService;
import com.se.aiconomy.server.storage.service.impl.JSONStorageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BudgetRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(BudgetRequestHandler.class);
    private final BudgetService budgetService;

    public BudgetRequestHandler(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public BudgetRequestHandler(){
        JSONStorageService jsonStorageService = JSONStorageServiceImpl.getInstance();
        this.budgetService = new BudgetServiceImpl(jsonStorageService);
    }

    /**
     * 处理添加预算请求
     */
    public BudgetInfo handleBudgetAddRequest(BudgetAddRequest request) {
        logger.info("Adding budget for category: {}", request.getBudgetCategory());
        Budget budget = new Budget(
                request.getId(),
                request.getUserId(),
                request.getBudgetCategory(),
                request.getBudgetAmount(),
                request.getAlertSettings(),
                request.getNotes()
        );
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
     * 处理删除预算请求
     */
    public boolean handleRemoveBudgetRequest(BudgetRemoveRequest request) {
        logger.info("Removing budget with id: {}", request.getId());
        try {
            budgetService.removeBudget(request.getId());
            return true;
        } catch (Exception e) {
            logger.error("Failed to remove budget: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 处理更新预算请求
     */
    public BudgetInfo handleBudgetUpdateRequest(BudgetUpdateRequest request) {
        logger.info("Updating budget for category: {}", request.getBudgetCategory());
        try {
            // 构造 Budget 对象（假设是全量更新）
            Budget budget = new Budget();
            budget.setId(request.getId());
            budget.setUserId(request.getUserId());
            budget.setBudgetCategory(request.getBudgetCategory());
            budget.setBudgetAmount(request.getBudgetAmount());
            budget.setAlertSettings(request.getAlertSettings());
            budget.setNotes(request.getNotes());

            // 执行更新
            budgetService.updateBudget(budget);

            logger.info("Successfully updated budget with id: {}", request.getId());
            return convertToBudgetInfo(budget);
        } catch (Exception e) {
            logger.error("Failed to update budget: {}", e.getMessage(), e);
            throw new RuntimeException("Update budget failed: " + e.getMessage());
        }
    }

    /**
     * 处理获得展示数据请求
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
            return totalBudgetInfo;
        } catch (Exception e) {
            logger.error("Failed to get total budget info: {}", e.getMessage(), e);
            throw new RuntimeException("Get total budget info failed: " + e.getMessage());
        }
    }

    /**
     * 处理根据类别获得预算列表请求
     */
    public List<BudgetCategoryInfo> handleGetBudgetByCategory(BudgetCategoryInfoRequest request) {
        logger.info("Getting budgets for user: {}", request.getUserId());
        try {
            List<Budget> budgets = budgetService.getBudgetsByUserId(request.getUserId());
            List<BudgetCategoryInfo> budgetCategoryInfoList = new ArrayList<>();

            // 用于记录已经处理过的类别，防止重复
            Set<String> processedCategories = new HashSet<>();

            for (Budget budget : budgets) {
                String category = budget.getBudgetCategory();
                if (processedCategories.contains(category)) {
                    continue; // 已处理，跳过
                }

                double budgetAmount = budgetService.getTotalBudgetByCategory(request.getUserId(), category);
                double spentAmount = budgetService.getTotalSpentByCategory(request.getUserId(), category);

                BudgetCategoryInfo info = new BudgetCategoryInfo();
                info.setCategoryName(category);
                info.setBudgetAmount(budgetAmount);
                info.setSpentAmount(spentAmount);

                budgetCategoryInfoList.add(info);
                processedCategories.add(category); // 标记为已处理
            }

            logger.info("Successfully retrieved budget info for user: {}", request.getUserId());
            return budgetCategoryInfoList;
        } catch (Exception e) {
            logger.error("Failed to get budgets for category: {}", e.getMessage(), e);
            throw new RuntimeException("Get budgets for category failed: " + e.getMessage());
        }
    }


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
