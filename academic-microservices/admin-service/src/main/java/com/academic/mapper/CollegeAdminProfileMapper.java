package com.academic.mapper;

import com.academic.common.entity.CollegeAdminProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CollegeAdminProfileMapper extends BaseMapper<CollegeAdminProfile> {

    @Select("SELECT cap.*, c.name as college_name FROM college_admin_profile cap LEFT JOIN colleges c ON cap.college_id = c.id WHERE cap.user_id = #{userId}")
    java.util.Map<String, Object> selectByUserId(Long userId);
}
