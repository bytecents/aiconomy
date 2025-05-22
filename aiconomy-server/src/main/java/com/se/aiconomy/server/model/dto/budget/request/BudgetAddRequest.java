package com.se.aiconomy.server.model.dto.budget.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BudgetAddRequest extends BaseRequest {
    private String userId; // 用户ID
    private String budgetCategory; // 预算类别 (如 "餐饮", "交通")，可自定义，与Transaction中的类别对应
    private double budgetAmount; // 预算金额（元）
    private double alertSettings; // 预算预警设置 (如 "超过80%时提醒")
    private String notes; // 备注
}
