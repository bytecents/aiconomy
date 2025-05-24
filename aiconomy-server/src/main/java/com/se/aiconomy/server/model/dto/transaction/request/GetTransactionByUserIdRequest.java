package com.se.aiconomy.server.model.dto.transaction.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.NoArgsConstructor;

/**
 * Request DTO for retrieving transactions by user ID.
 * <p>
 * This class is used to encapsulate the request for fetching transactions associated with a specific user.
 * It extends {@link BaseRequest} and does not contain any additional fields.
 * </p>
 */
@NoArgsConstructor
public class GetTransactionByUserIdRequest extends BaseRequest {
}
