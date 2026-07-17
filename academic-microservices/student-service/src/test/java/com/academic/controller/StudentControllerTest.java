package com.academic.controller;

import com.academic.common.dto.ApiResponse;
import com.academic.dto.StudentDashboardResponse;
import com.academic.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    @Test
    void testGetStudentDashboard() {
        StudentDashboardResponse response = new StudentDashboardResponse();
        when(studentService.getStudentDashboard(100L)).thenReturn(response);

        ApiResponse<StudentDashboardResponse> result = studentController.getStudentDashboard("100");

        assertNotNull(result);
        verify(studentService, times(1)).getStudentDashboard(100L);
    }

    @Test
    void testGetStudentDashboard_InvalidId() {
        when(studentService.getStudentDashboard(anyLong())).thenThrow(new RuntimeException("学生不存在"));

        ApiResponse<StudentDashboardResponse> result = studentController.getStudentDashboard("999");

        assertNotNull(result);
        verify(studentService, times(1)).getStudentDashboard(anyLong());
    }

    @Test
    void testGetStudentScores() {
        when(studentService.getStudentScores(100L, null)).thenReturn(Collections.emptyList());

        ApiResponse<Object> result = studentController.getStudentScores("100", null);

        assertNotNull(result);
        verify(studentService, times(1)).getStudentScores(100L, null);
    }

    @Test
    void testGetStudentScores_WithSemester() {
        when(studentService.getStudentScores(100L, "2025-1")).thenReturn(Collections.emptyList());

        ApiResponse<Object> result = studentController.getStudentScores("100", "2025-1");

        assertNotNull(result);
        verify(studentService, times(1)).getStudentScores(100L, "2025-1");
    }

    @Test
    void testExportScoresToExcel() {
        when(studentService.exportScoresToExcel(100L)).thenReturn("导出成功");

        ApiResponse<Object> result = studentController.exportScoresToExcel("100");

        assertNotNull(result);
        verify(studentService, times(1)).exportScoresToExcel(100L);
    }

    @Test
    void testGetStudentWarnings() {
        when(studentService.getStudentWarnings(100L)).thenReturn(Collections.emptyList());

        ApiResponse<Object> result = studentController.getStudentWarnings("100");

        assertNotNull(result);
        verify(studentService, times(1)).getStudentWarnings(100L);
    }
}
