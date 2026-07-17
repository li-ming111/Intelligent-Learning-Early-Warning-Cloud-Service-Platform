package com.academic.service.impl;

import com.academic.common.entity.AssistancePlan;
import com.academic.mapper.AssistancePlanMapper;
import com.academic.service.AssistancePlanService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帮扶计划服务实现类
 */
@Service
public class AssistancePlanServiceImpl extends ServiceImpl<AssistancePlanMapper, AssistancePlan> implements AssistancePlanService {

    @Override
    public List<AssistancePlan> getPlansByStudentId(Long studentId) {
        LambdaQueryWrapper<AssistancePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssistancePlan::getStudentId, studentId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean createPlan(AssistancePlan plan) {
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        plan.setStatus(0);
        return save(plan);
    }

    @Override
    public boolean updatePlan(AssistancePlan plan) {
        plan.setUpdatedAt(LocalDateTime.now());
        return updateById(plan);
    }

    @Override
    public boolean deletePlan(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updatePlanStatus(Long id, String status) {
        AssistancePlan plan = getById(id);
        if (plan == null) {
            return false;
        }
        plan.setStatus(Integer.parseInt(status));
        plan.setUpdatedAt(LocalDateTime.now());
        return updateById(plan);
    }

    @Override
    public boolean updatePlanProgress(Long id, java.math.BigDecimal progressPercentage) {
        return true;
    }
}
