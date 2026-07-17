package com.academic.service.impl;

import com.academic.common.entity.CounselorProfile;
import com.academic.mapper.CounselorMapper;
import com.academic.service.CounselorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 辅导员服务实现类
 */
@Service
public class CounselorServiceImpl extends ServiceImpl<CounselorMapper, CounselorProfile> implements CounselorService {

    @Override
    public CounselorProfile getByUserId(Long userId) {
        LambdaQueryWrapper<CounselorProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CounselorProfile::getUserId, userId);
        return baseMapper.selectOne(wrapper);
    }
}
