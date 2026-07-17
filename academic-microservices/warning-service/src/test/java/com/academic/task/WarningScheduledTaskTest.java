package com.academic.task;

import com.academic.common.entity.AcademicWarning;
import com.academic.mapper.AcademicWarningMapper;
import com.academic.mapper.ScoreRecordReadMapper;
import com.academic.mapper.WarningRuleMapper;
import com.academic.entity.WarningRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarningScheduledTaskTest {

    @Mock
    private ScoreRecordReadMapper scoreRecordReadMapper;

    @Mock
    private AcademicWarningMapper academicWarningMapper;

    @Mock
    private WarningRuleMapper warningRuleMapper;

    @InjectMocks
    private WarningScheduledTask scheduledTask;

    @BeforeEach
    void setUp() {
        // @ExtendWith(MockitoExtension.class) 自动初始化 mocks
    }

    @Test
    void testScanAndGenerateWarnings_WithLowScoreStudents() {
        // 准备：无启用规则，无已有预警
        when(warningRuleMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(scoreRecordReadMapper.findExistingWarningStudentIds()).thenReturn(Collections.emptyList());

        // 准备：2 个低分学生
        Map<String, Object> student1 = new HashMap<>();
        student1.put("studentId", 100L);
        student1.put("studentName", "张三");
        student1.put("courseName", "Java程序设计");
        student1.put("score", 45.0);
        List<Map<String, Object>> lowScoreStudents = List.of(student1);
        when(scoreRecordReadMapper.findLowScoreStudents(60.0)).thenReturn(lowScoreStudents);

        // 准备：无多门挂科学生
        when(scoreRecordReadMapper.findStudentsWithMultipleFailures(2)).thenReturn(Collections.emptyList());

        // 执行
        scheduledTask.scanAndGenerateWarnings();

        // 验证：应该插入 1 条预警（低分学生，非多门挂科）
        ArgumentCaptor<AcademicWarning> captor = ArgumentCaptor.forClass(AcademicWarning.class);
        verify(academicWarningMapper, times(1)).insert(captor.capture());
        AcademicWarning warning = captor.getValue();
        assertEquals(100L, warning.getStudentId());
        assertEquals("张三", warning.getStudentName());
        assertEquals(2, warning.getWarningLevel()); // 中级预警
        assertEquals(0, warning.getStatus()); // 未处理
        assertTrue(warning.getWarningMessage().contains("张三"));
        assertTrue(warning.getWarningMessage().contains("Java程序设计"));
    }

    @Test
    void testScanAndGenerateWarnings_SkipExistingWarnings() {
        when(warningRuleMapper.selectList(any())).thenReturn(Collections.emptyList());

        // 学生 100 已有未处理预警
        when(scoreRecordReadMapper.findExistingWarningStudentIds())
                .thenReturn(List.of(100L));

        Map<String, Object> student1 = new HashMap<>();
        student1.put("studentId", 100L);
        student1.put("studentName", "张三");
        student1.put("courseName", "Java程序设计");
        student1.put("score", 45.0);
        when(scoreRecordReadMapper.findLowScoreStudents(60.0)).thenReturn(List.of(student1));
        when(scoreRecordReadMapper.findStudentsWithMultipleFailures(2)).thenReturn(Collections.emptyList());

        scheduledTask.scanAndGenerateWarnings();

        // 验证：已有预警的学生不应重复生成
        verify(academicWarningMapper, never()).insert(any());
    }

    @Test
    void testScanAndGenerateWarnings_WithMultipleFailures() {
        when(warningRuleMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(scoreRecordReadMapper.findExistingWarningStudentIds()).thenReturn(Collections.emptyList());
        when(scoreRecordReadMapper.findLowScoreStudents(60.0)).thenReturn(Collections.emptyList());

        Map<String, Object> student1 = new HashMap<>();
        student1.put("studentId", 200L);
        student1.put("studentName", "李四");
        student1.put("failCount", 3L);
        student1.put("avgScore", 42.0);
        when(scoreRecordReadMapper.findStudentsWithMultipleFailures(2)).thenReturn(List.of(student1));

        scheduledTask.scanAndGenerateWarnings();

        ArgumentCaptor<AcademicWarning> captor = ArgumentCaptor.forClass(AcademicWarning.class);
        verify(academicWarningMapper, times(1)).insert(captor.capture());
        AcademicWarning warning = captor.getValue();
        assertEquals(200L, warning.getStudentId());
        assertEquals(3, warning.getWarningLevel()); // 高级预警
        assertTrue(warning.getWarningMessage().contains("高风险"));
    }

    @Test
    void testScanAndGenerateWarnings_WithRules() {
        // 准备规则
        WarningRule rule = new WarningRule();
        rule.setId(1L);
        rule.setRuleName("成绩低于50分");
        rule.setWarningLevel(2);
        rule.setExpression("score < 50");
        rule.setDescription("成绩低于50分需关注");
        when(warningRuleMapper.selectList(any())).thenReturn(List.of(rule));

        when(scoreRecordReadMapper.findExistingWarningStudentIds()).thenReturn(Collections.emptyList());
        when(scoreRecordReadMapper.findLowScoreStudents(60.0)).thenReturn(Collections.emptyList());
        when(scoreRecordReadMapper.findStudentsWithMultipleFailures(2)).thenReturn(Collections.emptyList());

        Map<String, Object> student1 = new HashMap<>();
        student1.put("studentId", 300L);
        student1.put("studentName", "王五");
        when(scoreRecordReadMapper.findLowScoreStudents(50.0)).thenReturn(List.of(student1));

        scheduledTask.scanAndGenerateWarnings();

        ArgumentCaptor<AcademicWarning> captor = ArgumentCaptor.forClass(AcademicWarning.class);
        verify(academicWarningMapper, times(1)).insert(captor.capture());
        AcademicWarning warning = captor.getValue();
        assertEquals(300L, warning.getStudentId());
        assertEquals(1L, warning.getRuleId()); // 关联到规则 1
    }

    @Test
    void testScanAndGenerateWarnings_ExceptionHandling() {
        when(warningRuleMapper.selectList(any())).thenThrow(new RuntimeException("数据库连接失败"));

        // 不应抛出异常，应被 catch 捕获
        assertDoesNotThrow(() -> scheduledTask.scanAndGenerateWarnings());
    }

    @Test
    void testParseThreshold_ValidCondition() {
        when(warningRuleMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(scoreRecordReadMapper.findExistingWarningStudentIds()).thenReturn(Collections.emptyList());
        when(scoreRecordReadMapper.findLowScoreStudents(60.0)).thenReturn(Collections.emptyList());
        when(scoreRecordReadMapper.findStudentsWithMultipleFailures(2)).thenReturn(Collections.emptyList());

        // 空规则列表，不应出错
        assertDoesNotThrow(() -> scheduledTask.scanAndGenerateWarnings());
    }
}
