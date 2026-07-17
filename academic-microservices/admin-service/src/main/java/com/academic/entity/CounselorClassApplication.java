package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("counselor_class_application")
public class CounselorClassApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long counselorId;
    private Long classId;
    private String reason;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String className;
    private String counselorName;
    private Integer applicationType;
}
