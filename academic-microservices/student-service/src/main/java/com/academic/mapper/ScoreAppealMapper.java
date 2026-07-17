package com.academic.mapper;

import com.academic.entity.ScoreAppeal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScoreAppealMapper extends BaseMapper<ScoreAppeal> {

    @Select("SELECT * FROM score_appeals WHERE student_id = #{studentId}")
    List<ScoreAppeal> selectByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT * FROM score_appeals WHERE student_id = #{studentId} AND status = 0")
    List<ScoreAppeal> selectPendingByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT * FROM score_appeals WHERE student_id = #{studentId} AND status = #{status}")
    List<ScoreAppeal> selectByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM score_appeals WHERE student_id = #{studentId}")
    int countByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT COUNT(*) FROM score_appeals WHERE student_id = #{studentId} AND status = 1")
    int countSuccessByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT reason, COUNT(*) as cnt FROM score_appeals WHERE student_id = #{studentId} GROUP BY reason")
    List<Map<String, Object>> countByReason(@Param("studentId") Long studentId);

    @Update("UPDATE score_appeals SET status = 3, updated_at = NOW() WHERE id = #{id} AND student_id = #{studentId}")
    int withdrawAppeal(@Param("id") Long id, @Param("studentId") Long studentId);
}