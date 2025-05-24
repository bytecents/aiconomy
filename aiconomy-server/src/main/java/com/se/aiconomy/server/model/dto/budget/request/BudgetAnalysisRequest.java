package com.se.aiconomy.server.model.dto.budget.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAnalysisRequest extends BaseRequest {
    public String userId;
}
