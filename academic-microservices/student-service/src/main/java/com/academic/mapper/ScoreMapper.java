package com.academic.mapper;

import com.academic.common.entity.Score;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {
    
    @Select("SELECT s.id, s.course_id, s.student_id, s.semester, s.year, "
          + "s.score_total, s.grade, s.grade_point, s.created_at, s.updated_at, "
          + "c.name as course_name, c.credits as credits "
          + "FROM scores s LEFT JOIN courses c ON s.course_id = c.id "
          + "WHERE s.student_id = #{studentId} AND s.year IS NOT NULL AND s.semester IS NOT NULL "
          + "ORDER BY s.created_at DESC")
    @Results({@Result(column = "course_name", property = "courseName"), @Result(column = "credits", property = "credits")})
    List<Score> selectByStudentId(String studentId);
    
    @Select("<script>"
          + "SELECT s.id, s.course_id, s.student_id, s.semester, s.year, "
          + "s.score_total, s.grade, s.grade_point, s.created_at, s.updated_at, "
          + "c.name as course_name, c.credits as credits "
          + "FROM scores s LEFT JOIN courses c ON s.course_id = c.id "
          + "WHERE s.student_id = #{studentId} "
          + "<if test='year != null'> AND s.year = #{year} </if>"
          + "<if test='semester != null'> AND s.semester = #{semester} </if>"
          + "ORDER BY s.created_at DESC"
          + "</script>")
    @Results({@Result(column = "course_name", property = "courseName"), @Result(column = "credits", property = "credits")})
    List<Score> selectByStudentIdAndSemester(@org.apache.ibatis.annotations.Param("studentId") String studentId,
                                               @org.apache.ibatis.annotations.Param("year") Integer year,
                                               @org.apache.ibatis.annotations.Param("semester") Integer semester);

    /** 计算班级均分：同班级所有学生的加权GPA均值 */
    @Select("SELECT COALESCE(SUM(sr.score * c.credits) / NULLIF(SUM(c.credits), 0), 0) / 20 FROM score_record sr JOIN courses c ON sr.course_id = c.id WHERE sr.student_id IN (SELECT id FROM student_profile WHERE class_id = #{classId})")
    Double calcClassAvgGpa(@org.apache.ibatis.annotations.Param("classId") Long classId, @org.apache.ibatis.annotations.Param("semester") String semester);

    /** 计算专业均分：同专业所有学生的加权GPA均值 */
    @Select("SELECT COALESCE(SUM(sr.score * c.credits) / NULLIF(SUM(c.credits), 0), 0) / 20 FROM score_record sr JOIN courses c ON sr.course_id = c.id WHERE sr.student_id IN (SELECT id FROM student_profile WHERE major_id = #{majorId})")
    Double calcMajorAvgGpa(@org.apache.ibatis.annotations.Param("majorId") Long majorId, @org.apache.ibatis.annotations.Param("semester") String semester);

    /** 班级排名：该学期加权分数高于当前学生的同班同学数 + 1 */
    @Select("SELECT COUNT(*) + 1 FROM (SELECT sp.id, SUM(sr.score * c.credits) / NULLIF(SUM(c.credits), 0) AS gpa_score FROM score_record sr JOIN courses c ON sr.course_id = c.id JOIN student_profile sp ON sr.student_id = sp.id WHERE sp.class_id = #{classId} GROUP BY sp.id) t WHERE t.gpa_score > (SELECT SUM(s2.score * c2.credits) / NULLIF(SUM(c2.credits), 0) FROM score_record s2 JOIN courses c2 ON s2.course_id = c2.id WHERE s2.student_id = #{profileId})")
    Integer calcClassRank(@org.apache.ibatis.annotations.Param("profileId") Long profileId, @org.apache.ibatis.annotations.Param("classId") Long classId, @org.apache.ibatis.annotations.Param("semester") String semester);

    /** 班级总人数（有成绩的） */
    @Select("SELECT COUNT(DISTINCT sr.student_id) FROM score_record sr JOIN student_profile sp ON sr.student_id = sp.id WHERE sp.class_id = #{classId}")
    Integer countClassStudents(@org.apache.ibatis.annotations.Param("classId") Long classId, @org.apache.ibatis.annotations.Param("semester") String semester);
}
