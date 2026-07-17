package com.academic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 成绩记录只读 Mapper（用于定时任务查询学生成绩）
 */
@Mapper
public interface ScoreRecordReadMapper {

    /**
     * 查询所有有成绩低于阈值的学号和姓名
     */
    @Select("SELECT sp.id as studentId, sp.name as studentName, " +
            "sr.course_id as courseId, c.name as courseName, " +
            "sr.score as score, sr.class_name as className " +
            "FROM score_record sr " +
            "LEFT JOIN student_profile sp ON sr.student_id = sp.user_id " +
            "LEFT JOIN courses c ON sr.course_id = c.id " +
            "WHERE sr.score < #{threshold} " +
            "ORDER BY sr.score ASC")
    List<Map<String, Object>> findLowScoreStudents(@Param("threshold") Double threshold);

    /**
     * 统计每个学生的挂科数量
     */
    @Select("SELECT sp.id as studentId, sp.name as studentName, " +
            "COUNT(*) as failCount, AVG(sr.score) as avgScore " +
            "FROM score_record sr " +
            "LEFT JOIN student_profile sp ON sr.student_id = sp.user_id " +
            "WHERE sr.score < 60 " +
            "GROUP BY sp.id, sp.name " +
            "HAVING COUNT(*) >= #{failThreshold}")
    List<Map<String, Object>> findStudentsWithMultipleFailures(@Param("failThreshold") int failThreshold);

    /**
     * 获取已存在预警的学生ID列表（避免重复生成）
     */
    @Select("SELECT DISTINCT student_id FROM academic_warning WHERE status = 0")
    List<Long> findExistingWarningStudentIds();
}
