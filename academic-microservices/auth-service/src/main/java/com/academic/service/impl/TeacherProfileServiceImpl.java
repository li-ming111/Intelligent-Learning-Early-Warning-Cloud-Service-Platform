package com.academic.service.impl;

import com.academic.common.entity.TeacherProfile;
import com.academic.mapper.TeacherProfileMapper;
import com.academic.service.TeacherProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherProfileServiceImpl implements TeacherProfileService {
    
    @Autowired
    private TeacherProfileMapper teacherProfileMapper;
    
    @Override
    public void save(TeacherProfile teacherProfile) {
        teacherProfileMapper.insert(teacherProfile);
    }
}