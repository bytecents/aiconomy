package com.se.aiconomy.server.model.dto.user.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Request DTO for deleting a user.
 * <p>
 * This class is used to encapsulate the request for deleting a user.
 * It extends {@link BaseRequest} and does not contain any additional fields.
 * </p>
 */
@Getter
@Setter
@ToString
public class UserDeleteRequest extends BaseRequest {
}
