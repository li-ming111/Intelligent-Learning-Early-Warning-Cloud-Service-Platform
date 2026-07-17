package com.academic.exception;

/**
 * 资源未找到异常
 * 用于表示请求的资源不存在
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(String.format("%s not found with id: %d", resourceType, id));
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s not found: %s", resourceType, identifier));
    }
}
