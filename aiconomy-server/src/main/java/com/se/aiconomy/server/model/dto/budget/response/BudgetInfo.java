package com.se.aiconomy.server.model.dto.budget.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BudgetInfo {
    private String Id; // 预算ID
    private String userId; // 用户ID
    private String budgetCategory; // 预算类别 (如 "餐饮", "交通")
    private double budgetAmount; // 预算金额（元）
    private String budgetPeriod; // 预算周期 (如 "月", "季度")
    private String alertSettings; // 预算预警设置 (如 "超过80%时提醒")
    private String notes; // 备注
}
