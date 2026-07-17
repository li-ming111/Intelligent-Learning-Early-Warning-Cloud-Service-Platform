package com.academic.service.impl;

import com.academic.common.entity.CounselorProfile;
import com.academic.mapper.CounselorProfileMapper;
import com.academic.service.CounselorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CounselorProfileServiceImpl implements CounselorProfileService {
    
    @Autowired
    private CounselorProfileMapper counselorProfileMapper;
    
    @Override
    public void save(CounselorProfile counselorProfile) {
        counselorProfileMapper.insert(counselorProfile);
    }
}