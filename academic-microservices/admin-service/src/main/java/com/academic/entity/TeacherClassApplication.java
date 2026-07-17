package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("teacher_class_application")
public class TeacherClassApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long teacherId;
    
    private Long classId;
    
    private String reason;
    
    private Integer status;
    
    private Date createTime;
    
    private Date updateTime;
    
    private String className;
    
    private String teacherName;
    
    private String teacherUsername;
    
    private String rejectReason;
    
    /**
     * 申请类型：0-申请管理，1-解除管理
     */
    private Integer applicationType;
}
