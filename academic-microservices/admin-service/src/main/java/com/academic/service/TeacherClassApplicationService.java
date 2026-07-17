package com.academic.service;

import com.academic.entity.TeacherClassApplication;

import java.util.List;

public interface TeacherClassApplicationService {

    List<TeacherClassApplication> getAllApplications();

    List<TeacherClassApplication> getApplicationsByStatus(Integer status);

    boolean updateApplicationStatus(Long id, Integer status, String rejectReason);
    
    boolean removeTeacherFromClass(Long classId);
}
