package com.se.aiconomy.server.model.entity;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "Budgets", schemaVersion = "1.0")
public class Budget {
    @Id
    private String budgetId; // 预算ID
    private String userId; // 用户ID
    private String budgetCategory; // 预算类别 (如 "餐饮", "交通")
    private double budgetAmount; // 预算金额（元）
    private String budgetPeriod; // 预算周期 (如 "月", "季度")
    private String alertSettings; // 预算预警设置 (如 "超过80%时提醒")
    private String notes; // 备注

    private Map<String, Object> extraFields = new HashMap<>();

    public Budget() {

    }

    public Budget(String budgetId, String userID, String budgetCategory, double budgetAmount, String budgetPeriod,
            String alertSettings, String notes) {
        this.budgetId = budgetId;
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

    public Map<String, Object> getExtraFields() {
        return extraFields;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBudgetCategory() {
        return budgetCategory;
    }

    public void setBudgetCategory(String budgetCategory) {
        this.budgetCategory = budgetCategory;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public String getBudgetPeriod() {
        return budgetPeriod;
    }

    public void setBudgetPeriod(String budgetPeriod) {
        this.budgetPeriod = budgetPeriod;
    }

    public String getAlertSettings() {
        return alertSettings;
    }

    public void setAlertSettings(String alertSettings) {
        this.alertSettings = alertSettings;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "budgetId='" + budgetId + '\'' +
                ", userId='" + userId + '\'' +
                ", budgetCategory='" + budgetCategory + '\'' +
                ", budgetAmount=" + budgetAmount +
                ", budgetPeriod='" + budgetPeriod + '\'' +
                ", alertSettings='" + alertSettings + '\'' +
                ", notes='" + notes + '\'' +
                ", extraFields=" + extraFields +
                '}';
    }
}
