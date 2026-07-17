package com.academic.mapper;

import com.academic.entity.CounselorClassApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CounselorClassApplicationMapper extends BaseMapper<CounselorClassApplication> {

    @Select("SELECT * FROM counselor_class_application WHERE status = 0 ORDER BY create_time DESC")
    List<CounselorClassApplication> getPendingApplications();

    @Select("SELECT * FROM counselor_class_application WHERE status = #{status} ORDER BY create_time DESC")
    List<CounselorClassApplication> getApplicationsByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM counselor_class_application ORDER BY create_time DESC")
    List<Map<String, Object>> getAllApplicationsRaw();
}
