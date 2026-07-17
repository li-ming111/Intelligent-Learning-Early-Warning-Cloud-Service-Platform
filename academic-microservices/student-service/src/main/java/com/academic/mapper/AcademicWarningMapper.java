package com.academic.mapper;

import com.academic.common.entity.AcademicWarning;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AcademicWarningMapper extends BaseMapper<AcademicWarning> {
    
    @Select("SELECT * FROM academic_warning WHERE student_id = #{studentId}")
    List<AcademicWarning> selectByStudentId(String studentId);
    
    @Select("SELECT * FROM academic_warning WHERE student_id = #{studentId} AND status = 0")
    List<AcademicWarning> selectPendingByStudentId(String studentId);
}
