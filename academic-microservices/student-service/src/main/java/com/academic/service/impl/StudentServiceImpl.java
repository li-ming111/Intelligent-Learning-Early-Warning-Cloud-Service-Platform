package com.academic.service.impl;

import com.academic.common.entity.Score;
import com.academic.common.entity.AcademicWarning;
import com.academic.dto.StudentDashboardResponse;
import com.academic.entity.*;
import com.academic.mapper.*;
import com.academic.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
@Primary
public class StudentServiceImpl implements StudentService {

    private final StudentProfileMapper studentProfileMapper;
    private final ScoreMapper scoreMapper;
    private final AcademicWarningMapper academicWarningMapper;
    private final ScoreAppealMapper scoreAppealMapper;
    private final BenchmarkAnalysisMapper benchmarkAnalysisMapper;
    private final NotificationMapper notificationMapper;
    private final SubscriptionPreferenceMapper subscriptionPreferenceMapper;
    private final AssistanceEvaluationMapper assistanceEvaluationMapper;

    public StudentServiceImpl(StudentProfileMapper studentProfileMapper,
                              ScoreMapper scoreMapper,
                              AcademicWarningMapper academicWarningMapper,
                              ScoreAppealMapper scoreAppealMapper,
                              BenchmarkAnalysisMapper benchmarkAnalysisMapper,
                              NotificationMapper notificationMapper,
                              SubscriptionPreferenceMapper subscriptionPreferenceMapper,
                              AssistanceEvaluationMapper assistanceEvaluationMapper) {
        this.studentProfileMapper = studentProfileMapper;
        this.scoreMapper = scoreMapper;
        this.academicWarningMapper = academicWarningMapper;
        this.scoreAppealMapper = scoreAppealMapper;
        this.benchmarkAnalysisMapper = benchmarkAnalysisMapper;
        this.notificationMapper = notificationMapper;
        this.subscriptionPreferenceMapper = subscriptionPreferenceMapper;
        this.assistanceEvaluationMapper = assistanceEvaluationMapper;
    }

    // ==================== 仪表盘 ====================
    @Override
    public StudentDashboardResponse getStudentDashboard(Long studentId) {
        log.info("获取学生仪表盘数据: userId={}", studentId);
        StudentProfile sp = studentProfileMapper.selectByUserId(studentId);
        if (sp == null) {
            log.warn("学生不存在: userId={}", studentId);
            return new StudentDashboardResponse();
        }
        Long profileId = sp.getId();
        List<Score> scores = scoreMapper.selectByStudentId(profileId.toString());
        int totalCourses = scores.size();
        int failedCourses = 0;
        double totalScore = 0;
        int completedCredits = 0;
        for (Score s : scores) {
            if (s.getScoreTotal() != null && s.getScoreTotal().doubleValue() < 60) failedCourses++;
            totalScore += s.getScoreTotal() != null ? s.getScoreTotal().doubleValue() : 0;
            completedCredits += s.getCredits() != null ? s.getCredits().intValue() : 0;
        }
        double gpa = totalCourses > 0 ? totalScore / totalCourses / 20 : 0;
        List<AcademicWarning> warnings = academicWarningMapper.selectPendingByStudentId(profileId.toString());
        int wc = warnings.size();
        String wl = "正常";
        if (wc > 0 && warnings.get(0).getWarningLevel() != null) wl = warnings.get(0).getWarningLevel().toString();

        // 最近预警列表
        java.util.List<java.util.Map<String, Object>> recentWarnings = new java.util.ArrayList<>();
        for (AcademicWarning w : warnings) {
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", w.getId());
            item.put("title", w.getDescription() != null && w.getDescription().length() > 20 
                ? w.getDescription().substring(0, 20) : (w.getDescription() != null ? w.getDescription() : "学业预警"));
            item.put("description", w.getDescription());
            item.put("warningLevel", w.getWarningLevel() != null ? w.getWarningLevel() : 1);
            item.put("createdAt", w.getCreatedAt() != null ? w.getCreatedAt().toString() : "");
            recentWarnings.add(item);
        }

        StudentDashboardResponse resp = new StudentDashboardResponse();
        resp.setStudentId(studentId);
        resp.setStudentName(sp.getName());
        resp.setClassName(sp.getClassName() != null ? sp.getClassName() : "未知班级");
        resp.setMajorName("计算机科学与技术");
        resp.setGpa(BigDecimal.valueOf(gpa).setScale(2, RoundingMode.HALF_UP).doubleValue());
        resp.setWarningCount(wc);
        resp.setWarningLevel(wl);
        resp.setFailedCoursesCount(failedCourses);
        resp.setTotalCoursesCount(totalCourses);
        resp.setCompletedCredits(completedCredits);
        resp.setRequiredCredits(120);
        resp.setRecentWarnings(recentWarnings);
        return resp;
    }

    // ==================== 成绩 ====================
    @Override
    public Object getStudentScores(Long studentId) { return getStudentScores(studentId, null); }

