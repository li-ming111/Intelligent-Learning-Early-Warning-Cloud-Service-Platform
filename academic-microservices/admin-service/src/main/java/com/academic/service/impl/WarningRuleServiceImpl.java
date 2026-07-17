package com.academic.service.impl;

import com.academic.entity.WarningRule;
import com.academic.mapper.WarningRuleMapper;
import com.academic.service.WarningRuleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 预警规则服务实现类
 */
@Service
public class WarningRuleServiceImpl extends ServiceImpl<WarningRuleMapper, WarningRule> implements WarningRuleService {

    @Override
    public List<WarningRule> list() {
        List<WarningRule> rules = super.list();
        // 去重：根据规则名称去重
        return rules.stream()
                .collect(Collectors.toMap(
                        WarningRule::getRuleName, // 以规则名称为键
                        rule -> rule, // 以规则对象为值
                        (existing, replacement) -> existing // 如果键重复，保留 existing
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
    }
}
