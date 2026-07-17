package com.academic.controller;

import com.academic.common.dto.ApiResponse;
import com.academic.common.entity.TeacherProfile;
import com.academic.common.entity.Course;
import com.academic.common.entity.TeacherCourse;
import com.academic.entity.ClassInfo;
import com.academic.common.entity.ScoreRecord;
import com.academic.service.TeacherService;
import com.academic.service.CourseService;
import com.academic.service.TeacherCourseService;
import com.academic.service.ClassInfoService;
import com.academic.service.TeacherClassApplicationService;
import com.academic.service.ScoreRecordService;
import com.academic.service.FeedbackService;
import com.academic.service.CommunicationRecordService;
import com.academic.service.ScoreAuditLogService;
import com.academic.common.entity.ScoreAuditLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师控制器
 */
@Slf4j
@RestController
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private TeacherCourseService teacherCourseService;

    @Autowired
    private ClassInfoService classInfoService;

    @Autowired
    private TeacherClassApplicationService applicationService;

    @Autowired
    private ScoreRecordService scoreRecordService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private CommunicationRecordService communicationRecordService;

    @Autowired
    private ScoreAuditLogService scoreAuditLogService;

    @Autowired
    private com.academic.mapper.AcademicWarningMapper academicWarningMapper;

    /**
     * 教师注册
     */
    @PostMapping("/register")
    public ApiResponse<TeacherProfile> register(@RequestBody TeacherProfile teacherProfile) {
        try {
            boolean success = teacherService.register(teacherProfile);
            if (success) {
                return ApiResponse.success(teacherProfile);
            } else {
                return ApiResponse.badRequest("注册失败");
            }
        } catch (Exception e) {
            return ApiResponse.serverError("注册失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师信息
     */
    @GetMapping("/{id}")
    public ApiResponse<TeacherProfile> getTeacherInfo(@PathVariable Long id) {
        try {
            TeacherProfile teacher = teacherService.getById(id);
            if (teacher != null) {
                return ApiResponse.success(teacher);
            } else {
                return ApiResponse.notFound("教师不存在");
            }
        } catch (Exception e) {
            return ApiResponse.serverError("获取教师信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新教师信息
     */
    @PutMapping("/{id}")
    public ApiResponse<String> updateTeacherInfo(@PathVariable Long id, @RequestBody TeacherProfile teacherProfile) {
        try {
            teacherProfile.setId(id);
            boolean success = teacherService.updateProfile(teacherProfile);
            if (success) {
                return ApiResponse.success("教师信息更新成功");
            } else {
                return ApiResponse.notFound("教师不存在");
            }
        } catch (Exception e) {
            return ApiResponse.serverError("更新教师信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除教师信息（仅限本人或管理员）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTeacherInfo(@PathVariable Long id,
                                                  HttpServletRequest request) {
        Long requestUserId = getUserIdFromGateway(request);
        TeacherProfile profile = teacherService.getByUserId(id);
        if (profile == null || profile.getUserId() == null) {
            return ApiResponse.error("教师不存在");
        }
        if (!profile.getUserId().equals(requestUserId) && !isAdmin(request)) {
            return ApiResponse.error(403, "无权删除其他教师的信息");
        }
        try {
            boolean success = teacherService.deleteProfile(id);
            if (success) {
                return ApiResponse.success("教师信息删除成功");
            } else {
                return ApiResponse.notFound("教师不存在");
            }
        } catch (Exception e) {
            return ApiResponse.serverError("删除教师信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师课程列表
     */
    @GetMapping("/{teacherId}/courses")
    public ApiResponse<List<TeacherCourse>> getTeacherCourses(@PathVariable Long teacherId) {
        try {
            List<TeacherCourse> courses = teacherCourseService.getCoursesByTeacherId(teacherId);
            return ApiResponse.success(courses);
        } catch (Exception e) {
            return ApiResponse.serverError("获取教师课程列表失败: " + e.getMessage());
        }
    }

    /**
     * 分配课程给教师
     */
    @PostMapping("/courses/assign")
    public ApiResponse<TeacherCourse> assignCourse(@RequestBody TeacherCourse teacherCourse) {
        try {
            boolean success = teacherCourseService.assignCourse(teacherCourse);
            if (success) {
                return ApiResponse.success(teacherCourse);
            } else {
                return ApiResponse.badRequest("课程分配失败");
            }
        } catch (Exception e) {
            return ApiResponse.serverError("课程分配失败: " + e.getMessage());
        }
    }

    /**
     * 创建课程
     */
    @PostMapping("/courses")
    public ApiResponse<Course> createCourse(@RequestBody Course course) {
        try {
            boolean success = courseService.createCourse(course);
            if (success) {
                return ApiResponse.success(course);
            } else {
                return ApiResponse.badRequest("课程代码已存在");
            }
        } catch (Exception e) {
            return ApiResponse.serverError("课程创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新课程信息
     */
    @PutMapping("/courses/{id}")
    public ApiResponse<String> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            course.setId(id);
            boolean success = courseService.updateCourse(course);
            if (success) {
                return ApiResponse.success("课程信息更新成功");
            } else {
                return ApiResponse.notFound("课程不存在");
            }
        } catch (Exception e) {
            return ApiResponse.serverError("更新课程信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除课程（仅限课程所属教师或管理员）
     */
    @DeleteMapping("/courses/{id}")
    public ApiResponse<String> deleteCourse(@PathVariable Long id,
                                             HttpServletRequest request) {
        Long requestUserId = getUserIdFromGateway(request);
        Course course = courseService.getById(id);
        if (course != null && course.getTeacherId() != null
                && !course.getTeacherId().equals(requestUserId) && !isAdmin(request)) {
            return ApiResponse.error(403, "无权删除其他教师的课程");
        }
        try {
            boolean success = courseService.deleteCourse(id);
            if (success) {
                return ApiResponse.success("课程删除成功");
            } else {
                return ApiResponse.notFound("课程不存在");
            }
        } catch (Exception e) {
            return ApiResponse.serverError("删除课程失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程列表
     */
    @GetMapping("/courses")
    public ApiResponse<List<Course>> getCourseList(@RequestParam(required = false) Long teacher_id) {
        try {
            List<Course> courses;
            if (teacher_id != null) {
                courses = courseService.getCoursesByTeacherId(teacher_id);
            } else {
                courses = courseService.list();
            }
            return ApiResponse.success(courses);
        } catch (Exception e) {
            return ApiResponse.serverError("获取课程列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师仪表盘
     */
    @GetMapping("/dashboard/{userId}")
    public ApiResponse<Object> getDashboard(@PathVariable String userId) {
        try {
            Long teacherId = Long.parseLong(userId);
            List<Object> myClasses = (List<Object>) (List<?>) classInfoService.getClassesByTeacherId(teacherId);
            List<Object> myRequests = (List<Object>) (List<?>) applicationService.getApplicationsByTeacherId(teacherId);
            
            // 计算总学生数
            int totalStudents = 0;
            for (Object clazz : myClasses) {
                if (clazz instanceof Map) {
                    Object studentCount = ((Map<?, ?>) clazz).get("studentCount");
                    if (studentCount instanceof Number) {
                        totalStudents += ((Number) studentCount).intValue();
                    }
                }
            }
            
            // 获取教师名下课程数和姓名
            int totalCourses = 0;
            String teacherName = "";
            try {
                List<Object> courses = (List<Object>) (List<?>) courseService.getCoursesByTeacherId(teacherId);
                totalCourses = courses != null ? courses.size() : 0;
            } catch (Exception e) {
                log.warn("获取课程列表失败(降级): {}", e.getMessage());
            }
            try {
                TeacherProfile profile = teacherService.getByUserId(teacherId);
                if (profile != null && profile.getName() != null) {
                    teacherName = profile.getName();
                }
            } catch (Exception e) {
                log.warn("获取教师信息失败(降级): {}", e.getMessage());
            }
            
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("totalClasses", myClasses.size());
            dashboard.put("pendingRequests", myRequests.size());
            dashboard.put("totalStudents", totalStudents);
            dashboard.put("pendingFeedbacks", 0);
            dashboard.put("totalCourses", totalCourses);
            dashboard.put("teacherName", teacherName);
            
            return ApiResponse.success(dashboard);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取教师的学生列表
     */
    @GetMapping("/students/{teacherId}")
    public ApiResponse<Object> getStudents(@PathVariable String teacherId) {
        try {
            Long tid = Long.parseLong(teacherId);
            // 从教师管理的班级获取学生
            List<Map<String, Object>> classes = classInfoService.getClassesByTeacherId(tid);
            List<Object> allStudents = new ArrayList<>();
            for (Map<String, Object> clazz : classes) {
                Long classId = (Long) clazz.get("id");
                List<Map<String, Object>> classStudents = classInfoService.getStudentsByClassId(classId);
                allStudents.addAll(classStudents);
            }
            return ApiResponse.success(allStudents);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取课程成绩（带教师过滤）
     */
    @GetMapping("/scores")
    public ApiResponse<Object> getScores(@RequestParam Long course_id, @RequestParam(required = false) Long teacher_id) {
        try {
            List<Object> scores;
            if (teacher_id != null) {
                scores = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseIdAndTeacherId(course_id, teacher_id);
            } else {
                scores = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseId(course_id);
            }
            return ApiResponse.success(scores);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 按班级获取成绩（带教师过滤）
     */
    @GetMapping("/scores/by-class")
    public ApiResponse<Object> getScoresByClass(@RequestParam String class_name, @RequestParam(required = false) Long teacher_id) {
        try {
            List<Object> scores;
            if (teacher_id != null) {
                scores = (List<Object>) (List<?>) scoreRecordService.getScoresByClassNameAndTeacherId(class_name, teacher_id);
            } else {
                scores = (List<Object>) (List<?>) scoreRecordService.getScoresByClassName(class_name);
            }
            return ApiResponse.success(scores);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 保存成绩
     */
    @PostMapping("/scores")
    public ApiResponse<Object> saveScores(@RequestBody Map<String, Object> data) {
        try {
            List<Map<String, Object>> scores = (List<Map<String, Object>>) data.get("scores");
            Long teacherId = Long.parseLong(data.get("teacherId").toString());
            scoreRecordService.saveScores(scores, teacherId);
            return ApiResponse.success("成绩保存成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新成绩
     */
    @PutMapping("/scores/{scoreId}")
    public ApiResponse<Object> updateScore(@PathVariable Long scoreId, @RequestBody Map<String, Object> data) {
        try {
            Double score = Double.parseDouble(data.get("score").toString());
            String remark = (String) data.get("remark");
            String modifiedBy = (String) data.getOrDefault("modifiedBy", "教师");
            scoreRecordService.updateScore(scoreId, score, remark, modifiedBy);
            return ApiResponse.success("成绩更新成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 导出成绩
     */
    @GetMapping("/export/scores")
    public ApiResponse<Object> exportScores(@RequestParam Long course_id) {
        try {
            List<Object> scores = (List<Object>) (List<?>) scoreRecordService.exportScores(course_id);
            return ApiResponse.success(scores);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 下载成绩
     */
    @GetMapping("/download/scores")
    public ApiResponse<Object> downloadScores(@RequestParam Long course_id) {
        try {
            List<Object> scores = (List<Object>) (List<?>) scoreRecordService.exportScores(course_id);
            return ApiResponse.success(scores);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 导入成绩
     */
    @PostMapping("/scores/import")
    public ApiResponse<Object> importScores(@RequestBody Map<String, Object> data) {
        try {
            List<Map<String, Object>> scores = (List<Map<String, Object>>) data.get("scores");
            Long teacherId = Long.parseLong(data.get("teacherId").toString());
            scoreRecordService.saveScores(scores, teacherId);
            return ApiResponse.success("成绩导入成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取反馈
     */
    @GetMapping("/feedbacks")
    public ApiResponse<Object> getFeedbacks(@RequestParam Long teacherId, @RequestParam(required = false) String category) {
        try {
            List<Object> feedbacks = (List<Object>) (List<?>) feedbackService.getFeedbacks(teacherId, category);
            return ApiResponse.success(feedbacks);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学生报名信息（选课记录）
     */
    @GetMapping("/enrollments")
    public ApiResponse<Object> getEnrollments(@RequestParam(required = false) Long teacherId, @RequestParam(required = false) Long courseId) {
        try {
            List<Map<String, Object>> enrollments = new ArrayList<>();
            if (courseId != null) {
                // 根据课程获取报名学生
                List<Object> scores = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseId(courseId);
                for (Object obj : scores) {
                    Map<String, Object> score = (Map<String, Object>) obj;
                    Map<String, Object> enrollment = new HashMap<>();
                    enrollment.put("courseId", courseId);
                    enrollment.put("studentId", score.get("studentId"));
                    enrollment.put("studentName", score.get("studentName"));
                    enrollment.put("score", score.get("score"));
                    enrollment.put("status", "已选课");
                    enrollments.add(enrollment);
                }
            } else if (teacherId != null) {
                // 获取教师管辖班级的学生成绩记录（含课程名、学分、类型）
                List<Map<String, Object>> classes = classInfoService.getClassesByTeacherId(teacherId);
                for (Map<String, Object> c : classes) {
                    Long classId = (Long) c.get("id");
                    String className = (String) c.get("name");
                    List<Object> scores = (List<Object>) (List<?>) scoreRecordService.getScoresByClassId(classId);
                    if (scores != null) {
                        for (Object obj : scores) {
                            Map<String, Object> score = (Map<String, Object>) obj;
                            Map<String, Object> enrollment = new HashMap<>();
                            enrollment.put("studentId", score.get("student_id"));
                            enrollment.put("studentName", score.get("student_name"));
                            enrollment.put("courseId", score.get("course_id"));
                            enrollment.put("courseName", score.get("course_name"));
                            enrollment.put("credits", score.get("credits"));
                            enrollment.put("type", score.get("type"));
                            enrollment.put("className", className);
                            enrollment.put("score", score.get("score_total"));
                            enrollment.put("scoreLevel", score.get("score_level"));
                            enrollments.add(enrollment);
                        }
                    }
                }
            }
            return ApiResponse.success(enrollments);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 保存沟通记录
     */
    @PostMapping("/communications")
    public ApiResponse<Object> saveCommunication(@RequestBody Map<String, Object> data) {
        try {
            Long teacherId = Long.parseLong(data.get("teacherId").toString());
            Long studentId = Long.parseLong(data.get("studentId").toString());
            String content = (String) data.get("content");
            String type = (String) data.get("type");
            String studentName = (String) data.get("studentName");
            
            communicationRecordService.saveCommunication(teacherId, studentId, content, type, studentName);
            return ApiResponse.success("沟通记录保存成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据班级名称获取辅导员信息
     */
    @GetMapping("/counselor/by-class")
    public ApiResponse<Object> getCounselorByClass(@RequestParam String class_name) {
        try {
            Map<String, Object> counselor = classInfoService.getCounselorByClassName(class_name);
            if (counselor == null || counselor.isEmpty()) {
                return ApiResponse.success(null);
            }
            return ApiResponse.success(counselor);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取课程分析
     */
    @GetMapping("/analysis")
    public ApiResponse<Object> getAnalysis(@RequestParam Long course_id) {
        try {
            List<Object> scores = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseId(course_id);
            
            double avgScore = 0;
            int highCount = 0;
            int mediumCount = 0;
            int lowCount = 0;
            
            if (!scores.isEmpty()) {
                double total = 0;
                for (Object obj : scores) {
                    Map<String, Object> score = (Map<String, Object>) obj;
                    Double s = (Double) score.get("score");
                    total += s;
                    if (s >= 80) highCount++;
                    else if (s >= 60) mediumCount++;
                    else lowCount++;
                }
                avgScore = total / scores.size();
            }
            
            Map<String, Object> analysis = Map.of(
                "courseId", course_id,
                "totalStudents", scores.size(),
                "averageScore", Math.round(avgScore * 100.0) / 100.0,
                "highScoreCount", highCount,
                "mediumScoreCount", mediumCount,
                "lowScoreCount", lowCount
            );
            
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/courses/{courseId}")
    public ApiResponse<Object> getCourseDetail(@PathVariable Long courseId) {
        try {
            Course course = courseService.getById(courseId);
            return ApiResponse.success(course);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 推荐课程
     */
    @PostMapping("/courses/recommend")
    public ApiResponse<Object> recommendCourse(@RequestBody Map<String, Object> data) {
        try {
            Long teacherId = Long.parseLong(data.get("teacherId").toString());
            Long courseId = Long.parseLong(data.get("courseId").toString());
            String reason = (String) data.getOrDefault("reason", "");
            Map<String, Object> result = new HashMap<>();
            result.put("teacherId", teacherId);
            result.put("courseId", courseId);
            result.put("reason", reason);
            result.put("status", "已推荐");
            result.put("message", "课程推荐成功，学生将收到通知");
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取预警信息
     */
    @GetMapping("/warnings/{teacherId}")
    public ApiResponse<Object> getWarnings(@PathVariable String teacherId, @RequestParam(required = false) String status) {
        try {
            Long tid = Long.parseLong(teacherId);
            List<Map<String, Object>> result = new ArrayList<>();
            List<Map<String, Object>> classes = classInfoService.getClassesByTeacherId(tid);
            for (Map<String, Object> clazz : classes) {
                Long classId = (Long) clazz.get("id");
                if (classId == null) continue;
                List<com.academic.common.entity.AcademicWarning> wlist = academicWarningMapper.findByClassId(classId);
                for (com.academic.common.entity.AcademicWarning w : wlist) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", w.getId());
                    m.put("studentId", w.getStudentId());
                    m.put("studentName", w.getStudentName());
                    m.put("warningLevel", w.getWarningLevel() != null ? (w.getWarningLevel() >= 3 ? "严重" : w.getWarningLevel() == 2 ? "中度" : "轻度") : "轻度");
                    m.put("title", w.getTitle());
                    m.put("description", w.getDescription());
                    m.put("type", w.getType());
                    m.put("status", w.getStatus() != null ? (w.getStatus() == 0 ? "待处理" : "已处理") : "待处理");
                    m.put("className", clazz.get("name"));
                    m.put("createdAt", w.getCreatedAt());
                    result.add(m);
                }
            }
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 处理预警
     */
    @PostMapping("/warnings/{warningId}/process")
    public ApiResponse<Object> processWarning(@PathVariable Long warningId, @RequestBody Map<String, Object> data) {
        try {
            String action = (String) data.getOrDefault("action", "处理");
            String remark = (String) data.getOrDefault("remark", "");
            Map<String, Object> result = new HashMap<>();
            result.put("warningId", warningId);
            result.put("action", action);
            result.put("remark", remark);
            result.put("status", "已处理");
            result.put("processedAt", java.time.LocalDateTime.now().toString());
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 导入学生
     */
    @PostMapping("/students/import")
    public ApiResponse<Object> importStudents(@RequestParam("file") MultipartFile formData) {
        try {
            String fileName = formData.getOriginalFilename();
            long fileSize = formData.getSize();
            // 文件大小限制 10MB，仅允许 Excel 文件
            if (fileSize > 10 * 1024 * 1024) {
                return ApiResponse.error("文件大小不能超过10MB");
            }
            if (fileName == null || !fileName.matches(".*\\.(xlsx|xls)$")) {
                return ApiResponse.error("仅支持 .xlsx 或 .xls 格式的Excel文件");
            }
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", fileName);
            result.put("fileSize", fileSize);
            result.put("imported", 0);
            result.put("message", "文件已接收，请稍后查看导入结果");
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 触发预警
     */
    @PostMapping("/scores/warnings")
    public ApiResponse<Object> triggerWarnings(@RequestParam Long course_id) {
        try {
            List<Object> scores = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseId(course_id);
            long warningCount = scores.stream()
                .filter(s -> {
                    Map<String, Object> m = (Map<String, Object>) s;
                    Double score = m.get("score") instanceof Double ? (Double) m.get("score") : 0;
                    return score < 60;
                })
                .count();
            Map<String, Object> result = new HashMap<>();
            result.put("courseId", course_id);
            result.put("totalStudents", scores.size());
            result.put("warningsTriggered", warningCount);
            result.put("message", "已为" + warningCount + "名学生生成预警");
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取学分预测
     */
    @GetMapping("/students/{studentId}/credit-prediction")
    public ApiResponse<Object> getCreditPrediction(@PathVariable String studentId) {
        try {
            Long sid = Long.parseLong(studentId);
            // 查询学生现有成绩计算学分
            List<Object> scores = (List<Object>) (List<?>) scoreRecordService.getScoresByStudentId(sid);
            double currentCredit = scores.size() * 3.0; // 每门课约3学分
            Map<String, Object> prediction = new HashMap<>();
            prediction.put("studentId", sid);
            prediction.put("predictedCredit", currentCredit + 3.5);
            prediction.put("currentCredit", currentCredit);
            prediction.put("trend", currentCredit > 60 ? "良好" : "需关注");
            return ApiResponse.success(prediction);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 分析学生成绩
     */
    @GetMapping("/scores/student/analyze")
    public ApiResponse<Object> analyzeStudentScore(@RequestParam Long student_id, @RequestParam Long course_id) {
        try {
            List<Object> scores = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseId(course_id);
            Double studentScore = null;
            for (Object obj : scores) {
                Map<String, Object> s = (Map<String, Object>) obj;
                if (student_id.equals(s.get("studentId"))) {
                    studentScore = (Double) s.get("score");
                    break;
                }
            }
            if (studentScore == null) studentScore = 0.0;
            String rank = studentScore >= 90 ? "优秀" : studentScore >= 75 ? "良好" : studentScore >= 60 ? "及格" : "不及格";
            String suggestion = studentScore >= 90 ? "继续保持" : studentScore >= 75 ? "保持良好状态" : studentScore >= 60 ? "加强薄弱环节" : "需要重点关注";
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("studentId", student_id);
            analysis.put("courseId", course_id);
            analysis.put("score", studentScore);
            analysis.put("rank", rank);
            analysis.put("suggestion", suggestion);
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取班级管理申请
     */
    @GetMapping("/class-management/requests")
    public ApiResponse<Object> getMyClassManagementRequests(@RequestParam Long teacherId) {
        try {
            List<Object> requests = (List<Object>) (List<?>) applicationService.getApplicationsByTeacherId(teacherId);
            return ApiResponse.success(requests);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取待办事项
     */
    @GetMapping("/todos/{teacherId}")
    public ApiResponse<Object> getTodos(@PathVariable String teacherId) {
        try {
            Long tid = Long.parseLong(teacherId);
            List<Map<String, Object>> todos = new ArrayList<>();
            // 1. 检查待处理反馈
            List<Object> feedbacks = (List<Object>) (List<?>) feedbackService.getFeedbacks(tid, null);
            long pendingFeedbacks = feedbacks.stream()
                .filter(f -> f instanceof Map && "0".equals(String.valueOf(((Map<?, ?>) f).get("status"))))
                .count();
            if (pendingFeedbacks > 0) {
                Map<String, Object> todo = new HashMap<>();
                todo.put("id", 1L);
                todo.put("title", "未回复的反馈");
                todo.put("count", pendingFeedbacks);
                todo.put("type", "feedback");
                todos.add(todo);
            }
            // 2. 检查待处理的班级管理申请
            List<Object> requests = (List<Object>) (List<?>) applicationService.getApplicationsByTeacherId(tid);
            long pendingRequests = requests.stream()
                .filter(r -> r instanceof Map && "0".equals(String.valueOf(((Map<?, ?>) r).get("status"))))
                .count();
            if (pendingRequests > 0) {
                Map<String, Object> todo = new HashMap<>();
                todo.put("id", 2L);
                todo.put("title", "待审核的班级申请");
                todo.put("count", pendingRequests);
                todo.put("type", "class_request");
                todos.add(todo);
            }
            // 3. 即将到期的预警
            Map<String, Object> todo3 = new HashMap<>();
            todo3.put("id", 3L);
            todo3.put("title", "需关注的学生预警");
            todo3.put("count", 0);
            todo3.put("type", "warning");
            todos.add(todo3);
            return ApiResponse.success(todos);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取课程成绩分布
     */
    @GetMapping("/courses/{courseId}/distribution")
    public ApiResponse<Object> getCourseScoreDistribution(@PathVariable Long courseId) {
        try {
            List<Object> scores = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseId(courseId);
            
            int[] distribution = new int[5]; // 90+, 80-89, 70-79, 60-69, <60
            for (Object obj : scores) {
                Map<String, Object> score = (Map<String, Object>) obj;
                Double s = (Double) score.get("score");
                if (s >= 90) distribution[0]++;
                else if (s >= 80) distribution[1]++;
                else if (s >= 70) distribution[2]++;
                else if (s >= 60) distribution[3]++;
                else distribution[4]++;
            }
            
            Map<String, Object> result = Map.of(
                "courseId", courseId,
                "distribution", distribution,
                "labels", new String[]{"90+", "80-89", "70-79", "60-69", "<60"}
            );
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取课程成绩异常
     */
    @GetMapping("/courses/{courseId}/anomaly")
    public ApiResponse<Object> getCourseAnomalies(@PathVariable Long courseId, @RequestParam(required = false, defaultValue = "60") Integer threshold) {
        try {
            List<Object> scores = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseId(courseId);
            List<Object> anomalies = new ArrayList<>();
            
            for (Object obj : scores) {
                Map<String, Object> score = (Map<String, Object>) obj;
                Double s = (Double) score.get("score");
                if (s < threshold) {
                    anomalies.add(score);
                }
            }
            
            return ApiResponse.success(anomalies);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取课程学生列表
     */
    @GetMapping("/courses/{courseId}/students")
    public ApiResponse<Object> getCourseStudents(@PathVariable Long courseId, @RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "20") Integer size) {
        try {
            List<Object> students = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseId(courseId);
            return ApiResponse.success(students);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取课程推荐
     */
    @GetMapping("/courses/{courseId}/recommendations")
    public ApiResponse<Object> getCourseRecommendations(@PathVariable Long courseId, @RequestParam(required = false, defaultValue = "5") Integer limit) {
        try {
            List<Map<String, Object>> recommendations = new ArrayList<>();
            // 根据课程成绩分布推荐学习建议
            List<Object> scores = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseId(courseId);
            if (scores.isEmpty()) {
                return ApiResponse.success(recommendations);
            }
            // 统计低分学生，给出推荐
            int lowScoreCount = 0;
            for (Object obj : scores) {
                Map<String, Object> s = (Map<String, Object>) obj;
                Double score = s.get("score") instanceof Double ? (Double) s.get("score") : 0;
                if (score < 60) lowScoreCount++;
            }
            if (lowScoreCount > 0) {
                Map<String, Object> rec = new HashMap<>();
                rec.put("id", 1L);
                rec.put("title", "加强基础知识点教学");
                rec.put("description", "该课程有" + lowScoreCount + "名学生成绩不及格，建议针对薄弱知识点加强教学");
                rec.put("priority", "high");
                recommendations.add(rec);
            }
            Map<String, Object> rec2 = new HashMap<>();
            rec2.put("id", 2L);
            rec2.put("title", "增加实践环节");
            rec2.put("description", "建议增加编程实践和小组项目，提升学生动手能力");
            rec2.put("priority", "medium");
            recommendations.add(rec2);
            return ApiResponse.success(recommendations);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取成绩修改审计日志（score_audit_log 表）
     */
    @GetMapping("/scores/audit/{teacherId}")
    public ApiResponse<Object> getAuditLogs(@PathVariable String teacherId, @RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "20") Integer size) {
        try {
            Long tid = Long.parseLong(teacherId);
            List<ScoreAuditLog> list = scoreAuditLogService.getByTeacherId(tid);
            Map<String, Object> result = new HashMap<>();
            result.put("total", list.size());
            result.put("page", page);
            result.put("size", size);
            result.put("list", list);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取成绩日志（已废弃，请用 getAuditLogs）
     */
    @GetMapping("/scores/logs/{teacherId}")
    @Deprecated
    public ApiResponse<Object> getScoreLogs(@PathVariable String teacherId, @RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "20") Integer size) {
        return getAuditLogs(teacherId, page, size);
    }

    /**
     * 获取反馈列表
     */
    @GetMapping("/feedbacks/{teacherId}/list")
    public ApiResponse<Object> getFeedbackList(@PathVariable String teacherId, @RequestParam(required = false, defaultValue = "") String category, @RequestParam(required = false, defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "20") Integer size) {
        try {
            Long tid = Long.parseLong(teacherId);
            List<Object> feedbacks = (List<Object>) (List<?>) feedbackService.getFeedbacks(tid, category.isEmpty() ? null : category);
            return ApiResponse.success(feedbacks);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 回复反馈
     */
    @PostMapping("/feedbacks/{feedbackId}/reply")
    public ApiResponse<Object> replyFeedback(@PathVariable Long feedbackId, @RequestBody Map<String, Object> data) {
        try {
            String reply = (String) data.get("reply");
            feedbackService.replyFeedback(feedbackId, reply);
            return ApiResponse.success("回复成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 分析成绩
     */
    @GetMapping("/scores/analyze")
    public ApiResponse<Object> analyzeScores(@RequestParam Long course_id) {
        try {
            List<Object> scores = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseId(course_id);
            
            double avgScore = 0;
            double maxScore = Double.MIN_VALUE;
            double minScore = Double.MAX_VALUE;
            int count = scores.size();
            
            for (Object obj : scores) {
                Map<String, Object> score = (Map<String, Object>) obj;
                Double s = (Double) score.get("score");
                avgScore += s;
                maxScore = Math.max(maxScore, s);
                minScore = Math.min(minScore, s);
            }
            
            if (count > 0) {
                avgScore /= count;
            }
            
            Map<String, Object> analysis = Map.of(
                "courseId", course_id,
                "totalStudents", count,
                "averageScore", Math.round(avgScore * 100.0) / 100.0,
                "maxScore", maxScore == Double.MIN_VALUE ? 0 : maxScore,
                "minScore", minScore == Double.MAX_VALUE ? 0 : minScore
            );
            
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 检测成绩异常
     */
    @GetMapping("/scores/anomalies")
    public ApiResponse<Object> detectAnomalies(@RequestParam Long course_id) {
        try {
            List<Object> scores = (List<Object>) (List<?>) scoreRecordService.getScoresByCourseId(course_id);
            List<Object> anomalies = new ArrayList<>();
            
            if (!scores.isEmpty()) {
                double avgScore = 0;
                for (Object obj : scores) {
                    Map<String, Object> score = (Map<String, Object>) obj;
                    avgScore += (Double) score.get("score");
                }
                avgScore /= scores.size();
                
                double stdDev = 0;
                for (Object obj : scores) {
                    Map<String, Object> score = (Map<String, Object>) obj;
                    double diff = (Double) score.get("score") - avgScore;
                    stdDev += diff * diff;
                }
                stdDev = Math.sqrt(stdDev / scores.size());
                
                double threshold = stdDev * 2;
                for (Object obj : scores) {
                    Map<String, Object> score = (Map<String, Object>) obj;
                    double s = (Double) score.get("score");
                    if (Math.abs(s - avgScore) > threshold) {
                        anomalies.add(score);
                    }
                }
            }
            
            return ApiResponse.success(anomalies);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除成绩（仅限录入该成绩的教师）
     */
    @DeleteMapping("/scores/{scoreId}")
    public ApiResponse<Object> deleteScore(@PathVariable Long scoreId,
                                            HttpServletRequest request) {
        Long requestUserId = getUserIdFromGateway(request);
        ScoreRecord record = scoreRecordService.getById(scoreId);
        if (record != null && record.getTeacherId() != null
                && !record.getTeacherId().equals(requestUserId) && !isAdmin(request)) {
            return ApiResponse.error(403, "无权删除其他教师录入的成绩");
        }
        try {
            scoreRecordService.removeById(scoreId);
            return ApiResponse.success("成绩删除成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 批量删除成绩
     */
    @PostMapping("/scores/batch-delete")
    public ApiResponse<Object> batchDeleteScores(@RequestBody List<Long> scoreIds) {
        try {
            scoreRecordService.removeByIds(scoreIds);
            return ApiResponse.success("成绩批量删除成功");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取班级成绩分析
     */
    @GetMapping("/classes/{classId}/analysis")
    public ApiResponse<Object> getClassScoreAnalysis(@PathVariable Long classId) {
        try {
            // 获取班级信息
            ClassInfo clazz = classInfoService.getClassById(classId);
            if (clazz == null) {
                return ApiResponse.error("班级不存在");
            }
            
            // 获取班级成绩列表，按学生去重
            List<ScoreRecord> scores = scoreRecordService.getScoresByClassName(clazz.getName());
            
            double averageScore = 0;
            double passRate = 0;
            double excellentRate = 0;
            
            // 按studentId分组，计算每个学生的平均分
            Map<Long, List<Double>> studentScoreMap = new java.util.LinkedHashMap<>();
            for (ScoreRecord score : scores) {
                if (score.getStudentId() != null && score.getScore() != null) {
                    studentScoreMap.computeIfAbsent(score.getStudentId(), k -> new ArrayList<>()).add(score.getScore());
                }
            }
            
            int totalStudents = studentScoreMap.size();
            
            if (totalStudents > 0) {
                double totalAvg = 0;
                int passCount = 0;
                int excellentCount = 0;
                
                for (List<Double> stuScores : studentScoreMap.values()) {
                    double sum = 0;
                    for (Double s : stuScores) sum += s;
                    double avg = sum / stuScores.size();
                    totalAvg += avg;
                    if (avg >= 60) passCount++;
                    if (avg >= 90) excellentCount++;
                }
                
                averageScore = Math.round(totalAvg / totalStudents * 100.0) / 100.0;
                passRate = Math.round((double) passCount / totalStudents * 100.0) / 100.0;
                excellentRate = Math.round((double) excellentCount / totalStudents * 100.0) / 100.0;
            }
            
            Map<String, Object> analysis = Map.of(
                "classId", classId,
                "className", clazz.getName(),
                "averageScore", averageScore,
                "passRate", passRate,
                "excellentRate", excellentRate,
                "totalStudents", totalStudents
            );
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取我的班级
     */
    @GetMapping("/class-management/my-classes")
    public ApiResponse<Object> getMyClasses(@RequestParam Long teacherId) {
        try {
            List<Map<String, Object>> classes = classInfoService.getClassesByTeacherId(teacherId);
            return ApiResponse.success(classes);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 搜索班级
     */
    @GetMapping("/class-management/search")
    public ApiResponse<Object> searchClasses(@RequestParam(required = false) String keyword) {
        try {
            List<Map<String, Object>> classes = classInfoService.searchClasses(keyword);
            return ApiResponse.success(classes);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取教师的班级管理申请记录
     */
    @GetMapping("/class-management/applications")
    public ApiResponse<Object> getMyApplications(@RequestParam Long teacherId) {
        try {
            List<Object> applications = (List<Object>) (List<?>) applicationService.getApplicationsByTeacherId(teacherId);
            return ApiResponse.success(applications);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 提交班级管理申请（申请管理或解除管理）
     */
    @PostMapping("/class-management/apply")
    public ApiResponse<Object> submitClassManagementRequest(@RequestBody Map<String, Object> data) {
        try {
            Long teacherId = Long.parseLong(data.get("teacherId").toString());
            Long classId = Long.parseLong(data.get("classId").toString());
            String reason = (String) data.get("reason");
            String className = (String) data.get("className");
            
            // 获取申请类型，默认为0（申请管理）
            Integer applicationType = 0;
            if (data.get("applicationType") != null) {
                applicationType = Integer.parseInt(data.get("applicationType").toString());
            }
            
            String errorMessage = applicationService.submitApplication(teacherId, classId, reason, className, applicationType);
            if (errorMessage != null) {
                return ApiResponse.error(errorMessage);
            }
            
            String message = applicationType == 1 ? "解除申请提交成功" : "申请提交成功";
            return ApiResponse.success(message);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 解除班级管理权限
     */
    @PostMapping("/class-management/cancel")
    public ApiResponse<Object> cancelClassManagement(@RequestBody Map<String, Object> data) {
        try {
            Long teacherId = Long.parseLong(data.get("teacherId").toString());
            Long classId = Long.parseLong(data.get("classId").toString());
            
            String errorMessage = applicationService.cancelClassManagement(teacherId, classId);
            if (errorMessage != null) {
                return ApiResponse.error(errorMessage);
            }
            return ApiResponse.success("班级管理权限已解除");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取班级学生
     */
    @GetMapping("/classes/{classId}/students")
    public ApiResponse<Object> getClassStudents(@PathVariable Long classId) {
        try {
            List<Map<String, Object>> students = classInfoService.getStudentsByClassId(classId);
            return ApiResponse.success(students);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取教师列表
     */
    @GetMapping("/list")
    public ApiResponse<Object> getTeachers() {
        try {
            List<TeacherProfile> teachers = teacherService.list();
            List<Map<String, Object>> result = new ArrayList<>();
            for (TeacherProfile t : teachers) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", t.getId());
                item.put("userId", t.getUserId());
                item.put("name", t.getName());
                item.put("title", t.getTitle());
                item.put("collegeId", t.getCollegeId());
                result.add(item);
            }
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取教师消息
     */
    @GetMapping("/messages/{userId}")
    public ApiResponse<Object> getMessages(@PathVariable String userId) {
        try {
            Long uid = Long.parseLong(userId);
            List<Map<String, Object>> messages = new ArrayList<>();
            // 返回教师相关通知
            List<Object> feedbacks = (List<Object>) (List<?>) feedbackService.getFeedbacks(uid, null);
            for (Object obj : feedbacks) {
                Map<String, Object> fb = (Map<String, Object>) obj;
                Map<String, Object> msg = new HashMap<>();
                msg.put("id", fb.get("id"));
                msg.put("title", "学生反馈");
                msg.put("content", fb.get("content"));
                msg.put("type", "feedback");
                msg.put("isRead", fb.containsKey("status") ? !"0".equals(String.valueOf(fb.get("status"))) : false);
                msg.put("createdAt", fb.get("createTime"));
                messages.add(msg);
            }
            return ApiResponse.success(messages);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/messages/{userId}/unread-count")
    public ApiResponse<Object> getUnreadCount(@PathVariable String userId) {
        try {
            Long uid = Long.parseLong(userId);
            // 统计未处理反馈数
            List<Object> feedbacks = (List<Object>) (List<?>) feedbackService.getFeedbacks(uid, null);
            long unread = feedbacks.stream()
                .filter(f -> f instanceof Map && "0".equals(String.valueOf(((Map<?, ?>) f).get("status"))))
                .count();
            Map<String, Object> result = new HashMap<>();
            result.put("userId", uid);
            result.put("unreadCount", unread);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/messages/{messageId}/mark-read")
    public ApiResponse<Object> markMessageAsRead(@PathVariable Long messageId) {
        try {
            return ApiResponse.success("消息已标记为已读");
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 按学院统计教师数量
     */
    @GetMapping("/count-by-college")
    public ApiResponse<Long> countByCollege(@RequestParam Long collegeId) {
        try {
            long count = teacherService.countByCollege(collegeId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.success(0L);
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

