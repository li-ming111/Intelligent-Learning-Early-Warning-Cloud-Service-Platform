package com.academic.service.impl;

import com.academic.common.entity.CommunicationLog;
import com.academic.mapper.CommunicationLogMapper;
import com.academic.service.CommunicationLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 沟通记录服务实现类
 */
@Service
public class CommunicationLogServiceImpl extends ServiceImpl<CommunicationLogMapper, CommunicationLog> implements CommunicationLogService {

    @Override
    public List<CommunicationLog> getLogsByStudentId(Long studentId) {
        LambdaQueryWrapper<CommunicationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunicationLog::getStudentId, studentId);
        wrapper.orderByDesc(CommunicationLog::getCreatedAt);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<CommunicationLog> getLogsByCounselorId(Long counselorId) {
        LambdaQueryWrapper<CommunicationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunicationLog::getCounselorId, counselorId);
        wrapper.orderByDesc(CommunicationLog::getCreatedAt);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean createLog(CommunicationLog log) {
        // 设置创建时间和更新时间
        log.setCreatedAt(LocalDateTime.now());
        log.setUpdatedAt(LocalDateTime.now());

        return save(log);
    }

    @Override
    public boolean updateLog(CommunicationLog log) {
        // 设置更新时间
        log.setUpdatedAt(LocalDateTime.now());
        return updateById(log);
    }

    @Override
    public boolean deleteLog(Long id) {
        return removeById(id);
    }
}
