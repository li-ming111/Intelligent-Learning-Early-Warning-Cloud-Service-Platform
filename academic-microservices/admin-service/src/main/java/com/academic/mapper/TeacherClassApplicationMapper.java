package com.academic.mapper;

import com.academic.entity.TeacherClassApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TeacherClassApplicationMapper extends BaseMapper<TeacherClassApplication> {

    @Select("SELECT * FROM teacher_class_application ORDER BY create_time DESC")
    List<TeacherClassApplication> getAllApplications();

    @Select("SELECT * FROM teacher_class_application WHERE status = #{status} ORDER BY create_time DESC")
    List<TeacherClassApplication> getApplicationsByStatus(@Param("status") Integer status);

    @Update("UPDATE teacher_class_application SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    @Update("UPDATE teacher_class_application SET status = #{status}, reject_reason = #{rejectReason}, update_time = NOW() WHERE id = #{id}")
    int updateStatusWithReason(@Param("id") Long id, @Param("status") Integer status, @Param("rejectReason") String rejectReason);
}
