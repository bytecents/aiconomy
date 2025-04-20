package com.se.aiconomy.server.model.dto.account.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GetAccountsByUserIdRequest extends BaseRequest {
    public GetAccountsByUserIdRequest(String userId) {
        this.userId = userId;
    }
}
