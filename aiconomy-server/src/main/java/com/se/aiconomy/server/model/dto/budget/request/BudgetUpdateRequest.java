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
    private String Id;
    private String budgetCategory;
    private Double budgetAmount;
    private String budgetPeriod;
    private String alertSettings;
    private String notes;

    /**
     * 判断是否真的有字段需要更新
     */
    public boolean hasUpdates() {
        return Id != null || budgetCategory != null || budgetAmount != null || budgetPeriod != null || alertSettings != null || notes != null;
    }
}
