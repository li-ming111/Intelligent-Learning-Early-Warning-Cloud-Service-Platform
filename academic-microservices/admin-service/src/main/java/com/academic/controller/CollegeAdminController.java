package com.academic.controller;

import com.academic.common.dto.ApiResponse;
import com.academic.common.entity.*;
import com.academic.entity.Classes;
import com.academic.entity.StudentProfile;
import com.academic.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/college-admin")
public class CollegeAdminController {

    @Autowired private CollegeAdminProfileMapper collegeAdminProfileMapper;
    @Autowired private ClassesMapper classesMapper;
    @Autowired private StudentProfileMapper studentProfileMapper;
    @Autowired private TeacherProfileMapper teacherProfileMapper;
    @Autowired private CounselorProfileMapper counselorProfileMapper;
    @Autowired private ScoreRecordMapper scoreRecordMapper;
    @Autowired private AcademicWarningMapper academicWarningMapper;
    @Autowired private com.academic.mapper.MajorMapper majorMapper;
    @Autowired private com.academic.mapper.CourseMapper courseMapper;

    @GetMapping("/profile/{userId}")
    public ApiResponse<Map<String, Object>> getProfile(@PathVariable Long userId) {
        Map<String, Object> profile = collegeAdminProfileMapper.selectByUserId(userId);
        return profile != null ? ApiResponse.success(profile) : ApiResponse.error("未找到学院管理员档案");
    }

    private Long getCollegeId(Long userId) {
        Map<String, Object> p = collegeAdminProfileMapper.selectByUserId(userId);
        return p != null ? (Long) p.get("college_id") : null;
    }

    @GetMapping("/dashboard/{userId}")
    public ApiResponse<Map<String, Object>> dashboard(@PathVariable Long userId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");

        Map<String, Object> d = new HashMap<>();
        Map<String, Object> profile = collegeAdminProfileMapper.selectByUserId(userId);
        d.put("profile", profile);

        List<Map<String, Object>> classes = classesMapper.getClassesByCollegeId(collegeId);
        d.put("classCount", classes.size());

        List<Long> classIds = classes.stream().map(c -> (Long) c.get("id")).collect(Collectors.toList());
        long studentCount = classIds.isEmpty() ? 0 :
            studentProfileMapper.selectCount(new QueryWrapper<StudentProfile>().in("class_id", classIds));
        d.put("studentCount", (int) studentCount);

        long teacherCount = teacherProfileMapper.selectCount(
            new QueryWrapper<TeacherProfile>().eq("college_id", collegeId));
        d.put("teacherCount", (int) teacherCount);

        List<CounselorProfile> allCounselors = counselorProfileMapper.selectList(new QueryWrapper<>());
        Set<Long> counselorClassIds = new HashSet<>();
        allCounselors.forEach(c -> {
            List<Map<String, Object>> cc = classesMapper.getClassesByCounselorIdMap(c.getUserId());
            cc.forEach(m -> counselorClassIds.add((Long) m.get("id")));
        });
        long counselorCount = classes.stream().filter(c -> counselorClassIds.contains(c.get("id"))).count();
        d.put("counselorCount", (int) Math.min(counselorCount, allCounselors.size()));

        long warningCount = classIds.isEmpty() ? 0 :
            academicWarningMapper.selectList(new QueryWrapper<>()).stream()
                .filter(w -> {
                    if (w.getStudentId() == null) return false;
                    StudentProfile sp = studentProfileMapper.selectById(w.getStudentId());
                    return sp != null && sp.getClassId() != null && classIds.contains(sp.getClassId());
                }).count();
        d.put("warningCount", (int) warningCount);

        List<Major> majors = majorMapper.selectList(new QueryWrapper<Major>().eq("college_id", collegeId));
        d.put("majorCount", majors.size());
        return ApiResponse.success(d);
    }

