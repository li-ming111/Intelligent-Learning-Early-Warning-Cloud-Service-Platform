package com.academic.service;

import com.academic.mapper.ClassInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 班级服务类
 */
@Slf4j
@Service
public class ClassInfoService {

    @Autowired
    private ClassInfoMapper classInfoMapper;

    /**
     * 根据辅导员ID获取管理的班级
     */
    public List<Map<String, Object>> getClassesByCounselorId(Long counselorId) {
        try {
            List<Map<String, Object>> classes = classInfoMapper.getClassesByCounselorIdMap(counselorId);
            return classes != null ? classes : new ArrayList<>();
        } catch (Exception e) {
            log.error("获取辅导员班级失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 搜索班级
     */
    public List<Map<String, Object>> searchClasses(String keyword) {
        try {
            List<Map<String, Object>> classes;
            if (keyword == null || keyword.trim().isEmpty()) {
                classes = classInfoMapper.getAllClassesMap();
            } else {
                classes = classInfoMapper.searchClassesMap(keyword.trim());
            }
            // 处理 hasCounselor 字段
            if (classes != null) {
                for (Map<String, Object> clazz : classes) {
                    Object hasCounselorObj = clazz.get("hasCounselor");
                    if (hasCounselorObj instanceof Integer) {
                        clazz.put("hasCounselor", ((Integer) hasCounselorObj) == 1);
                    } else if (hasCounselorObj instanceof Boolean) {
                        clazz.put("hasCounselor", hasCounselorObj);
                    } else {
                        clazz.put("hasCounselor", false);
                    }
                }
                return classes;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("搜索班级失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 获取无辅导员的班级
     */
    public List<Map<String, Object>> getClassesWithoutCounselor() {
        try {
            List<Map<String, Object>> classes = classInfoMapper.getClassesWithoutCounselor();
            if (classes != null) {
                for (Map<String, Object> clazz : classes) {
                    clazz.put("hasCounselor", false);
                }
                return classes;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("获取无辅导员班级失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 获取班级学生列表
     */
    public List<Map<String, Object>> getStudentsByClassId(Long classId) {
        try {
            log.info("查询班级学生, classId: {}", classId);
            List<Map<String, Object>> students = classInfoMapper.getStudentsByClassIdMap(classId);
            log.info("查询结果: {}", students);
            return students != null ? students : new ArrayList<>();
        } catch (Exception e) {
            log.error("获取班级学生失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
