package com.se.aiconomy.server.model.dto.budget.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Request object for retrieving total budget information.
 * <p>
 * This DTO is used to encapsulate the necessary information for requesting
 * total budget details. It extends {@link BaseRequest} and uses Lombok
 * annotations to reduce boilerplate code.
 * </p>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class BudgetTotalInfoRequest extends BaseRequest {
}