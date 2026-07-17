package com.academic.service;

import com.academic.entity.StudentProfile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface StudentProfileService extends IService<StudentProfile> {
    long countByCollegeId(Long collegeId);
}