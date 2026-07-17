package com.academic.service;

import com.academic.common.entity.Score;
import com.academic.common.entity.ScoreRecord;
import com.academic.mapper.ScoreRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ScoreRecordService {
    @Autowired
    private ScoreRecordMapper scoreRecordMapper;
    @Autowired
    private com.academic.mapper.ScoreMapper scoreMapper;
    @Autowired
    private ScoreAuditLogService auditLogService;
    
    public List<ScoreRecord> getScoresByCourseId(Long courseId) {
        return scoreRecordMapper.getScoresByCourseId(courseId);
    }
    
    public List<ScoreRecord> getScoresByCourseIdAndTeacherId(Long courseId, Long teacherId) {
        return scoreRecordMapper.getScoresByCourseIdAndTeacherId(courseId, teacherId);
    }
    
    public ScoreRecord getById(Long id) {
        return scoreRecordMapper.selectById(id);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void saveScores(List<Map<String, Object>> scores, Long teacherId) {
        for (Map<String, Object> scoreData : scores) {
            Long studentId = Long.parseLong(scoreData.get("studentId").toString());
            Long courseId = Long.parseLong(scoreData.get("courseId").toString());
            double score = Double.parseDouble(scoreData.get("score").toString());
            Integer level = calculateScoreLevel(score);

            // 查重：同一学生+课程已有成绩则更新，否则插入
            ScoreRecord existing = scoreRecordMapper.selectByStudentAndCourse(studentId, courseId);
            ScoreRecord sr;
            if (existing != null) {
                existing.setScore(score);
                existing.setScoreLevel(level);
                existing.setRemark((String) scoreData.get("remark"));
                existing.setStudentName((String) scoreData.get("studentName"));
                existing.setClassName((String) scoreData.get("className"));
                existing.setTeacherId(teacherId);
                scoreRecordMapper.updateScore(existing);
                sr = existing;
            } else {
                sr = new ScoreRecord();
                sr.setCourseId(courseId);
                sr.setStudentId(studentId);
                sr.setTeacherId(teacherId);
                sr.setStudentName((String) scoreData.get("studentName"));
                sr.setClassName((String) scoreData.get("className"));
                sr.setScore(score);
                sr.setScoreLevel(level);
                sr.setRemark((String) scoreData.get("remark"));
                scoreRecordMapper.insertScore(sr);
            }
            // 同步到 scores 表
            syncToScores(sr);
        }
    }
    
    /** 将 ScoreRecord 同步到 scores 表 */
    private void syncToScores(ScoreRecord sr) {
        if (sr.getStudentId() == null || sr.getCourseId() == null) return;
        Score existingScore = scoreMapper.selectByStudentAndCourse(sr.getStudentId(), sr.getCourseId());
        
        Score score = existingScore != null ? existingScore : new Score();
        score.setStudentId(sr.getStudentId());
        score.setCourseId(sr.getCourseId());
        score.setScoreTotal(sr.getScore() != null ? BigDecimal.valueOf(sr.getScore()) : null);
        score.setGrade(scoreToGrade(sr.getScore()));
        score.setGradePoint(scoreToGpa(sr.getScore()));
        score.setRegularScore(sr.getRegularScore() != null ? BigDecimal.valueOf(sr.getRegularScore()) : null);
        score.setFinalScore(sr.getFinalScore() != null ? BigDecimal.valueOf(sr.getFinalScore()) : null);
        score.setSemester(sr.getSemester() != null ? sr.getSemester() : getDefaultSemester());
        score.setYear(sr.getYear() != null ? sr.getYear() : getDefaultYear());
        score.setModifiedBy(sr.getModifiedBy());
        
        if (existingScore != null) {
            scoreMapper.updateById(score);
        } else {
            score.setCreatedAt(LocalDateTime.now());
            scoreMapper.insert(score);
        }
    }
    
    private static String scoreToGrade(Double s) {
        if (s == null) return null;
        if (s >= 90) return "A";
        if (s >= 80) return "A-";
        if (s >= 70) return "B";
        if (s >= 60) return "C";
        return "D";
    }
    
    private static BigDecimal scoreToGpa(Double s) {
        if (s == null) return BigDecimal.ZERO;
        if (s >= 90) return BigDecimal.valueOf(4.0);
        if (s >= 85) return BigDecimal.valueOf(3.7);
        if (s >= 82) return BigDecimal.valueOf(3.3);
        if (s >= 78) return BigDecimal.valueOf(3.0);
        if (s >= 75) return BigDecimal.valueOf(2.7);
        if (s >= 72) return BigDecimal.valueOf(2.3);
        if (s >= 68) return BigDecimal.valueOf(2.0);
        if (s >= 64) return BigDecimal.valueOf(1.5);
        if (s >= 60) return BigDecimal.valueOf(1.0);
        return BigDecimal.ZERO;
    }
    
    /** 根据当前日期推算默认学年 */
    private static int getDefaultYear() {
        int year = java.time.LocalDate.now().getYear();
        int month = java.time.LocalDate.now().getMonthValue();
        // 9月之后属于下一学年
        return month >= 9 ? year : year - 1;
    }
    
    /** 根据当前日期推算默认学期：9-1月=1，2-6月=2，7-8月=2 */
    private static String getDefaultSemester() {
        int month = java.time.LocalDate.now().getMonthValue();
        if (month >= 9 || month <= 1) return "1";
        return "2";
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void updateScore(Long scoreId, Double score, String remark, String modifiedBy) {
        // 读取旧记录
        ScoreRecord oldRecord = scoreRecordMapper.selectById(scoreId);
        
        // 更新成绩
        ScoreRecord scoreRecord = new ScoreRecord();
        scoreRecord.setId(scoreId);
        scoreRecord.setScore(score);
        scoreRecord.setScoreLevel(calculateScoreLevel(score));
        scoreRecord.setRemark(remark);
        scoreRecordMapper.updateScore(scoreRecord);
        
        // 写入审计日志
        if (oldRecord != null) {
            auditLogService.record(oldRecord, scoreRecord, remark, modifiedBy);
        }
        
        // 同步到 scores 表
        ScoreRecord sr = new ScoreRecord();
        sr.setId(scoreId);
        sr.setStudentId(oldRecord != null ? oldRecord.getStudentId() : null);
        sr.setCourseId(oldRecord != null ? oldRecord.getCourseId() : null);
        sr.setScore(score);
        sr.setRemark(remark);
        syncToScores(sr);
    }
    
    private Integer calculateScoreLevel(Double score) {
        if (score >= 90) return 1;
        if (score >= 80) return 2;
        if (score >= 70) return 3;
        if (score >= 60) return 4;
        return 5;
    }
    
    public List<ScoreRecord> exportScores(Long courseId) {
        return scoreRecordMapper.getScoresByCourseId(courseId);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        ScoreRecord sr = scoreRecordMapper.selectById(id);
        if (sr != null) {
            Score existing = scoreMapper.selectByStudentAndCourse(sr.getStudentId(), sr.getCourseId());
            if (existing != null) scoreMapper.deleteById(existing.getId());
        }
        scoreRecordMapper.deleteById(id);
    }
    
    public void removeByIds(List<Long> ids) {
        scoreRecordMapper.deleteByIds(ids);
    }
    
    public List<ScoreRecord> getScoresByClassName(String className) {
        return scoreRecordMapper.getScoresByClassName(className);
    }
    
    public List<ScoreRecord> getScoresByClassNameAndTeacherId(String className, Long teacherId) {
        return scoreRecordMapper.getScoresByClassNameAndTeacherId(className, teacherId);
    }
    
    public List<ScoreRecord> getScoresByTeacherId(Long teacherId) {
        return scoreRecordMapper.getScoresByTeacherId(teacherId);
    }
    
    public List<ScoreRecord> getScoresByStudentId(Long studentId) {
        return scoreRecordMapper.getScoresByStudentId(String.valueOf(studentId));
    }

    public List<Map<String, Object>> getScoresByClassId(Long classId) {
        return scoreRecordMapper.getScoresByClassId(classId);
    }
}