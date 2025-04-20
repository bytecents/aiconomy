package com.se.aiconomy.server.model.dto.budget.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetUpdateRequest extends BaseRequest {
    private String id; // 预算ID
    private String userId; // 用户ID
    private String budgetCategory; // 预算类别 (如 "餐饮", "交通")，可自定义，与Transaction中的类别对应
    private Double budgetAmount; // 预算金额（元）
    private Double alertSettings; // 预算预警设置 (如 "超过80%时提醒")
    private String notes; // 备注
}
