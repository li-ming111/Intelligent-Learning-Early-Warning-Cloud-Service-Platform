package com.academic.service;

import com.academic.common.entity.AssistancePlan;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 帮扶计划服务接口
 */
public interface AssistancePlanService extends IService<AssistancePlan> {
    /**
     * 根据学生ID获取帮扶计划列表
     * @param studentId 学生ID
     * @return 帮扶计划列表
     */
    List<AssistancePlan> getPlansByStudentId(Long studentId);

    /**
     * 创建帮扶计划
     * @param plan 帮扶计划
     * @return 是否创建成功
     */
    boolean createPlan(AssistancePlan plan);

    /**
     * 更新帮扶计划
     * @param plan 帮扶计划
     * @return 是否更新成功
     */
    boolean updatePlan(AssistancePlan plan);

    /**
     * 删除帮扶计划
     * @param id 帮扶计划ID
     * @return 是否删除成功
     */
    boolean deletePlan(Long id);

    /**
     * 更新帮扶计划状态
     * @param id 帮扶计划ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updatePlanStatus(Long id, String status);

    /**
     * 更新帮扶计划进度
     * @param id 帮扶计划ID
     * @param progressPercentage 进度百分比
     * @return 是否更新成功
     */
    boolean updatePlanProgress(Long id, BigDecimal progressPercentage);
}
