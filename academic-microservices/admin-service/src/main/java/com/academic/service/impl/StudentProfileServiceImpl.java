package com.academic.service.impl;

import com.academic.entity.StudentProfile;
import com.academic.mapper.StudentProfileMapper;
import com.academic.service.StudentProfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentProfileServiceImpl extends ServiceImpl<StudentProfileMapper, StudentProfile> implements StudentProfileService {

    @Override
    public long countByCollegeId(Long collegeId) {
        return baseMapper.countByCollegeId(collegeId);
    }
}