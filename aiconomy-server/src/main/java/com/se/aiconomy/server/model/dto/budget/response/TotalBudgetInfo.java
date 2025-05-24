package com.se.aiconomy.server.model.dto.budget.response;

import lombok.*;

/**
 * Represents the aggregated budget information for a user.
 * <p>
 * This DTO encapsulates the total budget, total used ratio, total spent amount,
 * total remaining amount, total number of alerts, daily available budget, and the number of days left.
 * </p>
 *
 * <ul>
 *   <li><b>totalBudget</b>: The sum of all budgeted amounts.</li>
 *   <li><b>totalUsedRatio</b>: The overall ratio of used budget to total budget.</li>
 *   <li><b>totalSpent</b>: The total amount spent.</li>
 *   <li><b>totalRemaining</b>: The total remaining budget.</li>
 *   <li><b>totalAlerts</b>: The total number of budget alerts triggered.</li>
 *   <li><b>dailyAvailableBudget</b>: The available budget per day.</li>
 *   <li><b>leftDays</b>: The number of days left in the budget period.</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TotalBudgetInfo {
    /**
     * The sum of all budgeted amounts.
     */
    private double totalBudget;

    /**
     * The overall ratio of used budget to total budget.
     */
    private double totalUsedRatio;

    /**
     * The total amount spent.
     */
    private double totalSpent;

    /**
     * The total remaining budget.
     */
    private double totalRemaining;

    /**
     * The total number of budget alerts triggered.
     */
    private int totalAlerts;

    /**
     * The available budget per day.
     */
    private double dailyAvailableBudget;

    /**
     * The number of days left in the budget period.
     */
    private int leftDays;
}
