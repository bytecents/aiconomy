package com.se.aiconomy.server.model.dto.account.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.NoArgsConstructor;

/**
 * Request object for retrieving accounts by user ID.
 * <p>
 * This DTO is used to encapsulate the user ID for which the accounts are to be retrieved.
 * It extends {@link BaseRequest} and provides a constructor for setting the user ID.
 * </p>
 */
@NoArgsConstructor
public class GetAccountsByUserIdRequest extends BaseRequest {
    /**
     * Constructs a new request with the specified user ID.
     *
     * @param userId the ID of the user whose accounts are to be retrieved
     */
    public GetAccountsByUserIdRequest(String userId) {
        this.userId = userId;
    }
}