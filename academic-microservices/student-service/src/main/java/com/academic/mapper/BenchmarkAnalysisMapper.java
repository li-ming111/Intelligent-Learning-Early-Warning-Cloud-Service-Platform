package com.academic.mapper;

import com.academic.entity.BenchmarkAnalysis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BenchmarkAnalysisMapper extends BaseMapper<BenchmarkAnalysis> {

    @Select("SELECT * FROM benchmark_analysis WHERE student_id = #{studentId} ORDER BY semester DESC LIMIT 1")
    BenchmarkAnalysis selectLatestByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT * FROM benchmark_analysis WHERE student_id = #{studentId} AND semester = #{semester}")
    BenchmarkAnalysis selectByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    @Select("SELECT * FROM benchmark_analysis WHERE student_id = #{studentId} ORDER BY semester ASC")
    List<BenchmarkAnalysis> selectHistoryByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT * FROM benchmark_analysis WHERE class_id = #{classId} AND semester = #{semester} ORDER BY class_rank ASC")
    List<BenchmarkAnalysis> selectClassComparison(@Param("classId") Long classId, @Param("semester") String semester);

    @Select("SELECT * FROM benchmark_analysis WHERE major_id = #{majorId} AND semester = #{semester} ORDER BY major_rank ASC")
    List<BenchmarkAnalysis> selectMajorComparison(@Param("majorId") Long majorId, @Param("semester") String semester);
}