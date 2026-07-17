package com.academic.config;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitExecutor;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 限流规则配置类
 * 支持 Nacos 动态配置，实现限流规则的热更新
 */
@Configuration
public class SentinelRuleConfig {

    private static final Logger logger = LoggerFactory.getLogger(SentinelRuleConfig.class);

    @Value("${spring.cloud.nacos.discovery.server-addr:localhost:8848}")
    private String nacosServerAddr;

    @Value("${sentinel.nacos.namespace:public}")
    private String nacosNamespace;

    @Value("${sentinel.nacos.group:SENTINEL_GROUP}")
    private String nacosGroup;

    private static final String FLOW_RULE_DATA_ID = "sentinel-flow-rules";
    private static final String DEGRADE_RULE_DATA_ID = "sentinel-degrade-rules";
    private static final String SYSTEM_RULE_DATA_ID = "sentinel-system-rules";

    @PostConstruct
    public void initRules() {
        initFlowRules();
        initDegradeRules();
        initSystemRules();
        initAuthorityRules();
        initNacosDataSource();
        logger.info("Sentinel 限流规则初始化完成，Nacos地址: {}, Namespace: {}, Group: {}",
                nacosServerAddr, nacosNamespace, nacosGroup);
    }

    /**
     * 初始化 Nacos 动态数据源
     * 实现规则的热更新，无需重启服务
     */
    private void initNacosDataSource() {
        try {
            NacosDataSource<List<FlowRule>> flowDataSource = new NacosDataSource<>(
                    nacosServerAddr,
                    nacosNamespace,
                    nacosGroup + ":" + FLOW_RULE_DATA_ID,
                    data -> JSON.parseObject(data, new TypeReference<List<FlowRule>>() {})
            );
            FlowRuleManager.register2Property(flowDataSource.getProperty());

            NacosDataSource<List<DegradeRule>> degradeDataSource = new NacosDataSource<>(
                    nacosServerAddr,
                    nacosNamespace,
                    nacosGroup + ":" + DEGRADE_RULE_DATA_ID,
                    data -> JSON.parseObject(data, new TypeReference<List<DegradeRule>>() {})
            );
            DegradeRuleManager.register2Property(degradeDataSource.getProperty());

            NacosDataSource<List<SystemRule>> systemDataSource = new NacosDataSource<>(
                    nacosServerAddr,
                    nacosNamespace,
                    nacosGroup + ":" + SYSTEM_RULE_DATA_ID,
                    data -> JSON.parseObject(data, new TypeReference<List<SystemRule>>() {})
            );
            SystemRuleManager.register2Property(systemDataSource.getProperty());

            logger.info("Sentinel Nacos 动态数据源初始化成功");
        } catch (Exception e) {
            logger.warn("Sentinel Nacos 动态数据源初始化失败，使用本地配置: {}", e.getMessage());
        }
    }

    /**
     * 初始化流控规则
     * 针对关键 API 配置限流策略
     */
    private void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        FlowRule rule1 = createFlowRule("adminApi:getUsers", 100, 0);
        FlowRule rule2 = createFlowRule("adminApi:createUser", 50, 0);
        FlowRule rule3 = createFlowRule("adminApi:updateUser", 50, 0);
        FlowRule rule4 = createFlowRule("adminApi:deleteUser", 30, 0);
        FlowRule rule5 = createFlowRule("adminApi:exportData", 10, 0);
        FlowRule rule6 = createFlowRule("adminApi:importData", 10, 0);
        FlowRule rule7 = createFlowRule("adminApi:searchUsers", 80, 0);
        FlowRule rule8 = createFlowRule("adminApi:getStatistics", 50, 0);

        FlowRule warningRule = createFlowRule("warningApi:trigger", 30, 0);
        FlowRule warningListRule = createFlowRule("warningApi:list", 100, 0);

        rules.add(rule1);
        rules.add(rule2);
        rules.add(rule3);
        rules.add(rule4);
        rules.add(rule5);
        rules.add(rule6);
        rules.add(rule7);
        rules.add(rule8);
        rules.add(warningRule);
        rules.add(warningListRule);

        FlowRuleManager.loadRules(rules);
    }

    private FlowRule createFlowRule(String resource, int count, int controlBehavior) {
        FlowRule rule = new FlowRule();
        rule.setResource(resource);
        rule.setGrade(1);
        rule.setCount(count);
        rule.setControlBehavior(controlBehavior);
        rule.setStrategy(0);
        rule.setClusterMode(false);
        return rule;
    }

    /**
     * 初始化熔断降级规则
     * 配置慢调用比例和异常比例熔断策略
     */
    private void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        DegradeRule adminDegrade = new DegradeRule();
        adminDegrade.setResource("adminApi:getUsers");
        adminDegrade.setGrade(0);
        adminDegrade.setCount(0.5);
        adminDegrade.setTimeWindow(10);
        adminDegrade.setMinRequestAmount(5);
        adminDegrade.setSlowRatioThreshold(0.5);
        rules.add(adminDegrade);

        DegradeRule warningDegrade = new DegradeRule();
        warningDegrade.setResource("warningApi:trigger");
        warningDegrade.setGrade(1);
        warningDegrade.setCount(0.3);
        warningDegrade.setTimeWindow(30);
        warningDegrade.setMinRequestAmount(3);
        rules.add(warningDegrade);

         DegradeRuleManager.loadRules(rules);
     }

    /**
     * 初始化系统规则
     * 保护系统整体稳定性
     */
    private void initSystemRules() {
        List<SystemRule> rules = new ArrayList<>();

        SystemRule rule = new SystemRule();
        rule.setResource("adminApi");
        rule.setHighestCpuUsage(0.8);
        rule.setHighestSystemLoad(10.0);
        rule.setAvgRt(1000);
        rule.setQps(1000);
        rules.add(rule);

        SystemRuleManager.loadRules(rules);
    }

    /**
     * 初始化授权规则
     * 控制不同应用的访问权限
     */
    private void initAuthorityRules() {
        List<AuthorityRule> rules = new ArrayList<>();

        AuthorityRule rule = new AuthorityRule();
        rule.setResource("adminApi:exportData");
        rule.setStrategy(1);
        rule.setLimitApp("gateway,web,app");

        rules.add(rule);
        AuthorityRuleManager.loadRules(rules);
    }
}
