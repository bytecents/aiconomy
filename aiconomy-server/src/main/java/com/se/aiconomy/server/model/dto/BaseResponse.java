package com.se.aiconomy.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Generic response wrapper for API responses.
 * <p>
 * This class provides a unified structure for API responses, including
 * success status, message, and optional data payload.
 * </p>
 *
 * @param <T> the type of the response data
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class BaseResponse<T> {
    /**
     * Indicates whether the request was successful.
     */
    private boolean success;

    /**
     * Message providing additional information about the response.
     */
    private String message;

    /**
     * The data payload of the response.
     */
    private T data;

    /**
     * Creates a successful response with the given data and a default message.
     *
     * @param data the response data
     * @param <T>  the type of the response data
     * @return a successful BaseResponse instance
     */
    public static <T> BaseResponse<T> success(T data) {
        return BaseResponse.<T>builder().success(true).data(data).message("OK").build();
    }

    /**
     * Creates a successful response with a custom message and data.
     *
     * @param msg  the custom success message
     * @param data the response data
     * @param <T>  the type of the response data
     * @return a successful BaseResponse instance
     */
    public static <T> BaseResponse<T> success(String msg, T data) {
        return BaseResponse.<T>builder().success(true).data(data).message(msg).build();
    }

    /**
     * Creates a failed response with a custom error message.
     *
     * @param msg the error message
     * @param <T> the type of the response data
     * @return a failed BaseResponse instance with null data
     */
    public static <T> BaseResponse<T> fail(String msg) {
        return BaseResponse.<T>builder().success(false).data(null).message(msg).build();
    }
}