    @Override
    public Object getStudentScores(Long studentId, String semester) {
        log.info("获取学生成绩: userId={}, semester={}", studentId, semester);
        StudentProfile sp = studentProfileMapper.selectByUserId(studentId);
        if (sp == null) return Collections.emptyList();
        Long profileId = sp.getId();
        List<Score> scores;
        if (semester != null && !semester.isEmpty()) {
            Integer year = null;
            Integer sem = null;
            String[] parts = semester.split("-");
            if (parts.length >= 3) {
                try { year = Integer.parseInt(parts[0]); } catch (NumberFormatException ignored) {}
                try { sem = Integer.parseInt(parts[2]); } catch (NumberFormatException ignored) {}
            }
            scores = scoreMapper.selectByStudentIdAndSemester(profileId.toString(), year, sem);
        } else {
            scores = scoreMapper.selectByStudentId(profileId.toString());
        }
        return scores;
    }

    // ==================== 预警 ====================
    @Override
    public Object getStudentWarnings(Long studentId) {
        log.info("获取学生预警: userId={}", studentId);
        StudentProfile sp = studentProfileMapper.selectByUserId(studentId);
        if (sp == null) return Collections.emptyList();
        return academicWarningMapper.selectByStudentId(sp.getId().toString());
    }

    // ==================== 学生信息 ====================
    @Override
    public Object getStudentInfo(Long studentId) {
        log.info("获取学生信息: userId={}", studentId);
        StudentProfile sp = studentProfileMapper.selectByUserId(studentId);
        if (sp == null) return Map.of("error", "学生不存在");
        return sp;
    }

    @Override
    public Object registerStudent(Object request) {
        log.info("学生注册");
        return "注册成功";
    }

    // ==================== 对标分析 ====================
    @Override
    public Object getHistoryBenchmark(Long studentId) {
        log.info("获取历史GPA数据: studentId={}", studentId);
        List<Score> scores = scoreMapper.selectByStudentId(studentId.toString());
        Map<String, List<Score>> bySemester = new LinkedHashMap<>();
        for (Score s : scores) {
            String sem = String.valueOf(s.getSemester());
            bySemester.computeIfAbsent(sem, k -> new ArrayList<>()).add(s);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<Score>> e : bySemester.entrySet()) {
            double ts = 0; int cc = 0;
            for (Score s : e.getValue()) { ts += s.getScoreTotal() != null ? s.getScoreTotal().doubleValue() : 0; cc++; }
            Map<String, Object> bm = new HashMap<>();
            bm.put("semester", e.getKey());
            bm.put("studentGpa", BigDecimal.valueOf(cc > 0 ? ts / cc / 20 : 0).setScale(3, RoundingMode.HALF_UP));
            result.add(bm);
        }
        result.sort((a, b) -> b.get("semester").toString().compareTo(a.get("semester").toString()));
        return result;
    }

    @Override
    public Object getLatestBenchmark(Long studentId) {
        log.info("获取最新对标分析: studentId={}", studentId);
        BenchmarkAnalysis ba = benchmarkAnalysisMapper.selectLatestByStudentId(studentId);
        if (ba == null) {
            log.warn("学生无对标分析数据: studentId={}", studentId);
            return Collections.emptyMap();
        }
        return baToMap(ba);
    }

    @Override
    public Object getSemesterBenchmark(Long studentId, String semester) {
        log.info("获取学期对标分析: studentId={}, semester={}", studentId, semester);
        BenchmarkAnalysis ba = benchmarkAnalysisMapper.selectByStudentIdAndSemester(studentId, semester);
        if (ba != null) return baToMap(ba);

        // fallback: 从成绩计算
        Integer year = null;
        Integer sem = null;
        if (semester != null && !semester.isEmpty()) {
            String[] parts = semester.split("-");
            if (parts.length >= 3) {
                try { year = Integer.parseInt(parts[0]); } catch (NumberFormatException ignored) {}
                try { sem = Integer.parseInt(parts[2]); } catch (NumberFormatException ignored) {}
            }
        }
        List<Score> scores = scoreMapper.selectByStudentIdAndSemester(studentId.toString(), year, sem);
        double ts = 0; int cc = 0;
        List<Map<String, Object>> courseDetails = new ArrayList<>();
        for (Score s : scores) {
            double sc = s.getScoreTotal() != null ? s.getScoreTotal().doubleValue() : 0;
            ts += sc; cc++;
            Map<String, Object> cd = new HashMap<>();
            cd.put("courseName", "课程" + s.getCourseId());
            cd.put("studentScore", sc);
            cd.put("classAverage", 75.0);
            cd.put("departmentAverage", 72.0);
            cd.put("rank", 10);
            cd.put("totalStudents", 100);
            courseDetails.add(cd);
        }
        double gpa = BigDecimal.valueOf(cc > 0 ? ts / cc / 20 : 0).setScale(2, RoundingMode.HALF_UP).doubleValue();
        Map<String, Object> bm = new HashMap<>();
        bm.put("studentId", studentId);
        bm.put("semester", semester);
        bm.put("gpa", gpa);
        bm.put("classAverage", 3.0);
        bm.put("departmentAverage", 2.9);
        bm.put("rank", 15);
        bm.put("totalStudents", 100);
        bm.put("courses", courseDetails);
        return bm;
    }

