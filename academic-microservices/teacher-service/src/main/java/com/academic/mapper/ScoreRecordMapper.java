package com.academic.mapper;

import com.academic.common.entity.ScoreRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface ScoreRecordMapper {
    @Select("SELECT * FROM score_record WHERE id = #{id}")
    ScoreRecord selectById(Long id);

    @Select("SELECT * FROM score_record WHERE course_id = #{courseId} ORDER BY student_name")
    List<ScoreRecord> getScoresByCourseId(Long courseId);

    @Select("SELECT * FROM score_record WHERE course_id = #{courseId} AND teacher_id = #{teacherId} ORDER BY student_name")
    List<ScoreRecord> getScoresByCourseIdAndTeacherId(Long courseId, Long teacherId);
    
    @Select("SELECT * FROM score_record WHERE student_id = #{studentId}")
    List<ScoreRecord> getScoresByStudentId(String studentId);
    
    @Insert("INSERT INTO score_record(course_id, student_id, teacher_id, student_name, class_name, score, score_level, remark) VALUES(#{courseId}, #{studentId}, #{teacherId}, #{studentName}, #{className}, #{score}, #{scoreLevel}, #{remark})")
    int insertScore(ScoreRecord score);
    
    @Update("UPDATE score_record SET score = #{score}, score_level = #{scoreLevel}, remark = #{remark} WHERE id = #{id}")
    int updateScore(ScoreRecord score);
    
    @Delete("DELETE FROM score_record WHERE id = #{id}")
    int deleteScore(Long id);
    
    @Delete("DELETE FROM score_record WHERE id = #{id}")
    int deleteById(Long id);
    
    @Delete("<script>DELETE FROM score_record WHERE id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach></script>")
    int deleteByIds(List<Long> ids);
    
    @Select("SELECT sr.*, c.name as course_name, sp.student_id as student_no FROM score_record sr LEFT JOIN courses c ON sr.course_id = c.id LEFT JOIN student_profile sp ON sr.student_id = sp.id WHERE sr.class_name = #{className}")
    List<ScoreRecord> getScoresByClassName(String className);

    @Select("SELECT sr.*, c.name as course_name, sp.student_id as student_no FROM score_record sr LEFT JOIN courses c ON sr.course_id = c.id LEFT JOIN student_profile sp ON sr.student_id = sp.id WHERE sr.class_name = #{className} AND sr.teacher_id = #{teacherId}")
    List<ScoreRecord> getScoresByClassNameAndTeacherId(String className, Long teacherId);
    
    @Select("SELECT * FROM score_record WHERE teacher_id = #{teacherId} ORDER BY id DESC")
    List<ScoreRecord> getScoresByTeacherId(Long teacherId);

    @Select("SELECT * FROM score_record WHERE student_id = #{studentId} AND course_id = #{courseId}")
    ScoreRecord selectByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Select("SELECT sr.student_id, sr.student_name, sr.course_id, c.name AS course_name, c.credits, c.type, sr.score AS score_total, sr.score_level FROM score_record sr LEFT JOIN courses c ON sr.course_id = c.id WHERE sr.class_name = (SELECT name FROM classes WHERE id = #{classId})")
    List<Map<String, Object>> getScoresByClassId(Long classId);
}