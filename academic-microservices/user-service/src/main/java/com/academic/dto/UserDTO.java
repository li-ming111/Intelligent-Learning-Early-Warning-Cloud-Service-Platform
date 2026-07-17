package com.academic.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户数据传输对象
 */
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private Integer role;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
