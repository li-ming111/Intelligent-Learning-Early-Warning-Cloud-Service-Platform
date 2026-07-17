package com.academic.common.dto;

import com.academic.common.constant.ApiConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一API响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码: 200成功, 其他为错误码
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private long timestamp;
    
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ApiConstants.SUCCESS_CODE, ApiConstants.SUCCESS_MESSAGE, null, System.currentTimeMillis());
    }
    
    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ApiConstants.SUCCESS_CODE, ApiConstants.SUCCESS_MESSAGE, data, System.currentTimeMillis());
    }
    
    /**
     * 成功响应（带消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ApiConstants.SUCCESS_CODE, message, data, System.currentTimeMillis());
    }
    
    /**
     * 失败响应（指定状态码）
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, System.currentTimeMillis());
    }
    
    /**
     * 失败响应（指定状态码和数据）
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(code, message, data, System.currentTimeMillis());
    }
    
    /**
     * 失败响应（默认500错误）
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ApiConstants.SERVER_ERROR_CODE, message, null, System.currentTimeMillis());
    }
    
    /**
     * 参数错误
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(ApiConstants.BAD_REQUEST_CODE, message, null, System.currentTimeMillis());
    }
    
    /**
     * 未授权
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(ApiConstants.UNAUTHORIZED_CODE, message, null, System.currentTimeMillis());
    }
    
    /**
     * 禁止访问
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(ApiConstants.FORBIDDEN_CODE, message, null, System.currentTimeMillis());
    }
    
    /**
     * 资源不存在
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(ApiConstants.NOT_FOUND_CODE, message, null, System.currentTimeMillis());
    }
    
    /**
     * 服务器错误
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(ApiConstants.SERVER_ERROR_CODE, message, null, System.currentTimeMillis());
    }
    
    public boolean isSuccess() {
        return code == ApiConstants.SUCCESS_CODE;
    }
}