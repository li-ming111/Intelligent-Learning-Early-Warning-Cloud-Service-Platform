package com.academic.mapper;

import com.academic.common.entity.AcademicWarning;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AcademicWarningMapper extends BaseMapper<AcademicWarning> {

    @Select("SELECT aw.* FROM academic_warning aw INNER JOIN student_profile sp ON aw.student_id = sp.id WHERE sp.class_id = #{classId} ORDER BY aw.created_at DESC")
    List<AcademicWarning> findByClassId(Long classId);

    @Select("SELECT COUNT(*) FROM academic_warning aw INNER JOIN student_profile sp ON aw.student_id = sp.id WHERE sp.class_id = #{classId} AND aw.warning_level >= 3")
    int countHighByClassId(Long classId);
}
