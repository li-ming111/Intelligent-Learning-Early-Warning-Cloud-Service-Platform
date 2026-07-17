package com.academic.service;

import com.academic.entity.ReportTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ReportTemplateService extends IService<ReportTemplate> {
    
    List<ReportTemplate> getAllTemplates();
    
    ReportTemplate getTemplateById(Long id);
    
    ReportTemplate uploadTemplate(String name, String description, String filePath, String fileName, Long fileSize, String fileType);
    
    void deleteTemplate(Long id);
}