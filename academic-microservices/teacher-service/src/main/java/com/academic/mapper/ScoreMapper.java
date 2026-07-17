package com.academic.mapper;

import com.academic.common.entity.Score;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * scores 表 Mapper（同步写入用）
 */
@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    /** 按学生+课程查重，避免重复插入 */
    @Select("SELECT * FROM scores WHERE student_id = #{studentId} AND course_id = #{courseId}")
    Score selectByStudentAndCourse(@Param("studentId") Long studentId,
                                   @Param("courseId") Long courseId);
}
