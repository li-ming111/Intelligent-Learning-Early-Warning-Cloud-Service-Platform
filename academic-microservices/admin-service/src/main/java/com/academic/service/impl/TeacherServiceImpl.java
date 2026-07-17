package com.academic.service.impl;

import com.academic.common.entity.TeacherProfile;
import com.academic.mapper.TeacherMapper;
import com.academic.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 教师服务实现类
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, TeacherProfile> implements TeacherService {

    @Override
    public TeacherProfile getByUserId(Long userId) {
        LambdaQueryWrapper<TeacherProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherProfile::getUserId, userId);
        return baseMapper.selectOne(wrapper);
    }
}
