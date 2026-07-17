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
     * 根据辅导员ID获取管理的班级
     */
    @Select("SELECT c.id, c.name, c.college_id as collegeId, c.major_id as majorId, COUNT(sp.id) as studentCount FROM classes c LEFT JOIN student_profile sp ON c.id = sp.class_id WHERE c.counselor_id = #{counselorId} GROUP BY c.id")
    List<Map<String, Object>> getClassesByCounselorIdMap(@Param("counselorId") Long counselorId);

    /**
     * 搜索班级（按名称）
     */
    @Select("SELECT c.id, c.name, c.major_id as majorId, COUNT(sp.id) as studentCount, CASE WHEN c.counselor_id IS NOT NULL THEN 1 ELSE 0 END as hasCounselor FROM classes c LEFT JOIN student_profile sp ON c.id = sp.class_id WHERE c.name LIKE CONCAT('%', #{keyword}, '%') GROUP BY c.id")
    List<Map<String, Object>> searchClassesMap(@Param("keyword") String keyword);

    /**
     * 获取所有班级
     */
    @Select("SELECT c.id, c.name, c.major_id as majorId, COUNT(sp.id) as studentCount, CASE WHEN c.counselor_id IS NOT NULL THEN 1 ELSE 0 END as hasCounselor FROM classes c LEFT JOIN student_profile sp ON c.id = sp.class_id GROUP BY c.id")
    List<Map<String, Object>> getAllClassesMap();

    /**
     * 获取无辅导员的班级
     */
    @Select("SELECT c.id, c.name, c.major_id as majorId, COUNT(sp.id) as studentCount, 0 as hasCounselor FROM classes c LEFT JOIN student_profile sp ON c.id = sp.class_id WHERE c.counselor_id IS NULL OR c.counselor_id = 0 GROUP BY c.id")
    List<Map<String, Object>> getClassesWithoutCounselor();

    /**
     * 根据班级ID获取学生列表
     */
    @Select("SELECT sp.student_id as studentId, u.name as studentName, sp.gender, sp.phone FROM student_profile sp JOIN users u ON sp.user_id = u.id WHERE sp.class_id = #{classId}")
    List<Map<String, Object>> getStudentsByClassIdMap(@Param("classId") Long classId);
}
