package com.academic.service.impl;

import com.academic.entity.TeacherClassApplication;
import com.academic.entity.Classes;
import com.academic.mapper.TeacherClassApplicationMapper;
import com.academic.mapper.ClassesMapper;
import com.academic.service.TeacherClassApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TeacherClassApplicationServiceImpl implements TeacherClassApplicationService {

    @Autowired
    private TeacherClassApplicationMapper applicationMapper;

    @Autowired
    private ClassesMapper classesMapper;

    @Override
    public List<TeacherClassApplication> getAllApplications() {
        try {
            List<TeacherClassApplication> applications = applicationMapper.getAllApplications();
            return applications != null ? applications : new ArrayList<>();
        } catch (Exception e) {
            log.error("获取班级管理申请失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<TeacherClassApplication> getApplicationsByStatus(Integer status) {
        try {
            List<TeacherClassApplication> applications = applicationMapper.getApplicationsByStatus(status);
            return applications != null ? applications : new ArrayList<>();
        } catch (Exception e) {
            log.error("获取班级管理申请失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateApplicationStatus(Long id, Integer status, String rejectReason) {
        try {
            log.info("更新申请状态, id={}, status={}, rejectReason={}", id, status, rejectReason);
            
            // 获取申请信息
            TeacherClassApplication application = applicationMapper.selectById(id);
            if (application == null) {
                log.warn("申请不存在, id={}", id);
                return false;
            }
            
            log.info("申请信息: classId={}, teacherId={}, className={}", 
                application.getClassId(), application.getTeacherId(), application.getClassName());
            
            // 更新申请状态
            int result;
            if (status == 2 && rejectReason != null && !rejectReason.isEmpty()) {
                // 拒绝申请，更新状态和拒绝原因
                result = applicationMapper.updateStatusWithReason(id, status, rejectReason);
                log.info("拒绝申请，更新拒绝原因: {}", rejectReason);
            } else {
                // 批准申请或其他状态，只更新状态
                result = applicationMapper.updateStatus(id, status);
            }
            log.info("更新申请状态结果: {}", result);
            
            // 如果批准，根据申请类型处理
            if (status == 1 && result > 0) {
                Classes classes = classesMapper.selectById(application.getClassId());
                if (classes != null) {
                    log.info("找到班级: id={}, name={}, 当前teacherId={}, 申请类型={}", 
                        classes.getId(), classes.getName(), classes.getTeacherId(), application.getApplicationType());
                    
                    // 使用 UpdateWrapper 确保字段被正确更新
                    com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Classes> updateWrapper = 
                        new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
                    updateWrapper.eq("id", application.getClassId());
                    
                    if (application.getApplicationType() == null || application.getApplicationType() == 0) {
                        // 申请管理：设置教师ID
                        updateWrapper.set("teacher_id", application.getTeacherId());
                        int updateResult = classesMapper.update(null, updateWrapper);
                        log.info("更新班级教师结果: {}, 班级 {} 已分配给教师 {}", 
                            updateResult, application.getClassId(), application.getTeacherId());
                    } else {
                        // 解除管理：将教师ID设置为null
                        updateWrapper.set("teacher_id", null);
                        int updateResult = classesMapper.update(null, updateWrapper);
                        log.info("解除班级教师结果: {}, 班级 {} 的教师已移除", 
                            updateResult, application.getClassId());
                    }
                } else {
                    log.warn("班级不存在, classId={}", application.getClassId());
                }
            }
            
            return result > 0;
        } catch (Exception e) {
            log.error("更新申请状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean removeTeacherFromClass(Long classId) {
        try {
            log.info("解除教师与班级关联, classId={}", classId);
            
            Classes classes = classesMapper.selectById(classId);
            if (classes == null) {
                log.warn("班级不存在, classId={}", classId);
                return false;
            }
            
            if (classes.getTeacherId() == null) {
                log.warn("班级没有关联教师, classId={}", classId);
                return false;
            }
            
            classes.setTeacherId(null);
            int result = classesMapper.updateById(classes);
            log.info("解除关联结果: {}", result);
            return result > 0;
        } catch (Exception e) {
            log.error("解除教师与班级关联失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
