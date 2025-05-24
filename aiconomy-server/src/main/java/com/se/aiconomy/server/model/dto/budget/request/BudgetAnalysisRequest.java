package com.se.aiconomy.server.model.dto.budget.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

/**
 * Request object for budget analysis.
 * <p>
 * This DTO is used to encapsulate the user ID for which the budget analysis is to be performed.
 * It extends {@link BaseRequest} and uses Lombok annotations to reduce boilerplate code.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAnalysisRequest extends BaseRequest {
    /**
     * The ID of the user for whom the budget analysis is requested.
     */
    public String userId;
}