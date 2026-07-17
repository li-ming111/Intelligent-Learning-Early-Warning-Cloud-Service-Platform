package com.academic.service;

import com.academic.common.entity.ScoreRecord;
import com.academic.mapper.ScoreRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreRecordServiceTest {

    @Mock
    private ScoreRecordMapper scoreRecordMapper;

    @InjectMocks
    private ScoreRecordService scoreRecordService;

    @Test
    void testGetScoresByClassName() {
        ScoreRecord record = new ScoreRecord();
        record.setId(1L);
        record.setStudentName("张三");
        record.setStudentId(100L);
        record.setCourseId(10L);
        record.setCourseName("Java程序设计");
        record.setScore(85.0);

        when(scoreRecordMapper.getScoresByClassName("软件工程2101")).thenReturn(List.of(record));

        List<ScoreRecord> results = scoreRecordService.getScoresByClassName("软件工程2101");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("张三", results.get(0).getStudentName());
        verify(scoreRecordMapper, times(1)).getScoresByClassName("软件工程2101");
    }

    @Test
    void testGetScoresByClassName_EmptyClass() {
        when(scoreRecordMapper.getScoresByClassName("不存在的班级")).thenReturn(Collections.emptyList());

        List<ScoreRecord> results = scoreRecordService.getScoresByClassName("不存在的班级");

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testUpdateScore() {
        when(scoreRecordMapper.updateScore(any(ScoreRecord.class))).thenReturn(1);

        assertDoesNotThrow(() -> scoreRecordService.updateScore(1L, 92.0, "成绩修正"));
        verify(scoreRecordMapper, times(1)).updateScore(any(ScoreRecord.class));
    }

    @Test
    void testRemoveByIds() {
        when(scoreRecordMapper.deleteByIds(anyList())).thenReturn(1);

        assertDoesNotThrow(() -> scoreRecordService.removeByIds(Arrays.asList(1L, 2L, 3L)));
        verify(scoreRecordMapper, times(1)).deleteByIds(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void testRemoveById() {
        when(scoreRecordMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> scoreRecordService.removeById(1L));
        verify(scoreRecordMapper, times(1)).deleteById(1L);
    }

    @Test
    void testGetScoresByTeacherId() {
        ScoreRecord record = new ScoreRecord();
        record.setId(1L);
        record.setStudentName("李四");

        when(scoreRecordMapper.getScoresByTeacherId(10L)).thenReturn(List.of(record));

        List<ScoreRecord> results = scoreRecordService.getScoresByTeacherId(10L);

        assertEquals(1, results.size());
        assertEquals("李四", results.get(0).getStudentName());
    }

    @Test
    void testGetScoresByStudentId() {
        ScoreRecord record = new ScoreRecord();
        record.setStudentName("王五");
        record.setScore(78.0);

        when(scoreRecordMapper.getScoresByStudentId("100")).thenReturn(List.of(record));

        List<ScoreRecord> results = scoreRecordService.getScoresByStudentId(100L);

        assertEquals(78.0, results.get(0).getScore());
    }

    @Test
    void testExportScores() {
        when(scoreRecordMapper.getScoresByCourseId(10L)).thenReturn(Collections.emptyList());

        List<ScoreRecord> results = scoreRecordService.exportScores(10L);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
