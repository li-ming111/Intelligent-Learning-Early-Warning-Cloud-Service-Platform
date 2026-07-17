package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("communication_record")
public class CommunicationRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long teacherId;
    
    private Long studentId;
    
    private String content;
    
    private String type;
    
    private Date createTime;
    
    private String studentName;
}