    @GetMapping("/classes/{userId}")
    public ApiResponse<List<Map<String, Object>>> getClasses(@PathVariable Long userId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        List<Map<String, Object>> classes = classesMapper.getClassesByCollegeId(collegeId);
        // 为每个班级补允统计数据
        for (Map<String, Object> c : classes) {
            Long classId = (Long) c.get("id");
            List<StudentProfile> students = studentProfileMapper.selectList(
                new QueryWrapper<StudentProfile>().eq("class_id", classId));
            c.put("studentCount", students.size());
            double totalGpa = 0; int hasGpa = 0; int warnCount = 0;
            for (StudentProfile sp : students) {
                List<ScoreRecord> scores = scoreRecordMapper.selectList(
                    new QueryWrapper<ScoreRecord>().eq("student_id", sp.getId()));
                if (!scores.isEmpty()) {
                    double t = 0; int p = 0;
                    for (ScoreRecord sr : scores) {
                        double s = sr.getScore() != null ? sr.getScore() : 0;
                        t += s; if (s >= 60) p++;
                    }
                    totalGpa += t / scores.size() / 20.0;
                    hasGpa++;
                }
                int wc = (int) academicWarningMapper.selectList(
                    new QueryWrapper<AcademicWarning>().eq("student_id", sp.getId())).stream().count();
                warnCount += wc;
            }
            c.put("avgGpa", hasGpa > 0 ? Math.round(totalGpa / hasGpa * 100.0) / 100.0 : 0);
            c.put("warningCount", warnCount);
        }
        return ApiResponse.success(classes);
    }

    @GetMapping("/students/{userId}")
    public ApiResponse<List<Map<String, Object>>> getStudents(
            @PathVariable Long userId,
            @RequestParam(required = false) String keyword) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        List<Map<String, Object>> classes = classesMapper.getClassesByCollegeId(collegeId);
        List<Long> classIds = classes.stream().map(c -> (Long) c.get("id")).collect(Collectors.toList());
        if (classIds.isEmpty()) return ApiResponse.success(Collections.emptyList());

