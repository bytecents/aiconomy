package com.se.aiconomy.server.model.dto.user.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Request DTO for user login.
 * <p>
 * This class encapsulates the request data required for user login,
 * including the user's email and password.
 * </p>
 *
 * <ul>
 *   <li><b>email</b>: The email address of the user attempting to log in.</li>
 *   <li><b>password</b>: The password associated with the user's account.</li>
 * </ul>
 *
 * <p>
 * Lombok annotations are used to generate boilerplate code such as getters, setters,
 * constructors, toString, and builder methods.
 * </p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {
    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The password of the user.
     */
    private String password;
}
