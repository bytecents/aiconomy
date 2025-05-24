package com.se.aiconomy.server.model.dto.account.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import com.se.aiconomy.server.model.entity.Account;
import lombok.*;

import java.util.List;

/**
 * Request object for adding multiple accounts.
 * <p>
 * This DTO is used to encapsulate a list of accounts to be added in a single request.
 * It extends {@link BaseRequest} and leverages Lombok annotations for boilerplate code generation.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddAccountsRequest extends BaseRequest {
    /**
     * The list of accounts to be added.
     */
    private List<Account> accounts;
}