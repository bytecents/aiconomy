package com.se.aiconomy.server.model.entity;

import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.*;

@Getter
@Setter
@ToString
@Document(collection = "Budgets", schemaVersion = "1.0")
@NoArgsConstructor
@AllArgsConstructor
public class Budget implements Identifiable {
    @Id
    private String id; // 预算ID
    private String userId; // 用户ID
    private String budgetCategory; // 预算类别 (如 "餐饮", "交通")，可自定义，与Transaction中的类别对应
    private double budgetAmount; // 预算金额（元）
    private double alertSettings; // 预算预警设置 (如 "超过80%时提醒")
    private String notes; // 备注
}
