package com.academic.mapper;

import com.academic.common.entity.AcademicWarning;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface WarningMapper extends BaseMapper<AcademicWarning> {

    @Select("SELECT warning_level, COUNT(*) as count FROM academic_warning " +
            "WHERE student_id IN (SELECT sp.id FROM student_profile sp " +
            "WHERE sp.class_id IN (SELECT id FROM classes WHERE counselor_id = #{counselorId})) " +
            "GROUP BY warning_level")
    List<Map<String, Object>> getWarningDistributionByCounselor(@Param("counselorId") Long counselorId);

    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m') as month, warning_level, COUNT(*) as count " +
            "FROM academic_warning " +
            "WHERE student_id IN (SELECT sp.id FROM student_profile sp " +
            "WHERE sp.class_id IN (SELECT id FROM classes WHERE counselor_id = #{counselorId})) " +
            "AND created_at >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
            "GROUP BY DATE_FORMAT(created_at, '%Y-%m'), warning_level " +
            "ORDER BY month ASC")
    List<Map<String, Object>> getWarningTrendByCounselor(@Param("counselorId") Long counselorId);

    @Select("SELECT AVG(TIMESTAMPDIFF(DAY, created_at, handled_at)) as avg_handle_days " +
            "FROM academic_warning " +
            "WHERE student_id IN (SELECT sp.id FROM student_profile sp " +
            "WHERE sp.class_id IN (SELECT id FROM classes WHERE counselor_id = #{counselorId})) " +
            "AND handled_at IS NOT NULL")
    Map<String, Object> getAvgHandleTimeByCounselor(@Param("counselorId") Long counselorId);
}