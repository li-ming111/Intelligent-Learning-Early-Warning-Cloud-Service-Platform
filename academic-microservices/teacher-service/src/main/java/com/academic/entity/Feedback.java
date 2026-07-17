package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long teacherId;
    
    private Long studentId;
    
    private String category;
    
    private String content;
    
    private String reply;
    
    private Integer status;
    
    private Date createTime;
    
    private Date replyTime;
    
    private String studentName;
}