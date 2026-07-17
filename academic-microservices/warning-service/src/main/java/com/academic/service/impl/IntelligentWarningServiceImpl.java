package com.academic.service.impl;

import com.academic.common.entity.AcademicWarning;
import com.academic.entity.WarningRule;
import com.academic.mapper.AcademicWarningMapper;
import com.academic.mapper.WarningRuleMapper;
import com.academic.service.IntelligentWarningService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IntelligentWarningServiceImpl implements IntelligentWarningService {

    private final AcademicWarningMapper academicWarningMapper;
    private final WarningRuleMapper warningRuleMapper;

    public IntelligentWarningServiceImpl(AcademicWarningMapper academicWarningMapper, WarningRuleMapper warningRuleMapper) {
        this.academicWarningMapper = academicWarningMapper;
        this.warningRuleMapper = warningRuleMapper;
    }

    @Override
    public Object generateWarning(Map<String, Object> request) {
        log.info("Generating warning: {}", request);
        
        Long studentId = request.get("studentId") != null ? Long.parseLong(request.get("studentId").toString()) : null;
        String studentName = request.get("studentName") != null ? request.get("studentName").toString() : null;
        Long ruleId = request.get("ruleId") != null ? Long.parseLong(request.get("ruleId").toString()) : null;
        Integer warningLevel = request.get("warningLevel") != null ? Integer.parseInt(request.get("warningLevel").toString()) : 2;
        String warningMessage = request.get("warningMessage") != null ? request.get("warningMessage").toString() : "学生学业状况需要关注";

        WarningRule rule = null;
        if (ruleId != null) {
            rule = warningRuleMapper.selectById(ruleId);
            if (rule != null) {
                warningLevel = rule.getWarningLevel();
                warningMessage = rule.getDescription();
            }
        }

        AcademicWarning warning = new AcademicWarning()
                .setStudentId(studentId)
                .setStudentName(studentName)
                .setRuleId(ruleId)
                .setWarningLevel(warningLevel)
                .setWarningMessage(warningMessage)
                .setStatus(0)
                .setCreatedAt(LocalDateTime.now());

        academicWarningMapper.insert(warning);
        
        Map<String, Object> result = new HashMap<>();
        result.put("warningId", warning.getId());
        result.put("warningLevel", warning.getWarningLevel());
        result.put("warningMessage", warning.getWarningMessage());
        result.put("studentId", warning.getStudentId());
        result.put("status", "success");
        
        return result;
    }

    @Override
    public Object handleWarning(Map<String, Object> request) {
        log.info("Handling warning: {}", request);
        
        Long warningId = request.get("warningId") != null ? Long.parseLong(request.get("warningId").toString()) : null;
        String handledBy = request.get("handledBy") != null ? request.get("handledBy").toString() : null;
        String handleResult = request.get("handleResult") != null ? request.get("handleResult").toString() : "已处理";

        if (warningId == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "预警ID不能为空");
            return result;
        }

        AcademicWarning warning = academicWarningMapper.selectById(warningId);
        if (warning == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "预警不存在");
            return result;
        }

        warning.setStatus(1)
               .setHandledBy(handledBy)
               .setHandleResult(handleResult)
               .setHandledAt(LocalDateTime.now());

        academicWarningMapper.updateById(warning);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "预警处理成功");
        result.put("warningId", warningId);
        
        return result;
    }

    @Override
    public Object getWarningList(Map<String, Object> params) {
        log.info("Getting warning list: {}", params);
        
        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        
        if (params != null) {
            if (params.containsKey("studentId")) {
                queryWrapper.eq("student_id", params.get("studentId"));
            }
            if (params.containsKey("warningLevel")) {
                queryWrapper.eq("warning_level", params.get("warningLevel"));
            }
            if (params.containsKey("status")) {
                queryWrapper.eq("status", params.get("status"));
            }
        }

        queryWrapper.orderByDesc("created_at");
        List<AcademicWarning> warnings = academicWarningMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("warnings", warnings);
        result.put("total", warnings.size());
        
        return result;
    }

    @Override
    public Object getWarningDetail(String warningId) {
        log.info("Getting warning detail: {}", warningId);
        
        Long id = Long.parseLong(warningId);
        AcademicWarning warning = academicWarningMapper.selectById(id);
        
        if (warning == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "预警不存在");
            return result;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", warning.getId());
        result.put("studentId", warning.getStudentId());
        result.put("studentName", warning.getStudentName());
        result.put("ruleId", warning.getRuleId());
        result.put("warningLevel", warning.getWarningLevel());
        result.put("warningMessage", warning.getWarningMessage());
        result.put("status", warning.getStatus());
        result.put("handledBy", warning.getHandledBy());
        result.put("handleResult", warning.getHandleResult());
        result.put("handledAt", warning.getHandledAt());
        result.put("createdAt", warning.getCreatedAt());

        if (warning.getRuleId() != null) {
            WarningRule rule = warningRuleMapper.selectById(warning.getRuleId());
            if (rule != null) {
                result.put("ruleName", rule.getRuleName());
                result.put("ruleCode", rule.getRuleCode());
            }
        }
        
        return result;
    }

    @Override
    public Object updateWarningStatus(String warningId, Map<String, Object> request) {
        log.info("Updating warning status: {} {}", warningId, request);
        
        Long id = Long.parseLong(warningId);
        Integer status = request.get("status") != null ? Integer.parseInt(request.get("status").toString()) : null;

        AcademicWarning warning = academicWarningMapper.selectById(id);
        if (warning == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "预警不存在");
            return result;
        }

        if (status != null) {
            warning.setStatus(status);
            if (status == 1 && warning.getHandledAt() == null) {
                warning.setHandledAt(LocalDateTime.now());
            }
        }

        academicWarningMapper.updateById(warning);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "预警状态更新成功");
        result.put("warningId", warningId);
        result.put("newStatus", warning.getStatus());
        
        return result;
    }

    @Override
    public Object getWarningsByStudentId(Long studentId) {
        log.info("Getting warnings by studentId: {}", studentId);
        
        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.orderByDesc("created_at");
        
        List<AcademicWarning> warnings = academicWarningMapper.selectList(queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("warnings", warnings);
        result.put("total", warnings.size());
        
        return result;
    }

    @Override
    public Object getWarningsByLevel(Integer level) {
        log.info("Getting warnings by level: {}", level);
        
        QueryWrapper<AcademicWarning> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("warning_level", level);
        queryWrapper.orderByDesc("created_at");
        
        List<AcademicWarning> warnings = academicWarningMapper.selectList(queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("warnings", warnings);
        result.put("total", warnings.size());
        
        return result;
    }
}