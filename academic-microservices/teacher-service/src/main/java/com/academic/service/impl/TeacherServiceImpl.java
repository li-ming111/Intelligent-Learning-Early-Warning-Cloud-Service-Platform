package com.academic.service.impl;

import com.academic.common.entity.TeacherProfile;
import com.academic.mapper.TeacherProfileMapper;
import com.academic.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 教师服务实现类
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherProfileMapper, TeacherProfile> implements TeacherService {

    @Override
    public TeacherProfile getByUserId(Long userId) {
        LambdaQueryWrapper<TeacherProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherProfile::getUserId, userId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public boolean register(TeacherProfile teacherProfile) {
        // 设置创建时间和更新时间
        teacherProfile.setCreatedAt(LocalDateTime.now());
        teacherProfile.setUpdatedAt(LocalDateTime.now());

        return save(teacherProfile);
    }

    @Override
    public boolean updateProfile(TeacherProfile teacherProfile) {
        // 设置更新时间
        teacherProfile.setUpdatedAt(LocalDateTime.now());
        return updateById(teacherProfile);
    }

    @Override
    public boolean deleteProfile(Long id) {
        return removeById(id);
    }

    @Override
    public long countByCollege(Long collegeId) {
        LambdaQueryWrapper<TeacherProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherProfile::getCollegeId, collegeId);
        return baseMapper.selectCount(wrapper);
    }
}
