package com.academic.service;

import com.academic.common.entity.ScoreAuditLog;
import com.academic.common.entity.ScoreRecord;
import com.academic.mapper.ScoreAuditLogMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ScoreAuditLogService extends ServiceImpl<ScoreAuditLogMapper, ScoreAuditLog> {

    public List<ScoreAuditLog> getByTeacherId(Long teacherId) {
        return baseMapper.findByTeacherId(teacherId);
    }

    /**
     * 记录一次成绩修改
     */
    public void record(ScoreRecord oldRecord, ScoreRecord newRecord, String reason, String modifiedBy) {
        ScoreAuditLog log = new ScoreAuditLog();
        log.setStudentId(newRecord.getStudentId());
        log.setStudentName(newRecord.getStudentName());
        log.setCourseId(newRecord.getCourseId());
        log.setTeacherId(newRecord.getTeacherId());
        log.setOldScore(BigDecimal.valueOf(oldRecord.getScore()));
        log.setNewScore(BigDecimal.valueOf(newRecord.getScore()));
        log.setReason(reason);
        log.setModifiedBy(modifiedBy);
        baseMapper.insert(log);
    }
}