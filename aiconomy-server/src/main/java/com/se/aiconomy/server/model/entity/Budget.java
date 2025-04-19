package com.se.aiconomy.server.model.entity;

import com.se.aiconomy.server.storage.common.Identifiable;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@Document(collection = "Budgets", schemaVersion = "1.0")
public class Budget implements Identifiable {
    @Id
    private String Id; // 预算ID
    private String userId; // 用户ID
    private String budgetCategory; // 预算类别 (如 "餐饮", "交通")
    private Double budgetAmount; // 预算金额（元）
    private String budgetPeriod; // 预算周期 (如 "月", "季度")
    private String alertSettings; // 预算预警设置 (如 "超过80%时提醒")
    private String notes; // 备注

    private Map<String, Object> extraFields = new HashMap<>();

    public Budget() {

    }

    public Budget(String userID, String budgetCategory, double budgetAmount, String budgetPeriod,
                  String alertSettings, String notes) {
        this.userId = userID;
        this.budgetCategory = budgetCategory;
        this.budgetAmount = budgetAmount;
        this.budgetPeriod = budgetPeriod;
        this.alertSettings = alertSettings;
        this.notes = notes;
    }

    // 添加扩展字段
    public void addExtraField(String key, Object value) {
        extraFields.put(key, value);
    }

    // 获取扩展字段
    public Object getExtraField(String key) {
        return extraFields.get(key);
    }
}