        List<StudentProfile> all = studentProfileMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentProfile sp : all) {
            if (sp.getClassId() != null && classIds.contains(sp.getClassId())) {
                // 搜索过滤
                if (keyword != null && !keyword.isEmpty()) {
                    String kw = keyword.toLowerCase();
                    boolean match = (sp.getName() != null && sp.getName().toLowerCase().contains(kw))
                        || (String.valueOf(sp.getStudentId()).contains(kw))
                        || (sp.getClassName() != null && sp.getClassName().toLowerCase().contains(kw));
                    if (!match) continue;
                }
                Map<String, Object> m = new HashMap<>();
                m.put("id", sp.getId());
                m.put("studentId", sp.getStudentId());
                m.put("name", sp.getName());
                m.put("className", sp.getClassName());
                m.put("classId", sp.getClassId());
                m.put("collegeId", sp.getCollegeId());
                // 附加成绩统计
                List<ScoreRecord> scores = scoreRecordMapper.selectList(
                    new QueryWrapper<ScoreRecord>().eq("student_id", sp.getId()));
                double total = 0; int pass = 0;
                for (ScoreRecord sr : scores) {
                    double sc = sr.getScore() != null ? sr.getScore() : 0;
                    total += sc; if (sc >= 60) pass++;
                }
                m.put("gpa", scores.isEmpty() ? 0 : Math.round(total / scores.size() / 20.0 * 100.0) / 100.0);
                m.put("totalCourses", scores.size());
                m.put("passedCourses", pass);
                m.put("failedCourses", scores.size() - pass);
                m.put("warningCount", academicWarningMapper.selectList(
                    new QueryWrapper<AcademicWarning>().eq("student_id", sp.getId())).size());
                result.add(m);
            }
        }
        return ApiResponse.success(result);
    }

    /** 学生详情 */
    @GetMapping("/student-detail/{userId}/{profileId}")
    public ApiResponse<Map<String, Object>> getStudentDetail(@PathVariable Long userId, @PathVariable Long profileId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        StudentProfile sp = studentProfileMapper.selectById(profileId);
        if (sp == null) return ApiResponse.error("学生不存在");
        if (sp.getClassId() == null) return ApiResponse.error("该学生无班级信息");
        List<Map<String, Object>> classes = classesMapper.getClassesByCollegeId(collegeId);
        boolean inCollege = classes.stream().anyMatch(c -> c.get("id").equals(sp.getClassId()));
        if (!inCollege) return ApiResponse.error("该学生不属于您管理的学院");

        Map<String, Object> detail = new HashMap<>();
        detail.put("id", sp.getId());
        detail.put("studentId", sp.getStudentId());
        detail.put("name", sp.getName());
        detail.put("className", sp.getClassName());

        // 通过 ScoreMapper 的 JOIN 查询来拿课程名（已在 student-service ScoreMapper 中定义）
        List<ScoreRecord> scores = scoreRecordMapper.selectList(
            new QueryWrapper<ScoreRecord>().eq("student_id", profileId));
        double total = 0; int pass = 0;
        List<Map<String, Object>> scoreList = new ArrayList<>();
        for (ScoreRecord sr : scores) {
            double sc = sr.getScore() != null ? sr.getScore() : 0;
            if (sr.getScore() != null) { total += sc; if (sc >= 60) pass++; }
            // 查询课程名
            String courseName = "";
            if (sr.getCourseId() != null) {
                Course course = courseMapper.selectById(sr.getCourseId());
                if (course != null) courseName = course.getName();
            }
            Map<String, Object> sm = new HashMap<>();
            sm.put("id", sr.getId());
            sm.put("courseId", sr.getCourseId());
            sm.put("courseName", courseName);
            sm.put("regularScore", sr.getRegularScore());   // 平时成绩
            sm.put("finalScore", sr.getFinalScore());       // 期末成绩
            sm.put("totalScore", sr.getScoreTotal());       // 总评成绩
            sm.put("score", sc);                            // 原始成绩
            sm.put("gradePoint", sr.getGradePoint());       // 绩点
            sm.put("grade", sr.getGrade());                 // 等级
            sm.put("semester", sr.getSemester());
            sm.put("remark", sr.getRemark());
            scoreList.add(sm);
        }
        int courseCount = scores.size();
        detail.put("gpa", courseCount == 0 ? 0 : Math.round(total / courseCount / 20.0 * 100.0) / 100.0);
        detail.put("totalCourses", courseCount);
        detail.put("passedCourses", pass);
        detail.put("failedCourses", courseCount - pass);
        detail.put("scores", scoreList);

        List<AcademicWarning> warnings = academicWarningMapper.selectList(
            new QueryWrapper<AcademicWarning>().eq("student_id", profileId));
        List<Map<String, Object>> warnList = new ArrayList<>();
        for (AcademicWarning w : warnings) {
            Map<String, Object> wm = new HashMap<>();
            wm.put("id", w.getId()); wm.put("description", w.getDescription());
            wm.put("warningLevel", w.getWarningLevel()); wm.put("status", w.getStatus());
            wm.put("createdAt", w.getCreatedAt());
            warnList.add(wm);
        }
        detail.put("warnings", warnList);
        detail.put("warningCount", warnings.size());

        return ApiResponse.success(detail);
    }

    @GetMapping("/teachers/{userId}")
    public ApiResponse<List<Map<String, Object>>> getTeachers(@PathVariable Long userId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        List<TeacherProfile> all = teacherProfileMapper.selectList(new QueryWrapper<>()).stream()
            .filter(t -> t.getCollegeId() != null && t.getCollegeId().equals(collegeId))
            .collect(Collectors.toList());
        List<Map<String, Object>> result = new ArrayList<>();
        for (TeacherProfile tp : all) {
            List<ScoreRecord> scores = scoreRecordMapper.selectList(
                new QueryWrapper<ScoreRecord>().eq("teacher_id", tp.getUserId()));
            Set<Long> uniqueCourses = new HashSet<>();
            List<String> courseNames = new ArrayList<>();
            for (ScoreRecord sr : scores) {
                if (sr.getCourseId() != null && !uniqueCourses.contains(sr.getCourseId())) {
                    uniqueCourses.add(sr.getCourseId());
                    Course course = courseMapper.selectById(sr.getCourseId());
                    if (course != null) courseNames.add(course.getName());
                }
            }
            Map<String, Object> m = new HashMap<>();
            m.put("id", tp.getId());
            m.put("name", tp.getName());
            m.put("title", tp.getTitle());
            m.put("researchAreas", tp.getResearchAreas());
            m.put("courseCount", uniqueCourses.size());
            m.put("studentCount", scores.size());
            m.put("courses", String.join("、", courseNames));
            result.add(m);
        }
        return ApiResponse.success(result);
    }

    @GetMapping("/warnings/{userId}")
    public ApiResponse<List<Map<String, Object>>> getWarnings(@PathVariable Long userId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");

        List<Map<String, Object>> classes = classesMapper.getClassesByCollegeId(collegeId);
        List<Long> classIds = classes.stream().map(c -> (Long) c.get("id")).collect(Collectors.toList());
        if (classIds.isEmpty()) return ApiResponse.success(Collections.emptyList());

        List<AcademicWarning> all = academicWarningMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (AcademicWarning w : all) {
            if (w.getStudentId() == null) continue;
            StudentProfile sp = studentProfileMapper.selectById(w.getStudentId());
            if (sp != null && sp.getClassId() != null && classIds.contains(sp.getClassId())) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", w.getId());
                m.put("studentId", w.getStudentId());
                m.put("studentName", sp.getName());
                m.put("className", sp.getClassName());
                m.put("description", w.getDescription());
                m.put("warningLevel", w.getWarningLevel());
                m.put("status", w.getStatus());
                m.put("createdAt", w.getCreatedAt());
                result.add(m);
            }
        }
        return ApiResponse.success(result);
    }

    @GetMapping("/scores-stats/{userId}")
    public ApiResponse<Map<String, Object>> getScoreStats(@PathVariable Long userId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");

        List<Map<String, Object>> classes = classesMapper.getClassesByCollegeId(collegeId);
        List<Long> classIds = classes.stream().map(c -> (Long) c.get("id")).collect(Collectors.toList());
        if (classIds.isEmpty()) return ApiResponse.success(Map.of("avgScore", 0, "passRate", 0));

        List<ScoreRecord> records = scoreRecordMapper.selectList(new QueryWrapper<>());
        double total = 0;
        int count = 0, pass = 0;
        for (ScoreRecord r : records) {
            StudentProfile sp = studentProfileMapper.selectById(r.getStudentId());
            if (sp != null && sp.getClassId() != null && classIds.contains(sp.getClassId())) {
                double sc = r.getScore() != null ? r.getScore().doubleValue() : 0;
                total += sc; count++;
                if (sc >= 60) pass++;
            }
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("avgScore", count > 0 ? Math.round(total / count * 10) / 10.0 : 0);
        stats.put("passRate", count > 0 ? Math.round(pass * 100.0 / count) : 0);
        stats.put("recordCount", count);
        return ApiResponse.success(stats);
    }

    /** 本学院成绩分布分析 + 各科目分析 */
    @GetMapping("/score-analysis/{userId}")
    public ApiResponse<Map<String, Object>> scoreAnalysis(@PathVariable Long userId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        List<Map<String, Object>> classes = classesMapper.getClassesByCollegeId(collegeId);
        Set<Long> classIds = classes.stream().map(c -> (Long) c.get("id")).collect(Collectors.toSet());
        if (classIds.isEmpty()) return ApiResponse.success(Map.of());
        
        List<ScoreRecord> records = scoreRecordMapper.selectList(new QueryWrapper<>());
        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("90-100", 0); dist.put("80-89", 0); dist.put("70-79", 0);
        dist.put("60-69", 0); dist.put("0-59", 0);
        double total = 0; int cnt = 0, pass = 0;
        
        // 课程维度统计: courseId -> { count, total, pass }
        Map<Long, double[]> courseBook = new HashMap<>();
        
        for (ScoreRecord r : records) {
            StudentProfile sp = studentProfileMapper.selectById(r.getStudentId());
            if (sp == null || sp.getClassId() == null || !classIds.contains(sp.getClassId())) continue;
            double s = r.getScore() != null ? r.getScore() : 0;
            total += s; cnt++;
            if (s >= 60) pass++;
            if (s >= 90) dist.put("90-100", dist.get("90-100") + 1);
            else if (s >= 80) dist.put("80-89", dist.get("80-89") + 1);
            else if (s >= 70) dist.put("70-79", dist.get("70-79") + 1);
            else if (s >= 60) dist.put("60-69", dist.get("60-69") + 1);
            else dist.put("0-59", dist.get("0-59") + 1);
            
            // 统计课程维度
            if (r.getCourseId() != null) {
                double[] acc = courseBook.computeIfAbsent(r.getCourseId(), k -> new double[]{0, 0, 0});
                acc[0] += s; acc[1]++; if (s >= 60) acc[2]++;
            }
        }
        
        // 构建课程统计列表
        List<Map<String, Object>> courseStats = new ArrayList<>();
        for (Map.Entry<Long, double[]> e : courseBook.entrySet()) {
            Course course = courseMapper.selectById(e.getKey());
            double[] acc = e.getValue();
            Map<String, Object> cs = new HashMap<>();
            cs.put("courseId", e.getKey());
            cs.put("courseName", course != null ? course.getName() : "未知课程");
            cs.put("recordCount", (int) acc[1]);
            cs.put("avgScore", acc[1] > 0 ? Math.round(acc[0] / acc[1] * 10) / 10.0 : 0);
            cs.put("passRate", acc[1] > 0 ? Math.round(acc[2] * 100 / acc[1]) : 0);
            courseStats.add(cs);
        }
        courseStats.sort((a, b) -> Double.compare((double) b.get("avgScore"), (double) a.get("avgScore")));
        
        Map<String, Object> result = new HashMap<>();
        result.put("distribution", dist);
        result.put("avgScore", cnt > 0 ? Math.round(total / cnt * 10) / 10.0 : 0);
        result.put("passRate", cnt > 0 ? Math.round(pass * 100.0 / cnt) : 0);
        result.put("totalRecords", cnt);
        result.put("courseStats", courseStats);
        return ApiResponse.success(result);
    }

    /** 班级对比分析 */
    @GetMapping("/class-comparison/{userId}")
    public ApiResponse<List<Map<String, Object>>> classComparison(@PathVariable Long userId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        List<Map<String, Object>> classes = classesMapper.getClassesByCollegeId(collegeId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Map<String, Object> c : classes) {
            Long cid = (Long) c.get("id");
            List<ScoreRecord> records = scoreRecordMapper.selectList(new QueryWrapper<>());
            double total = 0; int cnt = 0, pass = 0;
            Set<Long> seen = new HashSet<>();
            for (ScoreRecord r : records) {
                StudentProfile sp = studentProfileMapper.selectById(r.getStudentId());
                if (sp == null || sp.getClassId() == null || !sp.getClassId().equals(cid)) continue;
                double s = r.getScore() != null ? r.getScore() : 0;
                total += s; cnt++; if (s >= 60) pass++;
                seen.add(r.getStudentId());
            }
            Map<String, Object> item = new HashMap<>();
            item.put("className", c.get("name"));
            item.put("avgScore", cnt > 0 ? Math.round(total / cnt * 10) / 10.0 : 0);
            item.put("passRate", cnt > 0 ? Math.round(pass * 100.0 / cnt) : 0);
            item.put("studentCount", seen.size());
            item.put("recordCount", cnt);
            result.add(item);
        }
        return ApiResponse.success(result);
    }

    /** 本学院辅导员及管辖班级 */
    @GetMapping("/counselors/{userId}")
    public ApiResponse<List<Map<String, Object>>> getCounselors(@PathVariable Long userId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        List<CounselorProfile> counselors = counselorProfileMapper.selectList(
            new QueryWrapper<CounselorProfile>().eq("college_id", collegeId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (CounselorProfile cp : counselors) {
            List<Map<String, Object>> cls = classesMapper.getClassesByCounselorIdMap(cp.getUserId());
            List<String> classNames = new ArrayList<>();
            for (Map<String, Object> c : cls) classNames.add((String) c.get("name"));
            Map<String, Object> m = new HashMap<>();
            m.put("id", cp.getId()); m.put("name", cp.getName()); m.put("phone", cp.getPhone());
            m.put("classCount", cls.size()); m.put("classes", String.join("、", classNames));
            result.add(m);
        }
        return ApiResponse.success(result);
    }

    /** 本学院预警学生的帮扶计划 */
    @GetMapping("/assistance/{userId}")
    public ApiResponse<List<Map<String, Object>>> getAssistance(@PathVariable Long userId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        List<Map<String, Object>> classes = classesMapper.getClassesByCollegeId(collegeId);
        Set<Long> classIds = classes.stream().map(c -> (Long) c.get("id")).collect(Collectors.toSet());
        List<AcademicWarning> warnings = academicWarningMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (AcademicWarning w : warnings) {
            if (w.getStudentId() == null) continue;
            StudentProfile sp = studentProfileMapper.selectById(w.getStudentId());
            if (sp == null || sp.getClassId() == null || !classIds.contains(sp.getClassId())) continue;
            Map<String, Object> m = new HashMap<>();
            m.put("studentName", sp.getName()); m.put("className", sp.getClassName());
            m.put("level", w.getWarningLevel()); m.put("description", w.getDescription());
            m.put("status", w.getStatus()); m.put("createdAt", w.getCreatedAt());
            result.add(m);
        }
        return ApiResponse.success(result);
    }

    /** 导出本学院数据 */
    @GetMapping("/export/{userId}")
    public ApiResponse<List<Map<String, Object>>> exportData(@PathVariable Long userId,
            @RequestParam(defaultValue = "scores") String type) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        List<Map<String, Object>> classes = classesMapper.getClassesByCollegeId(collegeId);
        Set<Long> classIds = classes.stream().map(c -> (Long) c.get("id")).collect(Collectors.toSet());
        List<Map<String, Object>> result = new ArrayList<>();
        
        if ("scores".equals(type)) {
            List<ScoreRecord> records = scoreRecordMapper.selectList(new QueryWrapper<>());
            for (ScoreRecord r : records) {
                StudentProfile sp = studentProfileMapper.selectById(r.getStudentId());
                if (sp == null || sp.getClassId() == null || !classIds.contains(sp.getClassId())) continue;
                // 查询课程名
                String courseName = "";
                if (r.getCourseId() != null) {
                    Course course = courseMapper.selectById(r.getCourseId());
                    if (course != null) courseName = course.getName();
                }
                Map<String, Object> m = new HashMap<>();
                m.put("studentName", sp.getName());
                m.put("className", sp.getClassName());
                m.put("courseName", courseName);
                m.put("regularScore", r.getRegularScore());   // 平时成绩
                m.put("finalScore", r.getFinalScore());       // 期末成绩
                m.put("totalScore", r.getScoreTotal());       // 总评成绩
                m.put("gradePoint", r.getGradePoint());       // 绩点
                m.put("grade", r.getGrade());                 // 等级
                m.put("semester", r.getSemester());
                result.add(m);
            }
        } else if ("warnings".equals(type)) {
            List<AcademicWarning> warnings = academicWarningMapper.selectList(new QueryWrapper<>());
            for (AcademicWarning w : warnings) {
                if (w.getStudentId() == null) continue;
                StudentProfile sp = studentProfileMapper.selectById(w.getStudentId());
                if (sp == null || sp.getClassId() == null || !classIds.contains(sp.getClassId())) continue;
                Map<String, Object> m = new HashMap<>();
                m.put("studentName", sp.getName()); m.put("className", sp.getClassName());
                m.put("description", w.getDescription()); m.put("warningLevel", w.getWarningLevel());
                m.put("status", w.getStatus() == 1 ? "已处理" : "待处理");
                result.add(m);
            }
        } else if ("students".equals(type)) {
            List<StudentProfile> all = studentProfileMapper.selectList(new QueryWrapper<>());
            for (StudentProfile sp : all) {
                if (sp.getClassId() == null || !classIds.contains(sp.getClassId())) continue;
                Map<String, Object> m = new HashMap<>();
                m.put("studentId", sp.getStudentId()); m.put("name", sp.getName());
                m.put("className", sp.getClassName());
                result.add(m);
            }
        } else if ("teachers".equals(type)) {
            List<TeacherProfile> all = teacherProfileMapper.selectList(new QueryWrapper<>());
            for (TeacherProfile tp : all) {
                if (tp.getCollegeId() == null || !tp.getCollegeId().equals(collegeId)) continue;
                Map<String, Object> m = new HashMap<>();
                m.put("name", tp.getName()); m.put("title", tp.getTitle());
                m.put("researchAreas", tp.getResearchAreas());
                result.add(m);
            }
        }
        return ApiResponse.success(result);
    }

    /** 教师详情 */
    @GetMapping("/teacher-detail/{userId}/{teacherId}")
    public ApiResponse<Map<String, Object>> getTeacherDetail(@PathVariable Long userId, @PathVariable Long teacherId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        TeacherProfile tp = teacherProfileMapper.selectById(teacherId);
        if (tp == null || !tp.getCollegeId().equals(collegeId)) return ApiResponse.error("无权限");
        Map<String, Object> d = new HashMap<>();
        d.put("id", tp.getId()); d.put("name", tp.getName()); d.put("title", tp.getTitle());
        d.put("researchAreas", tp.getResearchAreas());
        d.put("collegeId", tp.getCollegeId());

        List<ScoreRecord> scores = scoreRecordMapper.selectList(
            new QueryWrapper<ScoreRecord>().eq("teacher_id", teacherId));
        List<Map<String, Object>> scoreList = new ArrayList<>();
        Set<Long> uniqueCourses = new HashSet<>();
        double total = 0; int sc = 0;
        for (ScoreRecord sr : scores) {
            Map<String, Object> sm = new HashMap<>();
            sm.put("id", sr.getId()); sm.put("studentName", sr.getStudentName());
            sm.put("score", sr.getScore()); sm.put("semester", sr.getSemester());
            sm.put("className", sr.getClassName()); sm.put("courseId", sr.getCourseId());
            // 查询课程名
            if (sr.getCourseId() != null) {
                uniqueCourses.add(sr.getCourseId());
                Course course = courseMapper.selectById(sr.getCourseId());
                sm.put("courseName", course != null ? course.getName() : "");
            }
            scoreList.add(sm);
            if (sr.getScore() != null) { total += sr.getScore(); sc++; }
        }
        d.put("scores", scoreList);
        d.put("courseCount", uniqueCourses.size());
        d.put("studentCount", scores.size());
        d.put("avgScore", sc > 0 ? Math.round(total / sc * 10) / 10.0 : 0);
        return ApiResponse.success(d);
    }

    /** 标记预警/帮扶状态 */
    @PostMapping("/process-warning")
    public ApiResponse<String> processWarning(@RequestBody Map<String, Object> body) {
        Long warningId = body.get("id") != null ? Long.valueOf(body.get("id").toString()) : null;
        if (warningId == null) return ApiResponse.error("缺少ID");
        AcademicWarning w = academicWarningMapper.selectById(warningId);
        if (w == null) return ApiResponse.error("记录不存在");
        w.setStatus(body.get("status") != null ? Integer.valueOf(body.get("status").toString()) : 1);
        w.setUpdatedAt(java.time.LocalDateTime.now());
        academicWarningMapper.updateById(w);
        return ApiResponse.success("处理成功");
    }

    /** 删除预警记录 */
    @DeleteMapping("/delete-warning/{warningId}")
    public ApiResponse<String> deleteWarning(@PathVariable Long warningId) {
        AcademicWarning w = academicWarningMapper.selectById(warningId);
        if (w == null) return ApiResponse.error("记录不存在");
        academicWarningMapper.deleteById(warningId);
        return ApiResponse.success("已删除");
    }

    /** 班级学生列表 */
    @GetMapping("/class-students/{userId}/{classId}")
    public ApiResponse<List<Map<String, Object>>> getClassStudents(@PathVariable Long userId, @PathVariable Long classId) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        List<StudentProfile> all = studentProfileMapper.selectList(
            new QueryWrapper<StudentProfile>().eq("class_id", classId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentProfile sp : all) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", sp.getId()); m.put("studentId", sp.getStudentId());
            m.put("name", sp.getName()); m.put("className", sp.getClassName());
            List<ScoreRecord> scores = scoreRecordMapper.selectList(
                new QueryWrapper<ScoreRecord>().eq("student_id", sp.getId()));
            double t = 0; int pass = 0;
            for (ScoreRecord sr : scores) {
                double s = sr.getScore() != null ? sr.getScore() : 0;
                t += s; if (s >= 60) pass++;
            }
            m.put("gpa", scores.isEmpty() ? 0 : Math.round(t / scores.size() / 20.0 * 100.0) / 100.0);
            m.put("totalCourses", scores.size());
            m.put("passedCourses", pass);
            m.put("failedCourses", scores.size() - pass);
            m.put("warningCount", academicWarningMapper.selectList(
                new QueryWrapper<AcademicWarning>().eq("student_id", sp.getId())).size());
            result.add(m);
        }
        return ApiResponse.success(result);
    }

    /** 成绩查询：按班级/学生筛选全院成绩 */
    @GetMapping("/score-query/{userId}")
    public ApiResponse<List<Map<String, Object>>> scoreQuery(
            @PathVariable Long userId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String keyword) {
        Long collegeId = getCollegeId(userId);
        if (collegeId == null) return ApiResponse.error("无权限");
        List<Map<String, Object>> classes = classesMapper.getClassesByCollegeId(collegeId);
        Set<Long> classIds = classes.stream().map(c -> (Long) c.get("id")).collect(Collectors.toSet());
        if (classIds.isEmpty()) return ApiResponse.success(Collections.emptyList());

        List<ScoreRecord> records = scoreRecordMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (ScoreRecord r : records) {
            StudentProfile sp = studentProfileMapper.selectById(r.getStudentId());
            if (sp == null || sp.getClassId() == null || !classIds.contains(sp.getClassId())) continue;
            // 班级筛选
            if (classId != null && !sp.getClassId().equals(classId)) continue;
            // 关键字搜索（学号/姓名）
            if (keyword != null && !keyword.isEmpty()) {
                String kw = keyword.toLowerCase();
                boolean match = (sp.getName() != null && sp.getName().toLowerCase().contains(kw))
                    || String.valueOf(sp.getStudentId()).contains(kw);
                if (!match) continue;
            }
            // 课程名
            String courseName = "";
            if (r.getCourseId() != null) {
                Course course = courseMapper.selectById(r.getCourseId());
                if (course != null) courseName = course.getName();
            }
            Map<String, Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("studentName", sp.getName());
            m.put("studentId", sp.getStudentId());
            m.put("className", sp.getClassName());
            m.put("courseName", courseName);
            m.put("regularScore", r.getRegularScore());
            m.put("finalScore", r.getFinalScore());
            m.put("totalScore", r.getScoreTotal());
            m.put("gradePoint", r.getGradePoint());
            m.put("grade", r.getGrade());
            m.put("semester", r.getSemester());
            result.add(m);
        }
        // 按学生姓名排序
        result.sort((a, b) -> {
            int cmp = String.valueOf(a.get("studentName")).compareTo(String.valueOf(b.get("studentName")));
            if (cmp == 0) cmp = String.valueOf(a.get("courseName")).compareTo(String.valueOf(b.get("courseName")));
            return cmp;
        });
        return ApiResponse.success(result);
    }
}
