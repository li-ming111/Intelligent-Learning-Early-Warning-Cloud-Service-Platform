package com.academic.domain.model;

import lombok.Data;

/**
 * 用户领域模型
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String role;
    private Integer status;
    private String createdAt;
    private String updatedAt;
}