package com.se.aiconomy.server.model.dto.budget.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BudgetAddRequest extends BaseRequest {
    private String Id;
    private String budgetCategory;
    private Double budgetAmount;
    private String budgetPeriod;
    private String alertSettings;
    private String notes;
}