    @Override
    public Object getBenchmarkClassRanking(Long studentId, Long classId, String semester) {
        log.info("获取班级排名: studentId={}, classId={}, semester={}", studentId, classId, semester);
        if (classId == null) {
            StudentProfile sp = studentProfileMapper.selectByStudentId(studentId.toString());
            if (sp != null) classId = sp.getClassId();
        }
        List<BenchmarkAnalysis> list = benchmarkAnalysisMapper.selectClassComparison(classId, semester);
        int rank = 0, total = list.size();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStudentId().equals(studentId)) { rank = i + 1; break; }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("classId", classId);
        result.put("semester", semester);
        result.put("rank", rank);
        result.put("total", total);
        result.put("list", list.stream().map(this::baBriefMap).toList());
        return result;
    }

    @Override
    public Object getBenchmarkMajorRanking(Long studentId, Long majorId, String semester) {
        log.info("获取专业排名: studentId={}, majorId={}, semester={}", studentId, majorId, semester);
        if (majorId == null) {
            StudentProfile sp = studentProfileMapper.selectByStudentId(studentId.toString());
            if (sp != null) majorId = sp.getMajorId();
        }
        List<BenchmarkAnalysis> list = benchmarkAnalysisMapper.selectMajorComparison(majorId, semester);
        int rank = 0, total = list.size();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStudentId().equals(studentId)) { rank = i + 1; break; }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("majorId", majorId);
        result.put("semester", semester);
        result.put("rank", rank);
        result.put("total", total);
        result.put("list", list.stream().map(this::baBriefMap).toList());
        return result;
    }

    @Override
    public Object getClassBenchmarkComparison(Long classId, String semester) {
        log.info("获取班级对标对比: classId={}, semester={}", classId, semester);
        List<BenchmarkAnalysis> list = benchmarkAnalysisMapper.selectClassComparison(classId, semester);
        double avg = list.stream().mapToDouble(b -> b.getStudentGpa() != null ? b.getStudentGpa().doubleValue() : 0).average().orElse(0);
        Map<String, Object> result = new HashMap<>();
        result.put("classId", classId);
        result.put("semester", semester);
        result.put("averageGpa", BigDecimal.valueOf(avg).setScale(3, RoundingMode.HALF_UP));
        result.put("count", list.size());
        result.put("list", list.stream().map(this::baBriefMap).toList());
        return result;
    }

    @Override
    public Object getMajorBenchmarkComparison(Long majorId, String semester) {
        log.info("获取专业对标对比: majorId={}, semester={}", majorId, semester);
        List<BenchmarkAnalysis> list = benchmarkAnalysisMapper.selectMajorComparison(majorId, semester);
        double avg = list.stream().mapToDouble(b -> b.getStudentGpa() != null ? b.getStudentGpa().doubleValue() : 0).average().orElse(0);
        Map<String, Object> result = new HashMap<>();
        result.put("majorId", majorId);
        result.put("semester", semester);
        result.put("averageGpa", BigDecimal.valueOf(avg).setScale(3, RoundingMode.HALF_UP));
        result.put("count", list.size());
        result.put("list", list.stream().map(this::baBriefMap).toList());
        return result;
    }

    @Override
    public Object getProgressReport(Long studentId) {
        log.info("获取进度报告: studentId={}", studentId);
        List<Score> scores = scoreMapper.selectByStudentId(studentId.toString());
        int total = 40, completed = (int) scores.stream().filter(s -> s.getScoreTotal() != null && s.getScoreTotal().doubleValue() >= 60).count();
        double rate = total > 0 ? (double) completed / total * 100 : 0;
        Map<String, List<Score>> bySemester = new LinkedHashMap<>();
        for (Score s : scores) bySemester.computeIfAbsent(String.valueOf(s.getSemester()), k -> new ArrayList<>()).add(s);
        List<Map<String, Object>> semesters = new ArrayList<>();
        for (Map.Entry<String, List<Score>> e : bySemester.entrySet()) {
            int sc = (int) e.getValue().stream().filter(s -> s.getScoreTotal() != null && s.getScoreTotal().doubleValue() >= 60).count();
            Map<String, Object> si = new HashMap<>();
            si.put("semester", e.getKey());
            si.put("completedCredits", sc);
            si.put("requiredCredits", e.getValue().size());
            si.put("completionRate", e.getValue().isEmpty() ? 0 : (double) sc / e.getValue().size() * 100);
            semesters.add(si);
        }
        semesters.sort((a, b) -> b.get("semester").toString().compareTo(a.get("semester").toString()));
        Map<String, Object> pr = new HashMap<>();
        pr.put("studentId", studentId);
        pr.put("totalCredits", total);
        pr.put("completedCredits", completed);
        pr.put("remainingCredits", total - completed);
        pr.put("completionRate", BigDecimal.valueOf(rate).setScale(1, RoundingMode.HALF_UP));
        pr.put("semesters", semesters);
        return pr;
    }

    // ==================== 班级信息 ====================
    @Override
    public Object getClassInfo(Long studentId) {
        log.info("获取班级信息: studentId={}", studentId);
        StudentProfile sp = studentProfileMapper.selectByStudentId(studentId.toString());
        if (sp == null) return new HashMap<>();
        Map<String, Object> ci = new HashMap<>();
        ci.put("classIdentifier", sp.getClassName() != null ? sp.getClassName() : "未知班级");
        ci.put("classId", sp.getClassId());
        ci.put("rankInClass", "1/45");
        return ci;
    }

    // ==================== 帮扶计划 ====================
    @Override
    public Object getAssistancePlans(Long studentId) {
        log.info("获取帮扶计划: studentId={}", studentId);
        List<AcademicWarning> warnings = academicWarningMapper.selectByStudentId(studentId.toString());
        List<Map<String, Object>> plans = new ArrayList<>();
        for (AcademicWarning w : warnings) {
            Map<String, Object> plan = new HashMap<>();
            plan.put("id", w.getId());
            plan.put("title", (w.getWarningLevel() != null ? w.getWarningLevel() : "未知") + "级帮扶计划");
            plan.put("status", "进行中");
            plan.put("progress", 50);
            plans.add(plan);
        }
        return plans;
    }

    // ==================== 学业建议 ====================
    @Override
    public Object getSuggestions(Long studentId) {
        log.info("获取学业建议: studentId={}", studentId);
        List<Score> scores = scoreMapper.selectByStudentId(studentId.toString());
        List<Map<String, Object>> suggestions = new ArrayList<>();
        long id = 1;
        for (Score s : scores) {
            if (s.getScoreTotal() != null && s.getScoreTotal().doubleValue() < 60) {
                Map<String, Object> sug = new HashMap<>();
                sug.put("id", id++);
                sug.put("content", "建议加强课程" + s.getCourseId() + "的学习，多做练习题");
                sug.put("priority", "high");
                suggestions.add(sug);
            } else if (s.getScoreTotal() != null && s.getScoreTotal().doubleValue() < 70) {
                Map<String, Object> sug = new HashMap<>();
                sug.put("id", id++);
                sug.put("content", "建议适当增加课程" + s.getCourseId() + "的学习时间");
                sug.put("priority", "medium");
                suggestions.add(sug);
            }
        }
        if (suggestions.isEmpty()) {
            Map<String, Object> sug = new HashMap<>();
            sug.put("id", id++);
            sug.put("content", "建议参加更多的编程实践项目，提升实际动手能力");
            sug.put("priority", "low");
            suggestions.add(sug);
        }
        return suggestions;
    }

    // ==================== 申诉 ====================
    @Override
    public Object submitAppeal(Object request) {
        log.info("提交成绩申诉: {}", request);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> req = (Map<String, Object>) request;
            ScoreAppeal appeal = new ScoreAppeal();
            // 前端传来的是 userId，通过 StudentProfile 查找真实 studentId
            Long uid = req.containsKey("studentId") ? Long.valueOf(req.get("studentId").toString()) : null;
            if (uid != null) {
                StudentProfile sp = studentProfileMapper.selectByUserId(uid);
                if (sp != null) {
                    appeal.setStudentId(sp.getId());
                } else {
                    return Map.of("error", "学生档案不存在");
                }
            }
            if (req.containsKey("scoreId")) appeal.setScoreId(Long.valueOf(req.get("scoreId").toString()));
            if (req.containsKey("reason")) appeal.setReason(req.get("reason").toString());
            if (req.containsKey("courseId")) appeal.setCourseId(Long.valueOf(req.get("courseId").toString()));
            if (req.containsKey("attachments")) appeal.setAttachments(req.get("attachments").toString());
            appeal.setStatus(0); // pending
            appeal.setCreatedAt(java.time.LocalDateTime.now());
            appeal.setUpdatedAt(java.time.LocalDateTime.now());
            scoreAppealMapper.insert(appeal);
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", appeal.getId());
            resp.put("message", "申诉提交成功");
            return resp;
        } catch (Exception e) {
            log.error("提交申诉失败: {}", e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    @Override
    public Object getStudentAppeals(Long studentId) {
        log.info("获取学生申诉列表: studentId={}", studentId);
        // studentId 参数实际是 userId，通过 StudentProfile 查找真实 studentId
        StudentProfile sp = studentProfileMapper.selectByUserId(studentId);
        if (sp == null) return List.of();
        List<ScoreAppeal> list = scoreAppealMapper.selectByStudentId(sp.getId());
        return list.stream().map(this::appealToMap).toList();
    }

    @Override
    public Object getAppealDetail(Long appealId) {
        log.info("获取申诉详情: appealId={}", appealId);
        ScoreAppeal a = scoreAppealMapper.selectById(appealId);
        return a != null ? appealToMap(a) : Map.of("error", "申诉不存在");
    }

    @Override
    public Object withdrawAppeal(Long appealId, Long userId) {
        log.info("撤销申诉: appealId={}, userId={}", appealId, userId);
        Long sid = resolveStudentProfileId(userId);
        if (sid == null) return Map.of("success", false, "message", "学生档案不存在");
        int rows = scoreAppealMapper.withdrawAppeal(appealId, sid);
        return Map.of("success", rows > 0, "message", rows > 0 ? "撤销成功" : "撤销失败");
    }

    @Override
    public Object getPendingAppeals(Long userId) {
        log.info("获取待处理申诉: userId={}", userId);
        Long sid = resolveStudentProfileId(userId);
        if (sid == null) return List.of();
        List<ScoreAppeal> list = scoreAppealMapper.selectPendingByStudentId(sid);
        return list.stream().map(this::appealToMap).toList();
    }

    @Override
    public Object getAppealStatistics(Long userId) {
        log.info("获取申诉统计: userId={}", userId);
        Long sid = resolveStudentProfileId(userId);
        if (sid == null) return Map.of("total", 0, "pending", 0, "approved", 0, "rejected", 0);
        int total = scoreAppealMapper.countByStudentId(sid);
        int pending = scoreAppealMapper.selectPendingByStudentId(sid).size();
        int success = scoreAppealMapper.countSuccessByStudentId(sid);
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("approved", success);
        stats.put("rejected", total - pending - success);
        return stats;
    }

    @Override
    public Object getAppealSuccessRate(Long userId) {
        log.info("获取申诉成功率: userId={}", userId);
        Long sid = resolveStudentProfileId(userId);
        if (sid == null) { Map<String, Object> r = new HashMap<>(); r.put("rate", 0); r.put("total", 0); r.put("success", 0); return r; }
        int total = scoreAppealMapper.countByStudentId(sid);
        int success = scoreAppealMapper.countSuccessByStudentId(sid);
        double rate = total > 0 ? (double) success / total * 100 : 0;
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("success", success);
        result.put("rate", BigDecimal.valueOf(rate).setScale(1, RoundingMode.HALF_UP));
        return result;
    }

    @Override
    public Object getAppealsByStatus(Long userId, Integer status) {
        log.info("按状态获取申诉: userId={}, status={}", userId, status);
        Long sid = resolveStudentProfileId(userId);
        if (sid == null) return List.of();
        List<ScoreAppeal> list = scoreAppealMapper.selectByStudentIdAndStatus(sid, status);
        return list.stream().map(this::appealToMap).toList();
    }

    @Override
    public Object getAppealHistory(Long userId, int page, int pageSize) {
        log.info("获取申诉历史: userId={}, page={}, pageSize={}", userId, page, pageSize);
        Long sid = resolveStudentProfileId(userId);
        if (sid == null) { Map<String, Object> r = new HashMap<>(); r.put("total", 0); r.put("list", List.of()); return r; }
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ScoreAppeal> p = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize);
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ScoreAppeal>()
            .eq(ScoreAppeal::getStudentId, sid)
            .orderByDesc(ScoreAppeal::getCreatedAt);
        var ip = scoreAppealMapper.selectPage(p, wrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("total", ip.getTotal());
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("list", ip.getRecords().stream().map(this::appealToMap).toList());
        return result;
    }

    @Override
    public Object getAppealReasonStatistics(Long userId) {
        log.info("获取申诉原因统计: userId={}", userId);
        Long sid = resolveStudentProfileId(userId);
        if (sid == null) return List.of();
        List<Map<String, Object>> rawList = scoreAppealMapper.countByReason(sid);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rawList) {
            Map<String, Object> item = new HashMap<>();
            item.put("reason", row.get("reason"));
            item.put("count", row.get("cnt"));
            result.add(item);
        }
        return result;
    }

    // ==================== 通知中心 ====================
    @Override
    public Object getNotificationCenterList(Long userId, int page, int pageSize) {
        log.info("获取通知列表: userId={}, page={}, pageSize={}", userId, page, pageSize);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Notification> p =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize);
        var wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Notification>()
            .eq(Notification::getUserId, userId)
            .eq(Notification::getIsDeleted, 0)
            .orderByDesc(Notification::getCreatedAt);
        var ip = notificationMapper.selectPage(p, wrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("total", ip.getTotal());
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("list", ip.getRecords().stream().map(this::notificationToMap).toList());
        return result;
    }

    @Override
    public Object getNotificationUnreadCount(Long userId) {
        log.info("获取未读通知数量: userId={}", userId);
        int count = notificationMapper.countUnreadByUserId(userId);
        return Map.of("unreadCount", count);
    }

    @Override
    public Object markNotificationsBatchRead(Long userId, List<Long> notificationIds) {
        log.info("批量标记通知已读: userId={}, ids={}", userId, notificationIds);
        String ids = notificationIds.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse("");
        if (ids.isEmpty()) return Map.of("success", false, "message", "无通知ID");
        int rows = notificationMapper.markBatchRead(userId, ids);
        return Map.of("success", rows > 0, "affected", rows);
    }

    @Override
    public Object deleteNotification(Long notificationId) {
        log.info("删除通知: notificationId={}", notificationId);
        int rows = notificationMapper.softDelete(notificationId);
        return Map.of("success", rows > 0, "message", rows > 0 ? "删除成功" : "删除失败");
    }

    @Override
    public Object clearReadNotifications(Long userId) {
        log.info("清除已读通知: userId={}", userId);
        int rows = notificationMapper.clearReadNotifications(userId);
        return Map.of("success", true, "cleared", rows);
    }

    @Override
    public Object getUnreadNotifications(Long userId) {
        log.info("获取未读通知: userId={}", userId);
        List<Notification> list = notificationMapper.selectUnreadByUserId(userId);
        return list.stream().map(this::notificationToMap).toList();
    }

    // ==================== 订阅管理 ====================
    @Override
    public Object getSubscriptionPreferences(Long studentId) {
        log.info("获取订阅偏好: studentId={}", studentId);
        SubscriptionPreference sp = subscriptionPreferenceMapper.selectByStudentId(studentId);
        if (sp == null) {
            // 创建默认偏好
            subscriptionPreferenceMapper.insertDefault(studentId);
            sp = subscriptionPreferenceMapper.selectByStudentId(studentId);
        }
        return subToMap(sp);
    }

    @Override
    public Object updateSubscriptionPreferences(Long studentId, Object preferences) {
        log.info("更新订阅偏好: studentId={}", studentId);
        @SuppressWarnings("unchecked")
        Map<String, Object> pref = (Map<String, Object>) preferences;
        // 使用 MyBatis-Plus 的 updateById
        SubscriptionPreference sp = subscriptionPreferenceMapper.selectByStudentId(studentId);
        if (sp == null) {
            subscriptionPreferenceMapper.insertDefault(studentId);
            sp = subscriptionPreferenceMapper.selectByStudentId(studentId);
        }
        if (sp != null) {
            if (pref.containsKey("subscribeWarnings")) sp.setSubscribeWarnings((Integer)pref.get("subscribeWarnings"));
            if (pref.containsKey("subscribeLow")) sp.setSubscribeLow((Integer)pref.get("subscribeLow"));
            if (pref.containsKey("subscribeMedium")) sp.setSubscribeMedium((Integer)pref.get("subscribeMedium"));
            if (pref.containsKey("subscribeHigh")) sp.setSubscribeHigh((Integer)pref.get("subscribeHigh"));
            if (pref.containsKey("subscribeAssistance")) sp.setSubscribeAssistance((Integer)pref.get("subscribeAssistance"));
            if (pref.containsKey("subscribeSystem")) sp.setSubscribeSystem((Integer)pref.get("subscribeSystem"));
            if (pref.containsKey("pushEmail")) sp.setPushEmail((Integer)pref.get("pushEmail"));
            if (pref.containsKey("pushSms")) sp.setPushSms((Integer)pref.get("pushSms"));
            if (pref.containsKey("pushApp")) sp.setPushApp((Integer)pref.get("pushApp"));
            sp.setUpdatedAt(java.time.LocalDateTime.now());
            subscriptionPreferenceMapper.updateById(sp);
        }
        return Map.of("success", true);
    }

    @Override
    public Object subscribeWarningLevel(Long studentId, String level) {
        log.info("订阅预警等级: studentId={}, level={}", studentId, level);
        String column = switch (level.toLowerCase()) {
            case "low" -> "subscribe_low";
            case "medium" -> "subscribe_medium";
            case "high" -> "subscribe_high";
            default -> "";
        };
        if (column.isEmpty()) return Map.of("success", false, "message", "无效的预警等级");
        ensureSubscriptionExists(studentId);
        subscriptionPreferenceMapper.updateField(studentId, column, 1);
        return Map.of("success", true);
    }

    @Override
    public Object unsubscribeWarningLevel(Long studentId, String level) {
        log.info("取消订阅预警等级: studentId={}, level={}", studentId, level);
        String column = switch (level.toLowerCase()) {
            case "low" -> "subscribe_low";
            case "medium" -> "subscribe_medium";
            case "high" -> "subscribe_high";
            default -> "";
        };
        if (column.isEmpty()) return Map.of("success", false, "message", "无效的预警等级");
        ensureSubscriptionExists(studentId);
        subscriptionPreferenceMapper.updateField(studentId, column, 0);
        return Map.of("success", true);
    }

    @Override
    public Object setPushChannel(Long studentId, String channel, Boolean enabled) {
        log.info("设置推送渠道: studentId={}, channel={}, enabled={}", studentId, channel, enabled);
        String column = switch (channel.toLowerCase()) {
            case "email" -> "push_email";
            case "sms" -> "push_sms";
            case "app" -> "push_app";
            default -> "";
        };
        if (column.isEmpty()) return Map.of("success", false, "message", "无效的推送渠道");
        ensureSubscriptionExists(studentId);
        subscriptionPreferenceMapper.updateField(studentId, column, enabled ? 1 : 0);
        return Map.of("success", true);
    }

    // ==================== 帮扶反馈 ====================
    @Override
    public Object submitEvaluation(Object evaluation) {
        log.info("提交帮扶评价: {}", evaluation);
        @SuppressWarnings("unchecked")
        Map<String, Object> req = (Map<String, Object>) evaluation;
        AssistanceEvaluation ae = new AssistanceEvaluation();
        if (req.containsKey("planId")) ae.setPlanId(Long.valueOf(req.get("planId").toString()));
        if (req.containsKey("counselorId")) ae.setCounselorId(Long.valueOf(req.get("counselorId").toString()));
        if (req.containsKey("content")) ae.setContent(req.get("content").toString());
        if (req.containsKey("score")) ae.setScore(Integer.valueOf(req.get("score").toString()));
        ae.setCreatedAt(java.time.LocalDateTime.now());
        ae.setUpdatedAt(java.time.LocalDateTime.now());
        assistanceEvaluationMapper.insert(ae);
        return Map.of("success", true, "id", ae.getId(), "message", "评价提交成功");
    }

    @Override
    public Object getEvaluationsByStudent(Long studentId) {
        log.info("获取学生评价列表: studentId={}", studentId);
        List<AssistanceEvaluation> list = assistanceEvaluationMapper.selectByStudentId(studentId);
        return list.stream().map(this::evaluationToMap).toList();
    }

    @Override
    public Object getPlanEvaluation(Long planId) {
        log.info("获取帮扶计划评价: planId={}", planId);
        List<AssistanceEvaluation> list = assistanceEvaluationMapper.selectByPlanId(planId);
        if (list.isEmpty()) return Map.of("error", "无评价数据");
        return list.stream().map(this::evaluationToMap).toList();
    }

    @Override
    public Object getEvaluationStatistics(Long studentId) {
        log.info("获取评价统计: studentId={}", studentId);
        Double avg = assistanceEvaluationMapper.avgScoreByStudentId(studentId);
        int count = assistanceEvaluationMapper.countByStudentId(studentId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageScore", avg != null ? BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP) : 0);
        stats.put("totalCount", count);
        return stats;
    }

    // ==================== 设置 ====================
    @Override
    public Object updatePrivacyLevel(Long studentId, Integer privacyLevel) {
        log.info("更新隐私级别: studentId={}, privacyLevel={}", studentId, privacyLevel);
        StudentProfile sp = studentProfileMapper.selectByStudentId(studentId.toString());
        if (sp == null) return Map.of("success", false, "message", "学生不存在");
        sp.setPrivacyLevel(privacyLevel);
        sp.setUpdatedAt(java.time.LocalDateTime.now());
        int rows = studentProfileMapper.updateById(sp);
        return Map.of("success", rows > 0);
    }

    @Override
    public Object getStudentSettings(Long studentId) {
        log.info("获取学生设置: studentId={}", studentId);
        StudentProfile sp = studentProfileMapper.selectByStudentId(studentId.toString());
        Map<String, Object> settings = new HashMap<>();
        settings.put("studentId", studentId);
        settings.put("notificationSettings", Map.of("emailNotifications", true, "smsNotifications", false, "appNotifications", true));
        settings.put("privacySettings", Map.of("shareGrades", false, "shareAttendance", false, "shareProgress", true));
        settings.put("displaySettings", Map.of("theme", "light", "language", "zh-CN", "timezone", "Asia/Shanghai"));
        if (sp != null) settings.put("privacyLevel", sp.getPrivacyLevel() != null ? sp.getPrivacyLevel() : 2);
        return settings;
    }

    @Override
    public Object getSecurityLogs(Long studentId, int page, int pageSize) {
        log.info("获取安全日志: studentId={}, page={}, pageSize={}", studentId, page, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("total", 0);
        result.put("logs", Collections.emptyList());
        return result;
    }

    // ==================== 消息 ====================
    @Override
    public Object getStudentMessages(Long studentId) {
        log.info("获取学生消息: studentId={}", studentId);
        // 融合 notifications 表数据
        List<Notification> notifs = notificationMapper.selectByUserId(studentId);
        if (!notifs.isEmpty()) return notifs.stream().map(this::notificationToMap).toList();
        return Collections.emptyList();
    }

    @Override
    public Object getUnreadMessageCount(Long studentId) {
        log.info("获取未读消息数量: studentId={}", studentId);
        int count = notificationMapper.countUnreadByUserId(studentId);
        return Map.of("unreadCount", count);
    }

    // ==================== 导出/下载 ====================
    @Override
    public Object exportScoresToExcel(Long studentId) {
        log.info("导出学生成绩到Excel: studentId={}", studentId);
        StudentProfile sp = studentProfileMapper.selectByStudentId(studentId.toString());
        if (sp == null) return Map.of("message", "学生不存在");
        List<Score> scores = scoreMapper.selectByStudentId(studentId.toString());
        Map<String, Object> result = new HashMap<>();
        result.put("message", "导出成功");
        result.put("studentId", studentId);
        result.put("studentName", sp.getName());
        result.put("scoreCount", scores.size());
        return result;
    }

    @Override
    public Object downloadScores(Long studentId) {
        log.info("下载成绩: studentId={}", studentId);
        return exportScoresToExcel(studentId); // 共用导出逻辑
    }

    // ==================== 学院相关 ====================
    @Override
    public long countByCollege(Long collegeId) {
        return studentProfileMapper.countByCollegeId(collegeId);
    }

    @Override
    public java.util.List<Object> getStudentsByCollege(Long collegeId) {
        List<StudentProfile> profiles = studentProfileMapper.selectByCollegeId(collegeId);
        return new ArrayList<>(profiles);
    }

    // ==================== 私有辅助方法 ====================
    /** 前端传来的是 userId，通过 StudentProfile 获得真正的 studentId */
    private Long resolveStudentProfileId(Long userId) {
        StudentProfile sp = studentProfileMapper.selectByUserId(userId);
        return sp != null ? sp.getId() : null;
    }

    private Map<String, Object> appealToMap(ScoreAppeal a) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", a.getId());
        m.put("studentId", a.getStudentId());
        m.put("scoreId", a.getScoreId());
        m.put("courseId", a.getCourseId());
        m.put("warningId", a.getWarningId());
        m.put("reason", a.getReason());
        m.put("attachments", a.getAttachments());
        m.put("status", a.getStatus());
        m.put("reply", a.getReply());
        m.put("createdAt", a.getCreatedAt() != null ? a.getCreatedAt().toString() : null);
        m.put("updatedAt", a.getUpdatedAt() != null ? a.getUpdatedAt().toString() : null);
        return m;
    }

    private Map<String, Object> baToMap(BenchmarkAnalysis ba) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", ba.getId());
        m.put("studentId", ba.getStudentId());
        m.put("classId", ba.getClassId());
        m.put("majorId", ba.getMajorId());
        m.put("semester", ba.getSemester());
        m.put("studentGpa", ba.getStudentGpa());
        m.put("classAvgGpa", ba.getClassAvgGpa());
        m.put("majorAvgGpa", ba.getMajorAvgGpa());
        m.put("classRank", ba.getClassRank());
        m.put("classTotal", ba.getClassTotal());
        m.put("majorRank", ba.getMajorRank());
        m.put("majorTotal", ba.getMajorTotal());
        m.put("coursesPassed", ba.getCoursesPassed());
        m.put("coursesFailed", ba.getCoursesFailed());
        m.put("requiredCredits", ba.getRequiredCredits());
        m.put("creditsOnTrack", ba.getCreditsOnTrack());
        return m;
    }

    private Map<String, Object> baBriefMap(BenchmarkAnalysis ba) {
        Map<String, Object> m = new HashMap<>();
        m.put("studentId", ba.getStudentId());
        m.put("studentGpa", ba.getStudentGpa());
        m.put("classRank", ba.getClassRank());
        m.put("majorRank", ba.getMajorRank());
        m.put("semester", ba.getSemester());
        return m;
    }

    private Map<String, Object> notificationToMap(Notification n) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", n.getId());
        m.put("userId", n.getUserId());
        m.put("title", n.getTitle());
        m.put("content", n.getContent());
        m.put("type", n.getType());
        m.put("warningId", n.getWarningId());
        m.put("pushChannel", n.getPushChannel());
        m.put("isRead", n.getIsRead());
        m.put("createdAt", n.getCreatedAt() != null ? n.getCreatedAt().toString() : null);
        return m;
    }

    private Map<String, Object> subToMap(SubscriptionPreference sp) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", sp.getId());
        m.put("studentId", sp.getStudentId());
        m.put("subscribeWarnings", sp.getSubscribeWarnings());
        m.put("subscribeLow", sp.getSubscribeLow());
        m.put("subscribeMedium", sp.getSubscribeMedium());
        m.put("subscribeHigh", sp.getSubscribeHigh());
        m.put("subscribeAssistance", sp.getSubscribeAssistance());
        m.put("subscribeSystem", sp.getSubscribeSystem());
        m.put("pushEmail", sp.getPushEmail());
        m.put("pushSms", sp.getPushSms());
        m.put("pushApp", sp.getPushApp());
        return m;
    }

    private Map<String, Object> evaluationToMap(AssistanceEvaluation ae) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", ae.getId());
        m.put("planId", ae.getPlanId());
        m.put("counselorId", ae.getCounselorId());
        m.put("content", ae.getContent());
        m.put("score", ae.getScore());
        m.put("createdAt", ae.getCreatedAt() != null ? ae.getCreatedAt().toString() : null);
        return m;
    }

    private void ensureSubscriptionExists(Long studentId) {
        SubscriptionPreference sp = subscriptionPreferenceMapper.selectByStudentId(studentId);
        if (sp == null) subscriptionPreferenceMapper.insertDefault(studentId);
    }
}