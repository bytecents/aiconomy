package com.se.aiconomy.server.model.dto.budget.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents budget information for a specific user and category.
 * <p>
 * This DTO encapsulates the details of a user's budget, including the budget ID,
 * user ID, budget category, budget amount, alert settings, and any additional notes.
 * It uses Lombok annotations to reduce boilerplate code.
 * </p>
 *
 * <ul>
 *   <li><b>id</b>: The unique identifier for the budget.</li>
 *   <li><b>userId</b>: The ID of the user associated with this budget.</li>
 *   <li><b>budgetCategory</b>: The category of the budget (e.g., "Dining", "Transportation"), customizable and corresponds to the category in Transaction.</li>
 *   <li><b>budgetAmount</b>: The amount of the budget (in yuan).</li>
 *   <li><b>alertSettings</b>: The alert settings for the budget (e.g., "Alert when exceeding 80%").</li>
 *   <li><b>notes</b>: Additional notes.</li>
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class BudgetInfo {
    /**
     * The unique identifier for the budget.
     */
    private String id;

    /**
     * The ID of the user associated with this budget.
     */
    private String userId;

    /**
     * The category of the budget (e.g., "Dining", "Transportation"), customizable and corresponds to the category in Transaction.
     */
    private String budgetCategory;

    /**
     * The amount of the budget (in yuan).
     */
    private double budgetAmount;

    /**
     * The alert settings for the budget (e.g., "Alert when exceeding 80%").
     */
    private double alertSettings;

    /**
     * Additional notes.
     */
    private String notes;
}
