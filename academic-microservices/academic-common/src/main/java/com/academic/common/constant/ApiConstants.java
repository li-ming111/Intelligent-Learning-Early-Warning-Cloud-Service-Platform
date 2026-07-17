package com.academic.common.constant;

public final class ApiConstants {
    
    public static final int SUCCESS_CODE = 200;
    public static final int BAD_REQUEST_CODE = 400;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int FORBIDDEN_CODE = 403;
    public static final int NOT_FOUND_CODE = 404;
    public static final int SERVER_ERROR_CODE = 500;
    
    public static final String SUCCESS_MESSAGE = "success";
    public static final String BAD_REQUEST_MESSAGE = "Bad Request";
    public static final String UNAUTHORIZED_MESSAGE = "Unauthorized";
    public static final String FORBIDDEN_MESSAGE = "Forbidden";
    public static final String NOT_FOUND_MESSAGE = "Not Found";
    public static final String SERVER_ERROR_MESSAGE = "Internal Server Error";
    
    public static final String DEFAULT_PASSWORD = "123456";
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 32;
    
    public static final int USER_STATUS_PENDING = 0;   // 待审批
    public static final int USER_STATUS_ACTIVE = 1;
    public static final int USER_STATUS_LOCKED = 2;
    public static final int USER_STATUS_INACTIVE = 3;   // 已禁用
    
    public static final int ROLE_STUDENT = 1;
    public static final int ROLE_TEACHER = 2;
    public static final int ROLE_COUNSELOR = 3;
    public static final int ROLE_ADMIN = 4;
    
    public static final int PAGE_DEFAULT = 1;
    public static final int SIZE_DEFAULT = 10;
    public static final int SIZE_MAX = 100;
    
    public static final long TOKEN_EXPIRY = 24 * 60 * 60 * 1000;
    public static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60 * 1000;
    
    private ApiConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}