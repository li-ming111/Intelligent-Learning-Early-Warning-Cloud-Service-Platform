package com.academic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("support_resource")
public class SupportResource {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private String type;

    private String category;

    private String content;

    private String url;

    private String filePath;

    private Long fileSize;

    private String fileType;

    private String author;

    private Integer views;

    private Integer downloads;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}