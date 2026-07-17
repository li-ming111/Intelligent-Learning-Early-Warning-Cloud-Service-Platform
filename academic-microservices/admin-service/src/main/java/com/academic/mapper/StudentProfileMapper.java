package com.academic.mapper;

import com.academic.entity.StudentProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentProfileMapper extends BaseMapper<StudentProfile> {
    
    @Select("SELECT COUNT(*) FROM student_profile WHERE college_id = #{collegeId}")
    long countByCollegeId(Long collegeId);
}