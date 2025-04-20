package com.se.aiconomy.server.model.dto.budget.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetCategoryInfo {
    private String categoryName;
    private double budgetAmount;
    private double spentAmount;
}
