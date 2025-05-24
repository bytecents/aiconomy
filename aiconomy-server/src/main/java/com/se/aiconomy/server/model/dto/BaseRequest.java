package com.se.aiconomy.server.model.dto;

/**
 * Abstract base class for request DTOs.
 * <p>
 * This class provides a common field for user identification, which can be extended by
 * specific request DTOs in the application.
 * </p>
 *
 * <ul>
 *   <li><b>userId</b>: The unique identifier of the user making the request.</li>
 * </ul>
 *
 * <p>
 * Lombok annotations are used to automatically generate boilerplate code such as getters,
 * setters, no-argument and all-argument constructors.
 * </p>
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseRequest {
    /**
     * The unique identifier of the user making the request.
     */
    public String userId;
}
