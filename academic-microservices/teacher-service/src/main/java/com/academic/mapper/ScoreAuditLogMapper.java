package com.academic.mapper;

import com.academic.common.entity.ScoreAuditLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScoreAuditLogMapper extends BaseMapper<ScoreAuditLog> {

    @Select("SELECT * FROM score_audit_log WHERE teacher_id = #{teacherId} ORDER BY modified_at DESC")
    List<ScoreAuditLog> findByTeacherId(Long teacherId);
}