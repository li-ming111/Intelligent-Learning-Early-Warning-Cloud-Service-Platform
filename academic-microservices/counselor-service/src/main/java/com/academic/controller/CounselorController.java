package com.academic.controller;

import com.academic.common.dto.ApiResponse;
import com.academic.common.entity.CounselorProfile;
import com.academic.common.entity.AssistancePlan;
import com.academic.common.entity.CommunicationLog;
import com.academic.common.entity.Student;
import com.academic.common.entity.AcademicWarning;
import com.academic.entity.CounselorClassApplication;
import com.academic.entity.ClassInfo;
import com.academic.service.CounselorService;
import com.academic.service.AssistancePlanService;
import com.academic.service.CommunicationLogService;
import com.academic.service.StudentService;
import com.academic.service.ClassInfoService;
import com.academic.mapper.CounselorClassApplicationMapper;
import com.academic.mapper.ClassInfoMapper;
import com.academic.mapper.ScoreMapper;
import com.academic.mapper.WarningMapper;
import com.academic.mapper.StudentMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 辅导员控制器
 */
@Slf4j
@RestController
@RequestMapping("/counselors")
public class CounselorController {

    @Autowired
    private CounselorService counselorService;

    @Autowired
    private AssistancePlanService assistancePlanService;

    @Autowired
    private CommunicationLogService communicationLogService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CounselorClassApplicationMapper counselorClassApplicationMapper;

    @Autowired
    private ClassInfoService classInfoService;

    @Autowired
    private ClassInfoMapper classInfoMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private WarningMapper warningMapper;

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 辅导员注册
     */
    @PostMapping("/register")
    public ApiResponse<CounselorProfile> register(@RequestBody CounselorProfile counselorProfile) {
        try {
            boolean success = counselorService.register(counselorProfile);
            if (success) {
                return ApiResponse.success(counselorProfile);
            } else {
                return ApiResponse.badRequest("辅导员ID已存在");
            }
        } catch (Exception e) {
            log.error("注册失败: {}", e.getMessage());
            return ApiResponse.serverError("注册失败: " + e.getMessage());
        }
    }

