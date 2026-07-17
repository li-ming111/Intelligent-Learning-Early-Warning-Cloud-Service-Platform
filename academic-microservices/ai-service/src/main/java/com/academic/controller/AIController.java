package com.academic.controller;

import com.academic.common.dto.ApiResponse;
import com.academic.feign.StudentServiceFeignClient;
import com.academic.service.AIService;
import com.academic.service.TongYiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 智能服务控制器
 * 提供成绩分析、风险预测、学习建议、预警生成等 AI 功能。
 */
@RestController
@RequestMapping("/ai")
public class AIController {

    private static final Logger log = LoggerFactory.getLogger(AIController.class);

    private final AIService aiService;
    private final TongYiService tongYiService;
    private final StudentServiceFeignClient studentServiceClient;

    public AIController(AIService aiService, TongYiService tongYiService,
                        StudentServiceFeignClient studentServiceClient) {
        this.aiService = aiService;
        this.tongYiService = tongYiService;
        this.studentServiceClient = studentServiceClient;
    }

    // ==================== 核心 AI 分析接口 ====================

    @PostMapping("/analyze-score")
    public ApiResponse<Object> analyzeScore(@RequestBody Map<String, Object> request) {
        try {
            log.info("成绩分析请求: studentId={}", request.get("studentId"));
            return ApiResponse.success(aiService.analyzeScore(request));
        } catch (Exception e) {
            log.error("成绩分析失败: {}", e.getMessage(), e);
            return ApiResponse.error("分析失败: " + e.getMessage());
        }
    }

    @PostMapping("/generate-suggestion")
    public ApiResponse<Object> generateSuggestion(@RequestBody Map<String, Object> request) {
        try {
            log.info("学习建议生成: studentId={}", request.get("studentId"));
            return ApiResponse.success(aiService.generateSuggestion(request));
        } catch (Exception e) {
            log.error("建议生成失败: {}", e.getMessage(), e);
            return ApiResponse.error("生成失败: " + e.getMessage());
        }
    }

    @PostMapping("/analyze-behavior")
    public ApiResponse<Object> analyzeBehavior(@RequestBody Map<String, Object> request) {
        try {
            log.info("趋势分析请求: studentId={}", request.get("studentId"));
            return ApiResponse.success(aiService.analyzeBehavior(request));
        } catch (Exception e) {
            log.error("趋势分析失败: {}", e.getMessage(), e);
            return ApiResponse.error("分析失败: " + e.getMessage());
        }
    }

    @PostMapping("/predict-risk")
    public ApiResponse<Object> predictRisk(@RequestBody Map<String, Object> request) {
        try {
            log.info("风险预测请求: studentId={}", request.get("studentId"));
            return ApiResponse.success(aiService.predictRisk(request));
        } catch (Exception e) {
            log.error("风险预测失败: {}", e.getMessage(), e);
            return ApiResponse.error("预测失败: " + e.getMessage());
        }
    }

    @PostMapping("/generate-warning")
    public ApiResponse<Object> generateWarning(@RequestBody Map<String, Object> request) {
        try {
            log.info("预警生成请求: studentId={}", request.get("studentId"));
            return ApiResponse.success(aiService.generateWarning(request));
        } catch (Exception e) {
            log.error("预警生成失败: {}", e.getMessage(), e);
            return ApiResponse.error("生成失败: " + e.getMessage());
        }
    }

    // ==================== GET 便捷接口 ====================

