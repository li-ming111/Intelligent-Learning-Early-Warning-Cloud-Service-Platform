package com.academic.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StudentDashboardResponse {
    private Long studentId;
    private String studentName;
    private String className;
    private String majorName;
    private Double gpa;
    private Integer warningCount;
    private Integer redWarnings;
    private Integer yellowWarnings;
    private Integer assistanceCount;
    private String warningLevel;
    private Integer failedCoursesCount;
    private Integer totalCoursesCount;
    private Integer completedCredits;
    private Integer requiredCredits;
    private List<Map<String, Object>> recentWarnings;
}
