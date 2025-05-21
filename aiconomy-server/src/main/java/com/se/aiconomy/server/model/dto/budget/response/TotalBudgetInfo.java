package com.se.aiconomy.server.model.dto.budget.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalBudgetInfo {
    private double totalBudget;
    private double totalUsedRatio;
    private double totalSpent;
    private double totalRemaining;
    private int totalAlerts;
    private double dailyAvailableBudget;
    private int leftDays;
}