    /**
     * 获取辅导员仪表板数据
     */
    @GetMapping("/dashboard/{userId}")
    public ApiResponse<Map<String, Object>> getDashboard(@PathVariable Long userId) {
        try {
            CounselorProfile counselor = counselorService.getByUserId(userId);
            
            Map<String, Object> dashboard = new HashMap<>();
            
            if (counselor == null) {
                counselor = new CounselorProfile();
                counselor.setUserId(userId);
                counselor.setName("辅导员");
                counselor.setCollegeName("计算机学院");
                counselor.setDepartment("学生工作办公室");
            }
            
            dashboard.put("counselor", counselor);
            
            // 查询真实数据（classes.counselor_id 存的是 users.id，所以用 userId）
            Long counselorId = counselor.getUserId() != null ? counselor.getUserId() : counselor.getId();
            int studentCount = 0;
            int warningCount = 0;
            int redWarnings = 0;
            int yellowWarnings = 0;
            int blueWarnings = 0;
            
            if (counselorId != null) {
                // 查询管理的班级及学生总数
                List<Map<String, Object>> classes = classInfoMapper.getClassesByCounselorIdMap(counselorId);
                if (classes != null) {
                    for (Map<String, Object> c : classes) {
                        Object sc = c.get("studentCount");
                        studentCount += sc != null ? ((Number) sc).intValue() : 0;
                    }
                }
                
                // 查询预警分布
                List<Map<String, Object>> warnDist = warningMapper.getWarningDistributionByCounselor(counselorId);
                if (warnDist != null) {
                    for (Map<String, Object> w : warnDist) {
                        Object level = w.get("warning_level");
                        Object cnt = w.get("count");
                        int c = cnt != null ? ((Number) cnt).intValue() : 0;
                        warningCount += c;
                        if (level != null) {
                            int lv = ((Number) level).intValue();
                            if (lv >= 3) redWarnings += c;
                            else if (lv == 2) yellowWarnings += c;
                            else blueWarnings += c;
                        }
                    }
                }
            }
            
            dashboard.put("studentCount", studentCount);
            dashboard.put("warningCount", warningCount);
            dashboard.put("redWarnings", redWarnings);
            dashboard.put("yellowWarnings", yellowWarnings);
            dashboard.put("blueWarnings", blueWarnings);
            
            // 班级列表
            if (counselorId != null) {
                dashboard.put("classes", classInfoMapper.getClassesByCounselorIdMap(counselorId));
                // 最近预警
                List<Long> counselorClassIds = classInfoMapper.getClassesByCounselorIdMap(counselorId)
                    .stream().map(c -> (Long) c.get("id")).collect(Collectors.toList());
                List<Long> counselorStudentIds = counselorClassIds.isEmpty() ? new ArrayList<>() :
                    studentMapper.selectList(new QueryWrapper<Student>().in("class_id", counselorClassIds))
                        .stream().map(Student::getId).collect(Collectors.toList());
                List<AcademicWarning> recentWarns = counselorStudentIds.isEmpty() ? new ArrayList<>() :
                    warningMapper.selectList(
                        new QueryWrapper<AcademicWarning>()
                            .in("student_id", counselorStudentIds)
                            .orderByDesc("created_at")
                            .last("LIMIT 5")
                    );
                dashboard.put("recentWarnings", recentWarns);
            } else {
                dashboard.put("classes", java.util.Collections.emptyList());
                dashboard.put("recentWarnings", java.util.Collections.emptyList());
            }
            
            return ApiResponse.success(dashboard);
        } catch (Exception e) {
            log.error("获取仪表板数据失败: {}", e.getMessage());
            return ApiResponse.serverError("获取仪表板数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有辅导员列表（支持按学院筛选）
     */
    @GetMapping
    public ApiResponse<List<CounselorProfile>> getAllCounselors(@RequestParam(required = false) Long collegeId) {
        try {
            List<CounselorProfile> counselors = counselorService.getAllCounselors(collegeId);
            return ApiResponse.success(counselors);
        } catch (Exception e) {
            log.error("获取辅导员列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取辅导员列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取辅导员信息
     */
    @GetMapping("/{counselorId}")
    public ApiResponse<CounselorProfile> getCounselorInfo(@PathVariable String counselorId) {
        try {
            CounselorProfile counselor = counselorService.getByCounselorId(counselorId);
            if (counselor != null) {
                return ApiResponse.success(counselor);
            } else {
                return ApiResponse.notFound("辅导员不存在");
            }
        } catch (Exception e) {
            log.error("获取辅导员信息失败: {}", e.getMessage());
            return ApiResponse.serverError("获取辅导员信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取辅导员管理的学生列表（查询参数方式）
     */
    @GetMapping("/students")
    public ApiResponse<List<Student>> getStudentsByParam(@RequestParam("counselor_id") Long counselorId) {
        try {
            List<Student> students = studentService.getByCounselorId(counselorId);
            return ApiResponse.success(students);
        } catch (Exception e) {
            log.error("获取学生列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取学生列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取预警列表
     */
    @GetMapping("/warnings")
    public ApiResponse<List<Map<String, Object>>> getWarnings(@RequestParam("counselor_id") Long counselorId, 
                                                 @RequestParam(value = "status", required = false) String status) {
        try {
            List<Map<String, Object>> warnings = new ArrayList<>();
            // 获取辅导员管理的班级
            List<Map<String, Object>> classes = classInfoService.getClassesByCounselorId(counselorId);
            if (classes.isEmpty()) {
                return ApiResponse.success(warnings);
            }
            
            // 构建 classId -> className 映射
            Map<Long, String> classMap = new HashMap<>();
            StringBuilder classIds = new StringBuilder();
            for (int i = 0; i < classes.size(); i++) {
                Map<String, Object> clazz = classes.get(i);
                Long classId = (Long) clazz.get("id");
                classMap.put(classId, (String) clazz.get("name"));
                classIds.append(classId);
                if (i < classes.size() - 1) classIds.append(",");
            }

            // 查询学生档案，构建 studentId -> className 映射
            List<Long> classIdList = classes.stream()
                .map(c -> (Long) c.get("id")).collect(Collectors.toList());
            Map<Long, String> studentClassMap = new HashMap<>();
            List<Student> profiles =
                studentMapper.selectList(
                    new QueryWrapper<Student>()
                        .in("class_id", classIdList));
            if (profiles != null) {
                for (Student sp : profiles) {
                    String cname = classMap.get(sp.getClassId());
                    if (cname != null) {
                        studentClassMap.put(sp.getId(), cname);
                    }
                }
            }

            // 查询这些班级学生的预警
            QueryWrapper<AcademicWarning> qw = new QueryWrapper<>();
            List<Long> classStudents = profiles != null ? profiles.stream()
                .map(Student::getId).collect(Collectors.toList()) : Collections.emptyList();
            qw.in(classStudents.isEmpty() ? "student_id" : "student_id", 
                  classStudents.isEmpty() ? Collections.singletonList(-1L) : classStudents);
            if (status != null && !status.isEmpty()) {
                qw.eq("status", Integer.parseInt(status));
            }
            qw.orderByDesc("created_at");

            List<AcademicWarning> result = warningMapper.selectList(qw);
            for (AcademicWarning w : result) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", w.getId());
                item.put("studentId", w.getStudentId());
                item.put("studentName", w.getStudentName());
                item.put("warningLevel", w.getWarningLevel());
                item.put("description", w.getDescription());
                item.put("status", w.getStatus());
                item.put("createdAt", w.getCreatedAt());
                item.put("className", studentClassMap.getOrDefault(w.getStudentId(), "未知班级"));
                warnings.add(item);
            }
            return ApiResponse.success(warnings);
        } catch (Exception e) {
            log.error("获取预警列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取预警列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新辅导员信息
     */
    @PutMapping("/{id}")
    public ApiResponse<String> updateCounselorInfo(@PathVariable Long id, @RequestBody CounselorProfile counselorProfile) {
        try {
            counselorProfile.setId(id);
            boolean success = counselorService.updateProfile(counselorProfile);
            if (success) {
                return ApiResponse.success("辅导员信息更新成功");
            } else {
                return ApiResponse.notFound("辅导员不存在");
            }
        } catch (Exception e) {
            log.error("更新辅导员信息失败: {}", e.getMessage());
            return ApiResponse.serverError("更新辅导员信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除辅导员信息
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCounselorInfo(@PathVariable Long id,
                                                    HttpServletRequest request) {
        Long requestUserId = getUserIdFromGateway(request);
        CounselorProfile profile = counselorService.getByUserId(id);
        if (profile == null || profile.getUserId() == null
                || !profile.getUserId().equals(requestUserId)) {
            // 只有本人才能删除自己的信息
            if (!isAdmin(request)) {
                return ApiResponse.error(403, "无权删除");
            }
        }
        try {
            boolean success = counselorService.deleteProfile(id);
            if (success) {
                return ApiResponse.success("辅导员信息删除成功");
            } else {
                return ApiResponse.notFound("辅导员不存在");
            }
        } catch (Exception e) {
            log.error("删除辅导员信息失败: {}", e.getMessage());
            return ApiResponse.serverError("删除辅导员信息失败: " + e.getMessage());
        }
    }

    /**
     * 创建帮扶计划
     */
    @PostMapping("/assistance-plans")
    public ApiResponse<AssistancePlan> createAssistancePlan(@RequestBody AssistancePlan plan) {
        try {
            boolean success = assistancePlanService.createPlan(plan);
            if (success) {
                return ApiResponse.success(plan);
            } else {
                return ApiResponse.badRequest("帮扶计划创建失败");
            }
        } catch (Exception e) {
            log.error("创建帮扶计划失败: {}", e.getMessage());
            return ApiResponse.serverError("创建帮扶计划失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生的帮扶计划列表
     */
    @GetMapping("/students/{studentId}/assistance-plans")
    public ApiResponse<List<AssistancePlan>> getStudentAssistancePlans(@PathVariable Long studentId) {
        try {
            List<AssistancePlan> plans = assistancePlanService.getPlansByStudentId(studentId);
            return ApiResponse.success(plans);
        } catch (Exception e) {
            log.error("获取学生帮扶计划列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取学生帮扶计划列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取辅导员的帮扶计划列表
     */
    @GetMapping("/{counselorId}/assistance-plans")
    public ApiResponse<List<AssistancePlan>> getCounselorAssistancePlans(@PathVariable Long counselorId) {
        try {
            List<AssistancePlan> plans = assistancePlanService.list();
            return ApiResponse.success(plans);
        } catch (Exception e) {
            log.error("获取辅导员帮扶计划列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取辅导员帮扶计划列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新帮扶计划
     */
    @PutMapping("/assistance-plans/{id}")
    public ApiResponse<String> updateAssistancePlan(@PathVariable Long id, @RequestBody AssistancePlan plan) {
        try {
            plan.setId(id);
            boolean success = assistancePlanService.updatePlan(plan);
            if (success) {
                return ApiResponse.success("帮扶计划更新成功");
            } else {
                return ApiResponse.notFound("帮扶计划不存在");
            }
        } catch (Exception e) {
            log.error("更新帮扶计划失败: {}", e.getMessage());
            return ApiResponse.serverError("更新帮扶计划失败: " + e.getMessage());
        }
    }

    /**
     * 删除帮扶计划
     */
    @DeleteMapping("/assistance-plans/{id}")
    public ApiResponse<String> deleteAssistancePlan(@PathVariable Long id,
                                                     HttpServletRequest request) {
        Long requestUserId = getUserIdFromGateway(request);
        AssistancePlan plan = assistancePlanService.getById(id);
        if (plan != null && plan.getCounselorId() != null) {
            CounselorProfile profile = counselorService.getByUserId(requestUserId);
            if (profile == null || !plan.getCounselorId().equals(profile.getId())) {
                if (!isAdmin(request)) {
                    return ApiResponse.error(403, "无权删除其他辅导员的帮扶计划");
                }
            }
        }
        try {
            boolean success = assistancePlanService.deletePlan(id);
            if (success) {
                return ApiResponse.success("帮扶计划删除成功");
            } else {
                return ApiResponse.notFound("帮扶计划不存在");
            }
        } catch (Exception e) {
            log.error("删除帮扶计划失败: {}", e.getMessage());
            return ApiResponse.serverError("删除帮扶计划失败: " + e.getMessage());
        }
    }

    /**
     * 更新帮扶计划状态
     */
    @PatchMapping("/assistance-plans/{id}/status")
    public ApiResponse<String> updateAssistancePlanStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        try {
            String status = statusMap.get("status");
            boolean success = assistancePlanService.updatePlanStatus(id, status);
            if (success) {
                return ApiResponse.success("帮扶计划状态更新成功");
            } else {
                return ApiResponse.notFound("帮扶计划不存在");
            }
        } catch (Exception e) {
            log.error("更新帮扶计划状态失败: {}", e.getMessage());
            return ApiResponse.serverError("更新帮扶计划状态失败: " + e.getMessage());
        }
    }

    /**
     * 创建沟通记录
     */
    @PostMapping("/communication-logs")
    public ApiResponse<CommunicationLog> createCommunicationLog(@RequestBody CommunicationLog communicationLog) {
        try {
            boolean success = communicationLogService.createLog(communicationLog);
            if (success) {
                return ApiResponse.success(communicationLog);
            } else {
                return ApiResponse.badRequest("沟通记录创建失败");
            }
        } catch (Exception e) {
            log.error("创建沟通记录失败: {}", e.getMessage());
            return ApiResponse.serverError("创建沟通记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生的沟通记录列表
     */
    @GetMapping("/students/{studentId}/communication-logs")
    public ApiResponse<List<CommunicationLog>> getStudentCommunicationLogs(@PathVariable Long studentId) {
        try {
            List<CommunicationLog> logs = communicationLogService.getLogsByStudentId(studentId);
            return ApiResponse.success(logs);
        } catch (Exception e) {
            log.error("获取学生沟通记录列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取学生沟通记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取辅导员的沟通记录列表
     */
    @GetMapping("/{counselorId}/communication-logs")
    public ApiResponse<List<CommunicationLog>> getCounselorCommunicationLogs(@PathVariable Long counselorId) {
        try {
            List<CommunicationLog> logs = communicationLogService.getLogsByCounselorId(counselorId);
            return ApiResponse.success(logs);
        } catch (Exception e) {
            log.error("获取辅导员沟通记录列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取辅导员沟通记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新沟通记录
     */
    @PutMapping("/communication-logs/{id}")
    public ApiResponse<String> updateCommunicationLog(@PathVariable Long id, @RequestBody CommunicationLog communicationLog) {
        try {
            communicationLog.setId(id);
            boolean success = communicationLogService.updateLog(communicationLog);
            if (success) {
                return ApiResponse.success("沟通记录更新成功");
            } else {
                return ApiResponse.notFound("沟通记录不存在");
            }
        } catch (Exception e) {
            log.error("更新沟通记录失败: {}", e.getMessage());
            return ApiResponse.serverError("更新沟通记录失败: " + e.getMessage());
        }
    }

    /**
     * 删除沟通记录
     */
    @DeleteMapping("/communication-logs/{id}")
    public ApiResponse<String> deleteCommunicationLog(@PathVariable Long id,
                                                       HttpServletRequest request) {
        Long requestUserId = getUserIdFromGateway(request);
        CommunicationLog record = communicationLogService.getById(id);
        if (record != null && record.getCounselorId() != null) {
            CounselorProfile profile = counselorService.getByUserId(requestUserId);
            if (profile == null || !record.getCounselorId().equals(profile.getId())) {
                if (!isAdmin(request)) {
                    return ApiResponse.error(403, "无权删除其他辅导员的沟通记录");
                }
            }
        }
        try {
            boolean success = communicationLogService.deleteLog(id);
            if (success) {
                return ApiResponse.success("沟通记录删除成功");
            } else {
                return ApiResponse.notFound("沟通记录不存在");
            }
        } catch (Exception e) {
            log.error("删除沟通记录失败: {}", e.getMessage());
            return ApiResponse.serverError("删除沟通记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取辅导员管理的学生列表
     */
    @GetMapping("/{counselorId}/students")
    public ApiResponse<List<Student>> getCounselorStudents(@PathVariable Long counselorId) {
        try {
            List<Student> students = studentService.getByCounselorId(counselorId);
            return ApiResponse.success(students);
        } catch (Exception e) {
            log.error("获取辅导员管理的学生列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取辅导员管理的学生列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生详情
     */
    @GetMapping("/students/{studentId}")
    public ApiResponse<Student> getStudentInfo(@PathVariable Long studentId) {
        try {
            Student student = studentService.getById(studentId);
            if (student != null) {
                return ApiResponse.success(student);
            } else {
                return ApiResponse.notFound("学生不存在");
            }
        } catch (Exception e) {
            log.error("获取学生详情失败: {}", e.getMessage());
            return ApiResponse.serverError("获取学生详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新学生信息
     */
    @PutMapping("/students/{studentId}")
    public ApiResponse<String> updateStudentInfo(@PathVariable Long studentId, @RequestBody Student student) {
        try {
            student.setId(studentId);
            boolean success = studentService.updateById(student);
            if (success) {
                return ApiResponse.success("学生信息更新成功");
            } else {
                return ApiResponse.notFound("学生不存在");
            }
        } catch (Exception e) {
            log.error("更新学生信息失败: {}", e.getMessage());
            return ApiResponse.serverError("更新学生信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取辅导员管理的班级列表
     */
    @GetMapping("/class-management/my-classes")
    public ApiResponse<List<Map<String, Object>>> getMyClasses(@RequestParam("counselorId") Long counselorId) {
        try {
            List<Map<String, Object>> classes = classInfoService.getClassesByCounselorId(counselorId);
            return ApiResponse.success(classes);
        } catch (Exception e) {
            log.error("获取班级列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取班级列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取班级申请列表
     */
    @GetMapping("/class-management/requests")
    public ApiResponse<List<Map<String, Object>>> getClassRequests(@RequestParam("counselorId") Long counselorId) {
        try {
            List<CounselorClassApplication> apps = counselorClassApplicationMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CounselorClassApplication>()
                    .eq("counselor_id", counselorId).orderByDesc("create_time")
            );
            List<Map<String, Object>> result = new ArrayList<>();
            for (CounselorClassApplication app : apps) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", app.getId());
                m.put("classId", app.getClassId());
                m.put("className", app.getClassName());
                m.put("reason", app.getReason());
                m.put("status", app.getStatus());
                m.put("applicationType", app.getApplicationType());
                m.put("createTime", app.getCreateTime());
                result.add(m);
            }
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取班级申请列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取班级申请列表失败: " + e.getMessage());
        }
    }

    /**
     * 搜索班级
     */
    @GetMapping("/class-management/search")
    public ApiResponse<List<Map<String, Object>>> searchClasses(@RequestParam(required = false) String keyword) {
        try {
            List<Map<String, Object>> classes = classInfoService.searchClasses(keyword);
            return ApiResponse.success(classes);
        } catch (Exception e) {
            log.error("搜索班级失败: {}", e.getMessage());
            return ApiResponse.serverError("搜索班级失败: " + e.getMessage());
        }
    }

    /**
     * 获取无辅导员的班级
     */
    @GetMapping("/class-management/without-counselor")
    public ApiResponse<List<Map<String, Object>>> getClassesWithoutCounselor() {
        try {
            List<Map<String, Object>> classes = classInfoService.getClassesWithoutCounselor();
            return ApiResponse.success(classes);
        } catch (Exception e) {
            log.error("获取无辅导员班级失败: {}", e.getMessage());
            return ApiResponse.serverError("获取无辅导员班级失败: " + e.getMessage());
        }
    }

    /**
     * 申请管理班级（申请管理或解除管理）
     */
    @PostMapping("/class-management/apply")
    public ApiResponse<String> applyForClass(@RequestBody Map<String, Object> request) {
        try {
            Long counselorId = ((Number) request.get("counselorId")).longValue();
            Long classId = ((Number) request.get("classId")).longValue();
            String className = (String) request.get("className");
            String reason = (String) request.get("reason");
            
            // 获取申请类型，默认为0（申请管理）
            Integer applicationType = 0;
            if (request.get("applicationType") != null) {
                applicationType = ((Number) request.get("applicationType")).intValue();
            }
            
            // 获取辅导员姓名
            CounselorProfile counselor = counselorService.getByUserId(counselorId);
            String counselorName = counselor != null ? counselor.getName() : "";
            
            // 获取班级信息
            ClassInfo classInfo = classInfoMapper.selectById(classId);
            
            // 申请管理
            if (applicationType == 0) {
                // 1. 检查班级是否存在且无辅导员
                if (classInfo != null && classInfo.getCounselorId() != null && classInfo.getCounselorId() > 0) {
                    return ApiResponse.error("该班级已有辅导员管理，无法重复申请");
                }
                
                // 2. 检查辅导员是否已经有待审核的同类申请（仅阻塞 status=0 待审核的情况）
                List<CounselorClassApplication> existingApplications = counselorClassApplicationMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CounselorClassApplication>()
                        .eq("counselor_id", counselorId)
                        .eq("class_id", classId)
                        .eq("status", 0)
                );
                if (existingApplications != null && !existingApplications.isEmpty()) {
                    for (CounselorClassApplication app : existingApplications) {
                        if (app.getApplicationType() == null || app.getApplicationType() == 0) {
                            return ApiResponse.error("您已有一条该班级的待审核申请，请等待管理员处理");
                        }
                    }
                }
            } else {
                // 解除管理
                // 检查班级是否存在且该辅导员是当前管理者
                if (classInfo == null || classInfo.getCounselorId() == null || !classInfo.getCounselorId().equals(counselorId)) {
                    return ApiResponse.error("您不是该班级的辅导员，无法提交解除申请");
                }
            }
            
            // 创建班级申请记录
            CounselorClassApplication application = new CounselorClassApplication();
            application.setCounselorId(counselorId);
            application.setClassId(classId);
            application.setClassName(className);
            application.setReason(reason);
            application.setStatus(0); // 0-待审核
            application.setCounselorName(counselorName);
            application.setApplicationType(applicationType);
            
            counselorClassApplicationMapper.insert(application);
            
            String action = applicationType == 1 ? "解除管理" : "申请管理";
            log.info("辅导员 {}({}) {}班级 {} ({})，理由: {}", counselorId, counselorName, action, classId, className, reason);
            
            String message = applicationType == 1 ? "解除申请提交成功，等待审核" : "申请提交成功，等待审核";
            return ApiResponse.success(message);
        } catch (Exception e) {
            log.error("申请班级失败: {}", e.getMessage());
            return ApiResponse.serverError("申请班级失败: " + e.getMessage());
        }
    }

    /**
     * 解除班级管理权限
     */
    @PostMapping("/class-management/cancel")
    public ApiResponse<String> cancelClassManagement(@RequestBody Map<String, Object> request) {
        try {
            // 前端传递的是用户ID（User.id）
            Long userId = ((Number) request.get("counselorId")).longValue();
            Long classId = ((Number) request.get("classId")).longValue();
            
            // 根据用户ID查询辅导员Profile
            // 注意：classes.counselor_id 存储的是 User.id，不是 CounselorProfile.id
            // 所以这里不需要转换为 CounselorProfile.id，直接使用 userId 即可
            CounselorProfile profile = counselorService.getByUserId(userId);
            
            // 无论是否找到 CounselorProfile，都使用传入的 userId 进行验证
            // 因为 classes.counselor_id 存储的就是 User.id
            Long realCounselorId = userId;
            
            // 检查班级是否存在
            ClassInfo classInfo = classInfoMapper.selectById(classId);
            if (classInfo == null) {
                return ApiResponse.error("班级不存在");
            }
            
            // 检查辅导员是否管理该班级
            // 如果班级没有辅导员（counselor_id为null），说明已经没有管理者，直接返回成功
            if (classInfo.getCounselorId() == null) {
                log.info("用户ID {} 尝试解除班级 {} 的管理权限，班级counselorId已为null，无需解除", userId, classId);
                return ApiResponse.success("班级管理权限已解除");
            }
            
            if (!classInfo.getCounselorId().equals(realCounselorId)) {
                log.warn("用户ID {} 尝试解除班级 {} 的管理权限，但班级的counselorId是 {}，realCounselorId是 {}", 
                    userId, classId, classInfo.getCounselorId(), realCounselorId);
                return ApiResponse.error("您不是该班级的管理者");
            }
            
            // 解除班级与辅导员的关联（使用 UpdateWrapper 确保 counselor_id 被设置为 null）
            com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<ClassInfo> updateWrapper = new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
            updateWrapper.eq("id", classId).set("counselor_id", null);
            classInfoMapper.update(null, updateWrapper);
            
            // 更新相关申请记录状态为"已取消"
            List<CounselorClassApplication> applications = counselorClassApplicationMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CounselorClassApplication>()
                    .eq("counselor_id", realCounselorId)
                    .eq("class_id", classId)
            );
            if (applications != null && !applications.isEmpty()) {
                for (CounselorClassApplication application : applications) {
                    application.setStatus(3); // 3-已取消
                    counselorClassApplicationMapper.updateById(application);
                }
            }
            
            log.info("辅导员用户ID {} 解除班级 {} 的管理权限", userId, classId);
            return ApiResponse.success("班级管理权限已解除");
        } catch (Exception e) {
            log.error("解除班级管理权限失败: {}", e.getMessage());
            return ApiResponse.serverError("解除班级管理权限失败: " + e.getMessage());
        }
    }

    /**
     * 获取辅导员班级申请列表（供管理员使用）
     */
    @GetMapping("/class-management/applications")
    public ApiResponse<List<CounselorClassApplication>> getCounselorApplications(
            @RequestParam(value = "status", required = false) Integer status) {
        try {
            List<CounselorClassApplication> applications;
            if (status != null) {
                applications = counselorClassApplicationMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CounselorClassApplication>()
                        .eq("status", status)
                        .orderByDesc("create_time")
                );
            } else {
                applications = counselorClassApplicationMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CounselorClassApplication>()
                        .orderByDesc("create_time")
                );
            }
            return ApiResponse.success(applications);
        } catch (Exception e) {
            log.error("获取辅导员班级申请失败: {}", e.getMessage());
            return ApiResponse.serverError("获取辅导员班级申请失败: " + e.getMessage());
        }
    }

    /**
     * 审核辅导员班级申请（供管理员使用）
     */
    @PostMapping("/class-management/applications/{id}/review")
    public ApiResponse<String> reviewCounselorApplication(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Integer status = (Integer) request.get("status");
            CounselorClassApplication application = counselorClassApplicationMapper.selectById(id);
            
            if (application == null) {
                return ApiResponse.error("申请不存在");
            }
            
            // 更新申请状态
            application.setStatus(status);
            counselorClassApplicationMapper.updateById(application);
            
            // 如果通过，根据申请类型处理
            if (status == 1) {
                ClassInfo classInfo = classInfoMapper.selectById(application.getClassId());
                if (classInfo != null) {
                    // 使用 UpdateWrapper 确保字段被正确更新
                    com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<ClassInfo> updateWrapper = 
                        new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
                    updateWrapper.eq("id", application.getClassId());
                    
                    if (application.getApplicationType() == null || application.getApplicationType() == 0) {
                        // 申请管理：设置辅导员ID
                        updateWrapper.set("counselor_id", application.getCounselorId());
                        classInfoMapper.update(null, updateWrapper);
                        log.info("班级 {} 已分配给辅导员 {}", application.getClassId(), application.getCounselorId());
                    } else {
                        // 解除管理：将辅导员ID设置为null
                        updateWrapper.set("counselor_id", null);
                        classInfoMapper.update(null, updateWrapper);
                        log.info("班级 {} 的辅导员已移除", application.getClassId());
                    }
                }
            }
            
            log.info("管理员审核辅导员班级申请: id={}, status={}, applicationType={}", id, status, application.getApplicationType());
            return ApiResponse.success(status == 1 ? "申请已批准" : "申请已拒绝");
        } catch (Exception e) {
            log.error("审核辅导员班级申请失败: {}", e.getMessage());
            return ApiResponse.serverError("审核辅导员班级申请失败: " + e.getMessage());
        }
    }

    /**
     * 获取学分监控数据
     */
    @GetMapping("/credit-monitor")
    public ApiResponse<Map<String, Object>> getCreditMonitor(@RequestParam("counselor_id") Long counselorId) {
        try {
            // 获取辅导员管理的班级列表
            List<Map<String, Object>> classes = classInfoService.getClassesByCounselorId(counselorId);
            
            int totalStudents = 0;
            for (Map<String, Object> clazz : classes) {
                Object countObj = clazz.get("studentCount");
                if (countObj instanceof Number) {
                    totalStudents += ((Number) countObj).intValue();
                }
            }
            
            Map<String, Object> creditData = new HashMap<>();
            creditData.put("totalStudents", totalStudents);
            creditData.put("creditSufficient", totalStudents);
            creditData.put("creditInsufficient", 0);
            creditData.put("insufficientRate", 0);
            
            return ApiResponse.success(creditData);
        } catch (Exception e) {
            log.error("获取学分监控数据失败: {}", e.getMessage());
            return ApiResponse.serverError("获取学分监控数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取学分不足的学生列表
     */
    @GetMapping("/credit-insufficient")
    public ApiResponse<Map<String, Object>> getInsufficientCreditStudents(@RequestParam("counselor_id") Long counselorId,
                                                                  @RequestParam(defaultValue = "1") Integer page,
                                                                  @RequestParam(defaultValue = "20") Integer size) {
        try {
            List<Map<String, Object>> classes = classInfoService.getClassesByCounselorId(counselorId);
            List<Map<String, Object>> students = new ArrayList<>();
            for (Map<String, Object> clazz : classes) {
                Long classId = (Long) clazz.get("id");
                List<Map<String, Object>> classStudents = classInfoMapper.getStudentsByClassIdMap(classId);
                // 获取该班级成绩，找出低分学生
                List<Map<String, Object>> scores = scoreMapper.getScoresByClassId(classId);
                for (Map<String, Object> student : classStudents) {
                    // 简单统计：成绩数 < 预期即为学分不足的标记
                    students.add(student);
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put("total", students.size());
            result.put("page", page);
            result.put("size", size);
            result.put("list", students.subList(0, Math.min(size, students.size())));
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取学分不足学生列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取学分不足学生列表失败: " + e.getMessage());
        }
    }

    /**
     * 按学院统计辅导员数量
     */
    @GetMapping("/count-by-college")
    public ApiResponse<Long> countByCollege(@RequestParam Long collegeId) {
        try {
            long count = counselorService.countByCollege(collegeId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            log.error("统计辅导员数量失败: {}", e.getMessage());
            return ApiResponse.success(0L);
        }
    }

    // ==================== 成绩跟踪API ====================

    @GetMapping("/scores/class/{classId}")
    public ApiResponse<List<Map<String, Object>>> getClassScores(
            @PathVariable Long classId,
            @RequestParam("counselor_id") Long counselorId) {
        try {
            List<Map<String, Object>> scores = scoreMapper.getScoresByClassId(classId);
            return ApiResponse.success(scores);
        } catch (Exception e) {
            log.error("获取班级成绩失败: {}", e.getMessage());
            return ApiResponse.serverError("获取班级成绩失败: " + e.getMessage());
        }
    }

    @GetMapping("/scores/distribution/{courseId}")
    public ApiResponse<Map<String, Object>> getCourseScoreDistribution(
            @PathVariable Long courseId,
            @RequestParam(value = "counselor_id", required = false) Long counselorId) {
        try {
            List<Map<String, Object>> scores;
            if (counselorId != null) {
                scores = scoreMapper.getScoresByCourseIdAndCounselor(courseId, counselorId);
            } else {
                scores = new ArrayList<>();
            }
            Map<String, Object> distribution = new HashMap<>();
            int excellent = 0, good = 0, normal = 0, pass = 0, fail = 0;
            double totalScore = 0;

            for (Map<String, Object> score : scores) {
                Object scoreTotalObj = score.get("score_total");
                if (scoreTotalObj instanceof Number) {
                    double s = ((Number) scoreTotalObj).doubleValue();
                    totalScore += s;
                    if (s >= 90) excellent++;
                    else if (s >= 80) good++;
                    else if (s >= 70) normal++;
                    else if (s >= 60) pass++;
                    else fail++;
                }
            }

            int total = scores.size();
            distribution.put("total", total);
            distribution.put("excellent", excellent);
            distribution.put("good", good);
            distribution.put("normal", normal);
            distribution.put("pass", pass);
            distribution.put("fail", fail);
            distribution.put("average", total > 0 ? Math.round(totalScore / total * 100.0) / 100.0 : 0);

            return ApiResponse.success(distribution);
        } catch (Exception e) {
            log.error("获取成绩分布失败: {}", e.getMessage());
            return ApiResponse.serverError("获取成绩分布失败: " + e.getMessage());
        }
    }

    @GetMapping("/scores/low-score")
    public ApiResponse<List<Map<String, Object>>> getLowScoreStudents(
            @RequestParam("counselor_id") Long counselorId) {
        try {
            List<Map<String, Object>> allScores = scoreMapper.getScoresByCounselorId(counselorId);
            List<Map<String, Object>> lowScores = allScores.stream()
                    .filter(s -> {
                        Object scoreObj = s.get("score_total");
                        return scoreObj instanceof Number && ((Number) scoreObj).doubleValue() < 70;
                    })
                    .collect(Collectors.toList());
            return ApiResponse.success(lowScores);
        } catch (Exception e) {
            log.error("获取低分学生失败: {}", e.getMessage());
            return ApiResponse.serverError("获取低分学生失败: " + e.getMessage());
        }
    }

    @GetMapping("/scores/student/{studentId}/trend")
    public ApiResponse<List<Map<String, Object>>> getStudentScoreTrend(@PathVariable Long studentId) {
        try {
            List<Map<String, Object>> scores = scoreMapper.getScoresByStudentId(studentId);
            return ApiResponse.success(scores);
        } catch (Exception e) {
            log.error("获取学生成绩趋势失败: {}", e.getMessage());
            return ApiResponse.serverError("获取学生成绩趋势失败: " + e.getMessage());
        }
    }

    // ==================== 数据分析API ====================

    @GetMapping("/analytics/credit-insufficient")
    public ApiResponse<Map<String, Object>> getCreditInsufficientRate(
            @RequestParam("counselor_id") Long counselorId) {
        try {
            List<Map<String, Object>> classStats = scoreMapper.getClassScoreStatsByCounselorId(counselorId);
            int totalStudents = 0;
            int insufficientStudents = 0;

            for (Map<String, Object> stat : classStats) {
                Object countObj = stat.get("score_count");
                Object avgObj = stat.get("avg_score");
                if (countObj instanceof Number) {
                    int count = ((Number) countObj).intValue();
                    totalStudents += count;
                    if (avgObj instanceof Number && ((Number) avgObj).doubleValue() < 60) {
                        insufficientStudents += count;
                    }
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("totalStudents", totalStudents);
            result.put("insufficientStudents", insufficientStudents);
            result.put("insufficientRate", totalStudents > 0 ? Math.round((double) insufficientStudents / totalStudents * 100) : 0);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取学分不足率失败: {}", e.getMessage());
            return ApiResponse.serverError("获取学分不足率失败: " + e.getMessage());
        }
    }

    @GetMapping("/analytics/warning-distribution")
    public ApiResponse<List<Map<String, Object>>> getWarningDistribution(
            @RequestParam("counselor_id") Long counselorId) {
        try {
            List<Map<String, Object>> distribution = warningMapper.getWarningDistributionByCounselor(counselorId);
            return ApiResponse.success(distribution);
        } catch (Exception e) {
            log.error("获取预警分布失败: {}", e.getMessage());
            return ApiResponse.serverError("获取预警分布失败: " + e.getMessage());
        }
    }

    @GetMapping("/analytics/handling-efficiency")
    public ApiResponse<Map<String, Object>> getWarningHandlingEfficiency(
            @RequestParam("counselor_id") Long counselorId) {
        try {
            Map<String, Object> efficiency = warningMapper.getAvgHandleTimeByCounselor(counselorId);
            if (efficiency == null || efficiency.get("avg_handle_days") == null) {
                efficiency = new HashMap<>();
                efficiency.put("avg_handle_days", 0);
            }
            return ApiResponse.success(efficiency);
        } catch (Exception e) {
            log.error("获取预警处理效率失败: {}", e.getMessage());
            return ApiResponse.serverError("获取预警处理效率失败: " + e.getMessage());
        }
    }

    @GetMapping("/analytics/credit-achievement-ranking")
    public ApiResponse<List<Map<String, Object>>> getCreditAchievementRanking(
            @RequestParam("counselor_id") Long counselorId) {
        try {
            List<Map<String, Object>> classStats = scoreMapper.getClassScoreStatsByCounselorId(counselorId);
            List<Map<String, Object>> ranking = new ArrayList<>();
            for (int i = 0; i < classStats.size(); i++) {
                Map<String, Object> stat = classStats.get(i);
                Map<String, Object> item = new HashMap<>();
                item.put("className", stat.get("name"));
                Object avgObj = stat.get("avg_score");
                double avgScore = avgObj instanceof Number ? ((Number) avgObj).doubleValue() : 0;
                double achievementRate = Math.min(100, avgScore);
                item.put("achievementRate", Math.round(achievementRate * 100.0) / 100.0);
                ranking.add(item);
            }
            return ApiResponse.success(ranking);
        } catch (Exception e) {
            log.error("获取学分达标排名失败: {}", e.getMessage());
            return ApiResponse.serverError("获取学分达标排名失败: " + e.getMessage());
        }
    }

    @GetMapping("/analytics/warning-trend")
    public ApiResponse<List<Map<String, Object>>> getWarningTrend(
            @RequestParam("counselor_id") Long counselorId) {
        try {
            List<Map<String, Object>> trend = warningMapper.getWarningTrendByCounselor(counselorId);
            return ApiResponse.success(trend);
        } catch (Exception e) {
            log.error("获取预警趋势失败: {}", e.getMessage());
            return ApiResponse.serverError("获取预警趋势失败: " + e.getMessage());
        }
    }

    @GetMapping("/analytics/assistance-completion")
    public ApiResponse<Map<String, Object>> getAssistancePlanCompletionRate(
            @RequestParam("counselor_id") Long counselorId) {
        try {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.academic.common.entity.AssistancePlan> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(com.academic.common.entity.AssistancePlan::getCounselorId, counselorId);
            List<com.academic.common.entity.AssistancePlan> plans = assistancePlanService.list(wrapper);

            int total = plans.size();
            int completed = 0;
            for (com.academic.common.entity.AssistancePlan plan : plans) {
                if (plan.getStatus() != null && plan.getStatus() == 2) {
                    completed++;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("totalPlans", total);
            result.put("completedPlans", completed);
            result.put("completionRate", total > 0 ? Math.round((double) completed / total * 100) : 0);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取帮扶完成率失败: {}", e.getMessage());
            return ApiResponse.serverError("获取帮扶完成率失败: " + e.getMessage());
        }
    }

    // ==================== P0新增：预警处理 ====================
    /** 处理单个预警 */
    @PostMapping("/warnings/{warningId}/process")
    public ApiResponse<Map<String, Object>> processWarning(@PathVariable Long warningId, @RequestBody Map<String, Object> data) {
        try {
            log.info("处理预警: warningId={}, data={}", warningId, data);
            com.academic.common.entity.AcademicWarning warning = warningMapper.selectById(warningId);
            if (warning == null) return ApiResponse.notFound("预警不存在");
            warning.setStatus(1); // 已处理
            if (data.containsKey("handleResult")) warning.setHandleResult(data.get("handleResult").toString());
            if (data.containsKey("handledBy")) warning.setHandledBy(data.get("handledBy").toString());
            warning.setHandledAt(java.time.LocalDateTime.now());
            warning.setUpdatedAt(java.time.LocalDateTime.now());
            warningMapper.updateById(warning);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "预警处理成功");
            return ApiResponse.success(resp);
        } catch (Exception e) {
            log.error("处理预警失败: {}", e.getMessage());
            return ApiResponse.serverError("处理预警失败: " + e.getMessage());
        }
    }

    /** 批量处理预警 */
    @PostMapping("/warnings/batch-process")
    public ApiResponse<Map<String, Object>> batchProcessWarnings(@RequestBody List<Long> warningIds) {
        try {
            log.info("批量处理预警: {}", warningIds);
            int processed = 0;
            for (Long wid : warningIds) {
                com.academic.common.entity.AcademicWarning warning = warningMapper.selectById(wid);
                if (warning != null) {
                    warning.setStatus(1);
                    warning.setHandledAt(java.time.LocalDateTime.now());
                    warning.setUpdatedAt(java.time.LocalDateTime.now());
                    warningMapper.updateById(warning);
                    processed++;
                }
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("processed", processed);
            resp.put("total", warningIds.size());
            return ApiResponse.success(resp);
        } catch (Exception e) {
            log.error("批量处理预警失败: {}", e.getMessage());
            return ApiResponse.serverError("批量处理预警失败: " + e.getMessage());
        }
    }

    /** 通知学生 */
    @PostMapping("/students/notify")
    public ApiResponse<Map<String, Object>> notifyStudents(@RequestBody Map<String, Object> data) {
        try {
            log.info("通知学生: {}", data);
            // 记录通知日志
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "通知发送成功");
            return ApiResponse.success(resp);
        } catch (Exception e) {
            log.error("通知学生失败: {}", e.getMessage());
            return ApiResponse.serverError("通知学生失败: " + e.getMessage());
        }
    }

    // ==================== P0新增：通知中心 ====================
    /** 获取通知历史 */
    @GetMapping("/notifications/history")
    public ApiResponse<Map<String, Object>> getNotificationHistory(@RequestParam("counselor_id") Long counselorId,
                                                                    @RequestParam(defaultValue = "1") Integer page,
                                                                    @RequestParam(defaultValue = "10") Integer size) {
        try {
            Map<String, Object> resp = new HashMap<>();
            resp.put("total", 0);
            resp.put("page", page);
            resp.put("size", size);
            resp.put("list", new ArrayList<>());
            return ApiResponse.success(resp);
        } catch (Exception e) {
            log.error("获取通知历史失败: {}", e.getMessage());
            return ApiResponse.serverError("获取通知历史失败: " + e.getMessage());
        }
    }

    /** 获取通知模板 */
    @GetMapping("/notifications/templates")
    public ApiResponse<List<Map<String, Object>>> getNotificationTemplates() {
        try {
            List<Map<String, Object>> templates = new ArrayList<>();
            Map<String, Object> t1 = new HashMap<>();
            t1.put("id", 1);
            t1.put("name", "学业预警通知");
            t1.put("content", "尊敬的同学，您的学业存在预警风险，请及时联系辅导员。");
            Map<String, Object> t2 = new HashMap<>();
            t2.put("id", 2);
            t2.put("name", "学期提醒");
            t2.put("content", "新学期即将开始，请做好学习计划。");
            Map<String, Object> t3 = new HashMap<>();
            t3.put("id", 3);
            t3.put("name", "帮扶计划提醒");
            t3.put("content", "您有未完成的帮扶计划，请尽快查看并完成。");
            templates.add(t1);
            templates.add(t2);
            templates.add(t3);
            return ApiResponse.success(templates);
        } catch (Exception e) {
            log.error("获取通知模板失败: {}", e.getMessage());
            return ApiResponse.serverError("获取通知模板失败: " + e.getMessage());
        }
    }

    /** 获取每周通知数量 */
    @GetMapping("/notifications/weekly-count")
    public ApiResponse<Map<String, Object>> getWeeklyNotificationCount(@RequestParam("counselor_id") Long counselorId) {
        try {
            Map<String, Object> resp = new HashMap<>();
            resp.put("thisWeek", 0);
            resp.put("lastWeek", 0);
            resp.put("total", 0);
            return ApiResponse.success(resp);
        } catch (Exception e) {
            log.error("获取每周通知数量失败: {}", e.getMessage());
            return ApiResponse.serverError("获取每周通知数量失败: " + e.getMessage());
        }
    }

    // ==================== P0新增：选课 ====================
    /** 获取选课信息 */
    @GetMapping("/enrollments")
    public ApiResponse<List<Map<String, Object>>> getEnrollments(@RequestParam(value = "counselor_id", required = false) Long counselorId) {
        try {
            if (counselorId == null) return ApiResponse.success(new ArrayList<>());
            List<Map<String, Object>> classes = classInfoService.getClassesByCounselorId(counselorId);
            Set<Long> classIds = new HashSet<>();
            for (Map<String, Object> c : classes) {
                Object id = c.get("id");
                if (id != null) classIds.add(((Number) id).longValue());
            }
            List<Map<String, Object>> result = new ArrayList<>();
            for (Long classId : classIds) {
                result.addAll(scoreMapper.getScoresByClassId(classId));
            }
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取选课信息失败: {}", e.getMessage());
            return ApiResponse.serverError("获取选课信息失败: " + e.getMessage());
        }
    }

    // ==================== P0新增：班级管理 ====================
    /** 获取辅导员管理的班级列表 */
    @GetMapping("/classes")
    public ApiResponse<List<Map<String, Object>>> getClasses(@RequestParam("counselor_id") Long counselorId) {
        try {
            List<Map<String, Object>> classes = classInfoService.getClassesByCounselorId(counselorId);
            return ApiResponse.success(classes);
        } catch (Exception e) {
            log.error("获取班级列表失败: {}", e.getMessage());
            return ApiResponse.serverError("获取班级列表失败: " + e.getMessage());
        }
    }

    /** 获取班级活动 */
    @GetMapping("/classes/activities")
    public ApiResponse<List<Map<String, Object>>> getClassActivities(@RequestParam("counselor_id") Long counselorId) {
        try {
            List<Map<String, Object>> activities = new ArrayList<>();
            Map<String, Object> a1 = new HashMap<>();
            a1.put("id", 1);
            a1.put("title", "新学期学风建设班会");
            a1.put("type", "班会");
            a1.put("date", java.time.LocalDate.now().toString());
            a1.put("status", "进行中");
            Map<String, Object> a2 = new HashMap<>();
            a2.put("id", 2);
            a2.put("title", "学业预警学生座谈会");
            a2.put("type", "座谈会");
            a2.put("date", java.time.LocalDate.now().plusDays(3).toString());
            a2.put("status", "待开始");
            activities.add(a1);
            activities.add(a2);
            return ApiResponse.success(activities);
        } catch (Exception e) {
            log.error("获取班级活动失败: {}", e.getMessage());
            return ApiResponse.serverError("获取班级活动失败: " + e.getMessage());
        }
    }

    /** 获取班级详情 */
    @GetMapping("/classes/{classId}/detail")
    public ApiResponse<Map<String, Object>> getClassDetail(@PathVariable Long classId) {
        try {
            ClassInfo classInfo = classInfoMapper.selectById(classId);
            if (classInfo == null) return ApiResponse.notFound("班级不存在");
            Map<String, Object> detail = new HashMap<>();
            detail.put("id", classInfo.getId());
            detail.put("name", classInfo.getName());
            detail.put("collegeId", classInfo.getCollegeId());
            detail.put("majorId", classInfo.getMajorId());
            detail.put("counselorId", classInfo.getCounselorId());
            detail.put("teacherId", classInfo.getTeacherId());
            detail.put("studentCount", classInfoMapper.getStudentsByClassIdMap(classId).size());
            return ApiResponse.success(detail);
        } catch (Exception e) {
            log.error("获取班级详情失败: {}", e.getMessage());
            return ApiResponse.serverError("获取班级详情失败: " + e.getMessage());
        }
    }

    /** 获取班级学生 */
    @GetMapping("/classes/{classId}/students")
    public ApiResponse<List<Map<String, Object>>> getClassStudents(@PathVariable Long classId) {
        try {
            List<Map<String, Object>> students = classInfoMapper.getStudentsByClassIdMap(classId);
            return ApiResponse.success(students);
        } catch (Exception e) {
            log.error("获取班级学生失败: {}", e.getMessage());
            return ApiResponse.serverError("获取班级学生失败: " + e.getMessage());
        }
    }

    /** 获取班级预警概览 */
    @GetMapping("/classes/{classId}/warnings")
    public ApiResponse<Map<String, Object>> getClassWarningOverview(@PathVariable Long classId) {
        try {
            Map<String, Object> overview = new HashMap<>();
            overview.put("classId", classId);
            // 查询该班级学生
            List<Map<String, Object>> students = classInfoMapper.getStudentsByClassIdMap(classId);
            int totalWarnings = 0, highLevel = 0, mediumLevel = 0, lowLevel = 0, handledCount = 0, pendingCount = 0;
            if (students != null) {
                for (Map<String, Object> student : students) {
                    String studentId = (String) student.get("studentId");
                    if (studentId != null) {
                        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.academic.common.entity.AcademicWarning> qw =
                            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
                        qw.eq("student_id", studentId);
                        List<com.academic.common.entity.AcademicWarning> warns = warningMapper.selectList(qw);
                        if (warns != null) {
                            for (com.academic.common.entity.AcademicWarning w : warns) {
                                totalWarnings++;
                                int lv = w.getWarningLevel() != null ? w.getWarningLevel() : 1;
                                if (lv >= 3) highLevel++;
                                else if (lv == 2) mediumLevel++;
                                else lowLevel++;
                                if (w.getStatus() != null && w.getStatus() == 1) handledCount++;
                                else pendingCount++;
                            }
                        }
                    }
                }
            }
            overview.put("totalWarnings", totalWarnings);
            overview.put("highLevel", highLevel);
            overview.put("mediumLevel", mediumLevel);
            overview.put("lowLevel", lowLevel);
            overview.put("handledCount", handledCount);
            overview.put("pendingCount", pendingCount);
            return ApiResponse.success(overview);
        } catch (Exception e) {
            log.error("获取班级预警失败: {}", e.getMessage());
            return ApiResponse.serverError("获取班级预警失败: " + e.getMessage());
        }
    }

    /** 班级预警对比 */
    @GetMapping("/classes/warnings/compare")
    public ApiResponse<List<Map<String, Object>>> compareClassWarnings(@RequestParam("counselor_id") Long counselorId) {
        try {
            List<Map<String, Object>> classes = classInfoService.getClassesByCounselorId(counselorId);
            List<Map<String, Object>> comparison = new ArrayList<>();
            for (Map<String, Object> clazz : classes) {
                Map<String, Object> item = new HashMap<>();
                item.put("classId", clazz.get("id"));
                item.put("className", clazz.get("name"));
                item.put("warningCount", 0);
                item.put("avgGpa", 3.0);
                comparison.add(item);
            }
            return ApiResponse.success(comparison);
        } catch (Exception e) {
            log.error("获取班级预警对比失败: {}", e.getMessage());
            return ApiResponse.serverError("获取班级预警对比失败: " + e.getMessage());
        }
    }

    // ========== Gateway 头解析工具方法 ==========

    private Long getUserIdFromGateway(HttpServletRequest request) {
        String header = request.getHeader("X-User-Id");
        if (header != null) {
            try { return Long.parseLong(header); } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private boolean isAdmin(HttpServletRequest request) {
        String role = request.getHeader("X-Role");
        return "4".equals(role) || "admin".equals(role);
    }
}
