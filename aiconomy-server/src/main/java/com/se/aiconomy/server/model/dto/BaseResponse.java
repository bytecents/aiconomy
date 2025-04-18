package com.se.aiconomy.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 统一响应体
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // 快捷方法
    public static <T> BaseResponse<T> success(T data) {
        return BaseResponse.<T>builder().success(true).data(data).message("OK").build();
    }

    public static <T> BaseResponse<T> success(String msg, T data) {
        return BaseResponse.<T>builder().success(true).data(data).message(msg).build();
    }

    public static <T> BaseResponse<T> fail(String msg) {
        return BaseResponse.<T>builder().success(false).data(null).message(msg).build();
    }
}