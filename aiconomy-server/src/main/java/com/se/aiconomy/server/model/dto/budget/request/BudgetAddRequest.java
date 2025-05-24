package com.se.aiconomy.server.model.dto.budget.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

/**
 * Request object for adding a new budget.
 * <p>
 * This DTO encapsulates the information required to add a budget for a user,
 * including user ID, budget category, budget amount, alert settings, and notes.
 * It extends {@link BaseRequest} and uses Lombok annotations to reduce boilerplate code.
 * </p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetAddRequest extends BaseRequest {
    /**
     * The ID of the user to whom the budget belongs.
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
     * Additional notes for the budget.
     */
    private String notes;
}