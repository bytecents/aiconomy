package com.se.aiconomy.server.model.dto.budget.response;

import lombok.*;

/**
 * Represents detailed information about a budget category.
 * <p>
 * This DTO encapsulates the budget category name, the total budgeted amount,
 * the amount already spent, the remaining budget, and the usage ratio.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BudgetCategoryInfo {
    /**
     * The name of the budget category.
     */
    private String categoryName;

    /**
     * The total amount budgeted for this category.
     */
    private double budgetAmount;

    /**
     * The amount already spent in this category.
     */
    private double spentAmount;

    /**
     * The remaining budget for this category.
     */
    private double remainingAmount;

    /**
     * The ratio of the spent amount to the total budget amount.
     */
    private double usedRatio;
}
