package com.academic.mapper;

import com.academic.common.entity.Score;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    @Select("SELECT sr.id, sr.student_id, sr.course_id, sr.score AS score_total, sr.score_level, " +
            "sr.student_name, sr.class_name, sr.create_time AS created_at, " +
            "c.name AS course_name, c.credits, c.type " +
            "FROM score_record sr " +
            "LEFT JOIN courses c ON sr.course_id = c.id " +
            "WHERE sr.student_id IN (SELECT sp.id FROM student_profile sp " +
            "WHERE sp.class_id IN (SELECT id FROM classes WHERE counselor_id = #{counselorId})) " +
            "ORDER BY sr.create_time DESC")
    List<Map<String, Object>> getScoresByCounselorId(@Param("counselorId") Long counselorId);

    @Select("SELECT sr.id, sr.student_id, sr.course_id, sr.score AS score_total, sr.score_level, " +
            "sr.student_name, sr.class_name, sr.create_time AS created_at, " +
            "c.name AS course_name, c.credits, c.type " +
            "FROM score_record sr " +
            "LEFT JOIN courses c ON sr.course_id = c.id " +
            "WHERE sr.class_name = (SELECT name FROM classes WHERE id = #{classId}) " +
            "ORDER BY sr.create_time DESC")
    List<Map<String, Object>> getScoresByClassId(@Param("classId") Long classId);

    @Select("SELECT sr.id, sr.student_id, sr.course_id, sr.score AS score_total, sr.score_level, " +
            "sr.student_name, sr.class_name, sr.create_time AS created_at, " +
            "c.name AS course_name, c.credits, c.type " +
            "FROM score_record sr " +
            "LEFT JOIN courses c ON sr.course_id = c.id " +
            "WHERE sr.course_id = #{courseId} " +
            "AND sr.student_id IN (SELECT sp.id FROM student_profile sp " +
            "WHERE sp.class_id IN (SELECT id FROM classes WHERE counselor_id = #{counselorId})) " +
            "ORDER BY sr.create_time DESC")
    List<Map<String, Object>> getScoresByCourseIdAndCounselor(@Param("courseId") Long courseId,
                                                              @Param("counselorId") Long counselorId);

    @Select("SELECT sr.id, sr.student_id, sr.course_id, sr.score AS score_total, sr.score_level, " +
            "sr.student_name, sr.class_name, sr.create_time AS created_at, " +
            "c.name AS course_name, c.credits " +
            "FROM score_record sr " +
            "LEFT JOIN courses c ON sr.course_id = c.id " +
            "WHERE sr.student_id = #{studentId} " +
            "ORDER BY sr.create_time ASC")
    List<Map<String, Object>> getScoresByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT c.id, c.name, COUNT(sr.id) AS score_count, " +
            "AVG(sr.score) AS avg_score " +
            "FROM classes c " +
            "LEFT JOIN student_profile sp ON c.id = sp.class_id " +
            "LEFT JOIN score_record sr ON sp.id = sr.student_id " +
            "WHERE c.counselor_id = #{counselorId} " +
            "GROUP BY c.id, c.name " +
            "ORDER BY avg_score DESC")
    List<Map<String, Object>> getClassScoreStatsByCounselorId(@Param("counselorId") Long counselorId);
}
