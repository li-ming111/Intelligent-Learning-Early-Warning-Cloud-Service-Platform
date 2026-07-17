package com.academic.mapper;

import com.academic.common.entity.CounselorProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CounselorProfileMapper extends BaseMapper<CounselorProfile> {
    
    @Select("SELECT COUNT(*) FROM counselor_profiles WHERE college_id = #{collegeId}")
    long countByCollegeId(Long collegeId);
}