    /**
     * 获取学生智能建议（综合调用）
     */
    @GetMapping("/student/{studentId}/suggestions")
    public ApiResponse<Object> getStudentSuggestions(@PathVariable String studentId) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", studentId);
            return ApiResponse.success(aiService.generateSuggestion(request));
        } catch (Exception e) {
            log.error("获取建议失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/chat")
    public ApiResponse<Object> chat(@RequestBody Map<String, Object> data) {
        try {
            // 聊天功能：组合调用分析+建议
            String message = (String) data.getOrDefault("message", "");
            String studentId = (String) data.getOrDefault("studentId", "");

            Map<String, Object> analysisReq = new HashMap<>();
            analysisReq.put("studentId", studentId);
            analysisReq.put("query", message);

            Map<String, Object> result = new HashMap<>();
            result.put("studentId", studentId);

            // 调用通义千问获取智能回复
            String aiReply = tongYiService.generalChat(message);
            if (aiReply != null && !aiReply.isEmpty()) {
                result.put("reply", aiReply);
            } else {
                result.put("reply", "已收到您的问题，请提供具体成绩数据以便精确分析");
            }

            // 如果有成绩数据，进行规则分析作为补充
            if (data.containsKey("scores")) {
                analysisReq.put("scores", data.get("scores"));
                Object analysis = aiService.analyzeScore(analysisReq);
                result.put("analysis", analysis);
            }

            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("聊天失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/qa")
    public ApiResponse<Object> qa(@RequestBody Map<String, Object> data) {
        try {
            String question = (String) data.getOrDefault("question", "");
            Map<String, Object> result = new HashMap<>();
            result.put("question", question);
            // 调用通义千问智能问答
            String aiAnswer = tongYiService.generalChat(question);
            result.put("answer", aiAnswer != null ? aiAnswer : "学业预警系统可以帮助分析成绩趋势、预测风险等级、生成学习建议");

            // 如果传入了学生数据，进行实际分析
            if (data.containsKey("scores")) {
                Map<String, Object> req = new HashMap<>();
                req.put("studentId", data.getOrDefault("studentId", ""));
                req.put("scores", data.get("scores"));
                Object analysis = aiService.analyzeScore(req);
                result.put("analysis", analysis);
            }

            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("问答失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/predict-warnings/{studentId}")
    public ApiResponse<Object> predictWarnings(@PathVariable String studentId) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", studentId);
            return ApiResponse.success(aiService.predictRisk(request));
        } catch (Exception e) {
            log.error("预警预测失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/generate-plan")
    public ApiResponse<Object> generatePlan(@RequestBody Map<String, Object> data) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", data.getOrDefault("studentId", ""));
            request.put("studentName", data.getOrDefault("studentName", ""));
            request.put("riskLevel", data.getOrDefault("riskLevel", "正常"));
            request.put("riskFactors", data.get("riskFactors"));
            return ApiResponse.success(aiService.generateWarning(request));
        } catch (Exception e) {
            log.error("计划生成失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/analyze-scores/{studentId}")
    public ApiResponse<Object> analyzeScores(@PathVariable String studentId) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", studentId);
            // 尝试从 student-service 获取真实成绩数据
            try {
                Object scores = studentServiceClient.getStudentScores(studentId);
                request.put("scores", scores);
            } catch (Exception ex) {
                log.warn("无法获取学生成绩数据: {}", ex.getMessage());
            }
            return ApiResponse.success(aiService.analyzeScore(request));
        } catch (Exception e) {
            log.error("成绩分析失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/analyze-warning-trend/{counselorId}")
    public ApiResponse<Object> analyzeWarningTrend(@PathVariable Long counselorId) {
        try {
            String aiAnalysis = tongYiService.generalChat(
                    "请分析辅导员ID为" + counselorId + "所管理学生的预警趋势，" +
                    "给出预警分布分析和重点关注建议。300字以内。");
            Map<String, Object> result = new HashMap<>();
            result.put("counselorId", counselorId);
            result.put("aiAnalysis", aiAnalysis != null ? aiAnalysis : "预警趋势分析中，请稍后重试");
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("趋势分析失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/analyze/{studentId}")
    public ApiResponse<Object> analyzeStudent(@PathVariable String studentId) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", studentId);
            request.put("studentName", "学生" + studentId);
            Object scoreAnalysis = aiService.analyzeScore(request);
            Object riskPrediction = aiService.predictRisk(request);

            Map<String, Object> result = new HashMap<>();
            result.put("scoreAnalysis", scoreAnalysis);
            result.put("riskPrediction", riskPrediction);
            // 用通义千问生成综合评语
            String comment = tongYiService.generalChat(
                    "请对学号为" + studentId + "的学生做综合学业评价，" +
                    "结合成绩和风险数据给出100字以内的总结和建议。");
            result.put("aiComment", comment);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("综合分析失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/analyze-warning/{warningId}")
    public ApiResponse<Object> analyzeWarning(@PathVariable Long warningId) {
        try {
            String aiAnalysis = tongYiService.generalChat(
                    "请分析预警记录ID为" + warningId + "的学业预警，" +
                    "给出该预警的严重性评估、原因分析和建议处理方案。200字以内。");
            Map<String, Object> result = new HashMap<>();
            result.put("warningId", warningId);
            result.put("aiAnalysis", aiAnalysis != null ? aiAnalysis : "预警分析中，请稍后重试");
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("预警分析失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/analyze-class-scores/{classId}")
    public ApiResponse<Object> analyzeClassScores(@PathVariable Long classId) {
        try {
            String aiAnalysis = tongYiService.generalChat(
                    "请分析班级ID为" + classId + "的学业情况，" +
                    "从整体成绩分布、薄弱环节、需要关注的学生群体等角度给出分析。300字以内。");
            Map<String, Object> result = new HashMap<>();
            result.put("classId", classId);
            result.put("aiAnalysis", aiAnalysis != null ? aiAnalysis : "班级分析中，请稍后重试");
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("班级成绩分析失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/warning-suggestions/{studentId}")
    public ApiResponse<Object> getWarningSuggestions(@PathVariable String studentId) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", studentId);
            return ApiResponse.success(aiService.generateSuggestion(request));
        } catch (Exception e) {
            log.error("预警建议获取失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/recommendations/{studentId}")
    public ApiResponse<Object> getRecommendations(@PathVariable String studentId) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", studentId);
            return ApiResponse.success(aiService.generateSuggestion(request));
        } catch (Exception e) {
            log.error("推荐获取失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/assess-risk/{studentId}")
    public ApiResponse<Object> assessRisk(@PathVariable String studentId) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", studentId);
            return ApiResponse.success(aiService.predictRisk(request));
        } catch (Exception e) {
            log.error("风险评估失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}
