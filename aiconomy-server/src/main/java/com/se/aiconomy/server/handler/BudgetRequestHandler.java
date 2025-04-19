package com.se.aiconomy.server.handler;

import com.se.aiconomy.server.model.dto.budget.request.BudgetAddRequest;
import com.se.aiconomy.server.model.dto.budget.request.BudgetDeleteRequest;
import com.se.aiconomy.server.model.dto.budget.request.BudgetListRequest;
import com.se.aiconomy.server.model.dto.budget.request.BudgetUpdateRequest;
import com.se.aiconomy.server.model.dto.budget.response.BudgetInfo;
import com.se.aiconomy.server.model.entity.Budget;
import com.se.aiconomy.server.service.BudgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class BudgetRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(BudgetRequestHandler.class);
    private final BudgetService budgetService;

    public BudgetRequestHandler(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * 处理新增预算请求
     */
    public BudgetInfo handleAddBudgetRequest(BudgetAddRequest request) {
        Budget newBudget = new Budget();
        newBudget.setId(request.getId());
        newBudget.setUserId(request.getUserId());
        newBudget.setBudgetCategory(request.getBudgetCategory());
        newBudget.setBudgetAmount(request.getBudgetAmount());
        newBudget.setBudgetPeriod(request.getBudgetPeriod());
        newBudget.setAlertSettings(request.getAlertSettings());
        newBudget.setNotes(request.getNotes());

        try {
            budgetService.addBudget(newBudget);
            return convertToBudgetInfo(newBudget);
        } catch (Exception e) {
            logger.error("Failed to add budget: {}", e.getMessage());
            throw new RuntimeException("Add budget failed: " + e.getMessage());
        }
    }

    /**
     * 处理更新预算请求
     */
    public BudgetInfo handleUpdateBudgetRequest(BudgetUpdateRequest request) {
        logger.info("Processing update budget request for userId: {}", request.getUserId());

        try {
            // 获取当前用户信息
            Budget existingBudget = budgetService.getBudget(request.getId());

            // 只更新非null的字段
            if (request.getId() != null) existingBudget.setId(request.getId());
            if (request.getUserId() != null) existingBudget.setUserId(request.getUserId());
            if (request.getBudgetCategory() != null) existingBudget.setBudgetCategory(request.getBudgetCategory());
            if (request.getBudgetAmount() != null) existingBudget.setBudgetAmount(request.getBudgetAmount());
            if (request.getBudgetPeriod() != null) existingBudget.setBudgetPeriod(request.getBudgetPeriod());
            if (request.getAlertSettings() != null) existingBudget.setAlertSettings(request.getAlertSettings());
            if (request.getNotes() != null) existingBudget.setNotes(request.getNotes());

            budgetService.updateBudget(existingBudget);
            return convertToBudgetInfo(existingBudget);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update budget: " + e.getMessage());
        }
    }

    /**
     * 处理删除预算请求
     */
    public void handleDeleteBudgetRequest(BudgetDeleteRequest request) {
        try {
            budgetService.removeBudget(request.getId());
        } catch (Exception e) {
            logger.error("Failed to delete budget: {}", e.getMessage());
            throw new RuntimeException("Delete budget failed: " + e.getMessage());
        }
    }

    /**
     * 获取用户的预算列表
     */
    public List<BudgetInfo> handleListBudgetsRequest(BudgetListRequest request) {
        try {
            List<Budget> budgets = budgetService.getBudgetsByUserId(request.getUserId());
            return budgets.stream().map(this::convertToBudgetInfo).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to get budgets: {}", e.getMessage());
            throw new RuntimeException("List budgets failed: " + e.getMessage());
        }
    }

    private BudgetInfo convertToBudgetInfo(Budget budget) {
        return new BudgetInfo(
                budget.getId(),
                budget.getUserId(),
                budget.getBudgetCategory(),
                budget.getBudgetAmount(),
                budget.getBudgetPeriod(),
                budget.getAlertSettings(),
                budget.getNotes()
        );
    }
}
