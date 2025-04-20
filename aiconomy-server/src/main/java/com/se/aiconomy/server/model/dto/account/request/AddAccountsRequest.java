package com.se.aiconomy.server.model.dto.account.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import com.se.aiconomy.server.model.entity.Account;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddAccountsRequest extends BaseRequest {
    private List<Account> accounts;
}
