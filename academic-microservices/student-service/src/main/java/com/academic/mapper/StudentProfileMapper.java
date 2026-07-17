package com.academic.mapper;

import com.academic.entity.StudentProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentProfileMapper extends BaseMapper<StudentProfile> {
    
    @Select("SELECT * FROM student_profile WHERE student_id = #{studentId}")
    StudentProfile selectByStudentId(String studentId);

    @Select("SELECT * FROM student_profile WHERE user_id = #{userId}")
    StudentProfile selectByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM student_profile WHERE college_id = #{collegeId}")
    long countByCollegeId(Long collegeId);
    
    @Select("SELECT * FROM student_profile WHERE college_id = #{collegeId}")
    java.util.List<StudentProfile> selectByCollegeId(Long collegeId);
}
