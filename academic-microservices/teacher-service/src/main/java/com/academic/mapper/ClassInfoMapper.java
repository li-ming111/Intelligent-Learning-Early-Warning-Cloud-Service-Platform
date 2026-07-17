package com.academic.mapper;

import com.academic.entity.ClassInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 班级Mapper接口
 */
@Mapper
public interface ClassInfoMapper extends BaseMapper<ClassInfo> {

    /**
     * 根据教师ID获取管理的班级
     */
    @Select("SELECT c.id, c.name, c.college_id as collegeId, c.major_id as majorId, COUNT(s.id) as studentCount FROM classes c LEFT JOIN student_profile s ON c.id = s.class_id WHERE c.teacher_id = #{teacherId} GROUP BY c.id")
    List<Map<String, Object>> getClassesByTeacherIdMap(@Param("teacherId") Long teacherId);

    /**
     * 搜索班级（按名称）
     */
    @Select("SELECT c.id, c.name, c.major_id as majorId, COUNT(s.id) as studentCount, CASE WHEN c.teacher_id IS NOT NULL THEN 1 ELSE 0 END as hasTeacher FROM classes c LEFT JOIN student_profile s ON c.id = s.class_id WHERE c.name LIKE CONCAT('%', #{keyword}, '%') GROUP BY c.id")
    List<Map<String, Object>> searchClassesMap(@Param("keyword") String keyword);

    /**
     * 获取所有班级
     */
    @Select("SELECT c.id, c.name, c.major_id as majorId, COUNT(s.id) as studentCount, CASE WHEN c.teacher_id IS NOT NULL THEN 1 ELSE 0 END as hasTeacher FROM classes c LEFT JOIN student_profile s ON c.id = s.class_id GROUP BY c.id")
    List<Map<String, Object>> getAllClassesMap();

    /**
     * 根据班级ID获取学生列表
     */
    @Select("SELECT sp.student_id as studentId, u.name as studentName FROM student_profile sp JOIN users u ON sp.user_id = u.id WHERE sp.class_id = #{classId}")
    List<Map<String, Object>> getStudentsByClassIdMap(@Param("classId") Long classId);

    /**
     * 根据班级名称获取辅导员信息
     */
    @Select("SELECT c.counselor_id as counselorId, cp.name as counselorName, cp.phone as counselorPhone FROM classes c LEFT JOIN counselor_profiles cp ON c.counselor_id = cp.id WHERE c.name = #{className}")
    Map<String, Object> getCounselorByClassName(@Param("className") String className);
}
