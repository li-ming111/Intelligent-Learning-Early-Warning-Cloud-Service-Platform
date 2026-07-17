package com.academic.mapper;

import com.academic.common.entity.TeacherProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 教师个人资料Mapper
 */
@Mapper
public interface TeacherProfileMapper extends BaseMapper<TeacherProfile> {
    
    @Select("SELECT COUNT(*) FROM teacher_profile WHERE college_id = #{collegeId}")
    long countByCollegeId(Long collegeId);
}
