package com.academic.mapper;

import com.academic.entity.Classes;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 班级Mapper
 */
@Mapper
public interface ClassesMapper extends BaseMapper<Classes> {

    @Select("SELECT c.id, c.name, c.college_id, c.major_id, c.teacher_id, " +
            "COALESCE(tp.name, '') AS teacherName, " +
            "COUNT(sp.id) AS studentCount " +
            "FROM classes c " +
            "LEFT JOIN student_profile sp ON sp.class_id = c.id " +
            "LEFT JOIN teacher_profile tp ON tp.user_id = c.teacher_id " +
            "GROUP BY c.id, c.name, c.college_id, c.major_id, c.teacher_id, tp.name " +
            "ORDER BY c.id")
    List<Map<String, Object>> selectAllWithStudentCount();

    @Select("SELECT id, name FROM classes WHERE college_id = #{collegeId} ORDER BY id")
    List<Map<String, Object>> getClassesByCollegeId(Long collegeId);

    @Select("SELECT id, name FROM classes WHERE counselor_id = #{counselorId} ORDER BY id")
    List<Map<String, Object>> getClassesByCounselorIdMap(Long counselorId);
}
