package com.academic.task;

import com.academic.common.entity.AcademicWarning;
import com.academic.mapper.AcademicWarningMapper;
import com.academic.mapper.ScoreRecordReadMapper;
import com.academic.mapper.WarningRuleMapper;
import com.academic.entity.WarningRule;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 预警自动扫描定时任务
 * 每天凌晨 2 点自动扫描学生成绩，根据预警规则自动生成预警记录
 */
@Slf4j
@Component
@EnableScheduling
public class WarningScheduledTask {

    private final ScoreRecordReadMapper scoreRecordReadMapper;
    private final AcademicWarningMapper academicWarningMapper;
    private final WarningRuleMapper warningRuleMapper;

    public WarningScheduledTask(ScoreRecordReadMapper scoreRecordReadMapper,
                                AcademicWarningMapper academicWarningMapper,
                                WarningRuleMapper warningRuleMapper) {
        this.scoreRecordReadMapper = scoreRecordReadMapper;
        this.academicWarningMapper = academicWarningMapper;
        this.warningRuleMapper = warningRuleMapper;
    }

    /**
     * 每天凌晨 2:00 执行自动预警扫描
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scanAndGenerateWarnings() {
        log.info("========== 自动预警扫描开始 ==========");
        try {
            // 1. 获取所有启用的预警规则
            List<WarningRule> activeRules = warningRuleMapper.selectList(
                    new QueryWrapper<WarningRule>().eq("status", 1)
            );
            log.info("加载到 {} 条启用的预警规则", activeRules.size());

            // 2. 获取已有预警的学生ID（避免重复）
            Set<Long> existingWarningStudents = scoreRecordReadMapper.findExistingWarningStudentIds()
                    .stream().collect(Collectors.toSet());
            log.info("已有 {} 名学生存在未处理预警", existingWarningStudents.size());

            int generatedCount = 0;

            // 3. 扫描低分学生（分数 < 60）
            List<Map<String, Object>> lowScoreStudents = scoreRecordReadMapper.findLowScoreStudents(60.0);
            for (Map<String, Object> student : lowScoreStudents) {
                Long studentId = parseLong(student.get("studentId"));
                if (studentId == null || existingWarningStudents.contains(studentId)) continue;

                String studentName = (String) student.get("studentName");
                String courseName = (String) student.get("courseName");
                Object score = student.get("score");

                createWarning(studentId, studentName, null, 2,
                        String.format("%s同学《%s》课程成绩为%s分，未达到及格线，系统自动触发预警。",
                                studentName, courseName, score));
                generatedCount++;
            }

            // 4. 扫描多门挂科的学生（>= 2 门不及格）
            List<Map<String, Object>> multiFailStudents = scoreRecordReadMapper.findStudentsWithMultipleFailures(2);
            for (Map<String, Object> student : multiFailStudents) {
                Long studentId = parseLong(student.get("studentId"));
                if (studentId == null || existingWarningStudents.contains(studentId)) continue;

                String studentName = (String) student.get("studentName");
                Object failCount = student.get("failCount");
                Object avgScore = student.get("avgScore");

                createWarning(studentId, studentName, null, 3,
                        String.format("%s同学已累计%s门课程不及格（均分%s），属于高风险学生，系统自动触发高级预警。",
                                studentName, failCount, avgScore));
                generatedCount++;
            }

            // 5. 根据规则条件进行更精细的扫描
            for (WarningRule rule : activeRules) {
                if (rule.getWarningLevel() == null) continue;
                Double threshold = parseThreshold(rule);
                if (threshold == null) continue;

                List<Map<String, Object>> ruleStudents = scoreRecordReadMapper.findLowScoreStudents(threshold);
                for (Map<String, Object> student : ruleStudents) {
                    Long studentId = parseLong(student.get("studentId"));
                    if (studentId == null || existingWarningStudents.contains(studentId)) continue;

                    String studentName = (String) student.get("studentName");
                    createWarning(studentId, studentName, rule.getId(), rule.getWarningLevel(),
                            String.format("%s同学触发预警规则「%s」：%s",
                                    studentName, rule.getRuleName(),
                                    rule.getDescription() != null ? rule.getDescription() : "成绩低于" + threshold + "分"));
                    generatedCount++;
                }
            }

            log.info("========== 自动预警扫描完成，共生成 {} 条预警 ==========", generatedCount);
        } catch (Exception e) {
            log.error("自动预警扫描异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 每30分钟检查一次，补充新增的成绩数据
     */
    @Scheduled(fixedRate = 1800000)
    public void incrementalScan() {
        log.debug("增量预警扫描...");
        try {
            Set<Long> existingWarningStudents = scoreRecordReadMapper.findExistingWarningStudentIds()
                    .stream().collect(Collectors.toSet());

            List<Map<String, Object>> lowScoreStudents = scoreRecordReadMapper.findLowScoreStudents(60.0);
            int count = 0;
            for (Map<String, Object> student : lowScoreStudents) {
                Long studentId = parseLong(student.get("studentId"));
                if (studentId == null || existingWarningStudents.contains(studentId)) continue;

                String studentName = (String) student.get("studentName");
                String courseName = (String) student.get("courseName");
                Object score = student.get("score");

                createWarning(studentId, studentName, null, 2,
                        String.format("%s同学《%s》课程成绩为%s分，未达到及格线。",
                                studentName, courseName, score));
                count++;
            }
            if (count > 0) {
                log.info("增量扫描发现 {} 名新预警学生", count);
            }
        } catch (Exception e) {
            log.debug("增量扫描异常: {}", e.getMessage());
        }
    }

    private void createWarning(Long studentId, String studentName, Long ruleId,
                               Integer warningLevel, String message) {
        AcademicWarning warning = new AcademicWarning()
                .setStudentId(studentId)
                .setStudentName(studentName)
                .setRuleId(ruleId)
                .setWarningLevel(warningLevel)
                .setWarningMessage(message)
                .setStatus(0)
                .setCreatedAt(LocalDateTime.now());
        academicWarningMapper.insert(warning);
    }

    private Long parseLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseThreshold(WarningRule rule) {
        try {
            if (rule.getExpression() != null && rule.getExpression().contains("<")) {
                String num = rule.getExpression().replaceAll("[^0-9.]", "");
                return Double.parseDouble(num);
            }
            if (rule.getFailDefinition() != null) {
                return 60.0;
            }
        } catch (NumberFormatException ignored) {}
        return null;
    }
}
