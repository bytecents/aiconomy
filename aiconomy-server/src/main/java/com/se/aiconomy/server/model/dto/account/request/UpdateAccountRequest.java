package com.se.aiconomy.server.model.dto.account.request;

import com.se.aiconomy.server.model.entity.Account;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAccountRequest {
    private String userId;
    private Account account;
}