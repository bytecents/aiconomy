package com.se.aiconomy.server.model.dto.account.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.*;

/**
 * Request object for deleting an account.
 * <p>
 * This DTO is used to encapsulate the account ID of the account to be deleted.
 * It extends {@link BaseRequest} and leverages Lombok annotations for boilerplate code generation.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAccountRequest extends BaseRequest {
    /**
     * The ID of the account to be deleted.
     */
    private String accountId;
}