package com.se.aiconomy.server.model.dto.budget.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Request object for removing a budget.
 * <p>
 * This DTO encapsulates the information required to remove a budget,
 * specifically the category of the budget to be removed. It extends
 * {@link BaseRequest} and uses Lombok annotations to reduce boilerplate code.
 * </p>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class BudgetRemoveRequest extends BaseRequest {
    /**
     * The category of the budget to be removed.
     */
    private String category;
}