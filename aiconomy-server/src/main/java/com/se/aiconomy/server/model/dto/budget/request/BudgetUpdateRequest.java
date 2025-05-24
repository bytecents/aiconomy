package com.se.aiconomy.server.model.dto.budget.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

/**
 * Request object for updating budget information.
 * <p>
 * This DTO encapsulates the necessary information for updating a user's budget.
 * It extends {@link BaseRequest} and uses Lombok annotations to reduce boilerplate code.
 * </p>
 *
 * <ul>
 *   <li><b>userId</b>: The ID of the user whose budget is being updated.</li>
 *   <li><b>budgetCategory</b>: The category of the budget (e.g., "Dining", "Transportation"), customizable and corresponds to the category in Transaction.</li>
 *   <li><b>budgetAmount</b>: The amount of the budget (in yuan).</li>
 *   <li><b>alertSettings</b>: The alert settings for the budget (e.g., "Alert when exceeding 80%").</li>
 *   <li><b>notes</b>: Additional notes.</li>
 * </ul>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetUpdateRequest extends BaseRequest {
    /**
     * The ID of the user whose budget is being updated.
     */
    private String userId;

    /**
     * The category of the budget (e.g., "Dining", "Transportation"), customizable and corresponds to the category in Transaction.
     */
    private String budgetCategory;

    /**
     * The amount of the budget (in yuan).
     */
    private Double budgetAmount;

    /**
     * The alert settings for the budget (e.g., "Alert when exceeding 80%").
     */
    private Double alertSettings;

    /**
     * Additional notes.
     */
    private String notes;
}
