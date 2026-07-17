package com.academic.mapper;

import com.academic.entity.AssistanceEvaluation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AssistanceEvaluationMapper extends BaseMapper<AssistanceEvaluation> {

    @Select("SELECT * FROM assistance_evaluations WHERE plan_id = #{planId} ORDER BY created_at DESC")
    List<AssistanceEvaluation> selectByPlanId(@Param("planId") Long planId);

    @Select("SELECT ae.* FROM assistance_evaluations ae " +
            "INNER JOIN assistance_plans ap ON ae.plan_id = ap.id " +
            "WHERE ap.student_id = #{studentId} ORDER BY ae.created_at DESC")
    List<AssistanceEvaluation> selectByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT AVG(score) FROM assistance_evaluations ae " +
            "INNER JOIN assistance_plans ap ON ae.plan_id = ap.id " +
            "WHERE ap.student_id = #{studentId}")
    Double avgScoreByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT COUNT(*) FROM assistance_evaluations ae " +
            "INNER JOIN assistance_plans ap ON ae.plan_id = ap.id " +
            "WHERE ap.student_id = #{studentId}")
    int countByStudentId(@Param("studentId") Long studentId);
}