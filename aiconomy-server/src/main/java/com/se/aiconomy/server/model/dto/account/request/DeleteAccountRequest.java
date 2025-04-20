package com.se.aiconomy.server.model.dto.account.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAccountRequest extends BaseRequest {
    private String accountId;
}
