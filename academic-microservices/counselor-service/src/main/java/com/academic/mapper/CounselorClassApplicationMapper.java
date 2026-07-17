package com.academic.mapper;

import com.academic.entity.CounselorClassApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 辅导员班级申请Mapper接口
 */
@Mapper
public interface CounselorClassApplicationMapper extends BaseMapper<CounselorClassApplication> {

    /**
     * 根据辅导员ID获取申请记录
     */
    @Select("SELECT * FROM counselor_class_application WHERE counselor_id = #{counselorId} ORDER BY create_time DESC")
    List<CounselorClassApplication> getByCounselorId(@Param("counselorId") Long counselorId);

    /**
     * 获取所有待审核的申请
     */
    @Select("SELECT * FROM counselor_class_application WHERE status = 0 ORDER BY create_time DESC")
    List<CounselorClassApplication> getPendingApplications();

    /**
     * 更新申请状态
     */
    @Select("UPDATE counselor_class_application SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
