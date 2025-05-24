package com.se.aiconomy.server.model.dto.budget.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Request object for retrieving budget category information.
 * <p>
 * This DTO is used to encapsulate the necessary information for requesting
 * budget category details. It extends {@link BaseRequest} and uses Lombok
 * annotations to reduce boilerplate code.
 * </p>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class BudgetCategoryInfoRequest extends BaseRequest {
}