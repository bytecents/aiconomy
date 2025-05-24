package com.se.aiconomy.server.model.dto.account.request;

import com.se.aiconomy.server.model.entity.Account;
import lombok.*;

/**
 * Request object for updating an account.
 * <p>
 * This DTO is used to encapsulate the user ID and the account information to be updated.
 * It leverages Lombok annotations for boilerplate code generation.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAccountRequest {
    /**
     * The ID of the user who owns the account.
     */
    private String userId;

    /**
     * The account information to be updated.
     */
    private Account account;
}