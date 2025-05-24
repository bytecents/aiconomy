package com.se.aiconomy.server.model.dto.user.request;

import com.se.aiconomy.server.model.dto.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Request DTO for retrieving user information.
 * <p>
 * This class is used to encapsulate the request for fetching user information.
 * It extends {@link BaseRequest} and does not contain any additional fields.
 * </p>
 */
@Getter
@Setter
@ToString
public class UserInfoRequest extends BaseRequest {
}
