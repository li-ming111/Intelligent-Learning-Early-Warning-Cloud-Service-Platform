package com.academic.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * 数据库自动初始化组件
 * 应用启动时检查 users 表是否存在，若不存在则自动执行建表+种子数据脚本。
 * 所有 DDL 使用 CREATE TABLE IF NOT EXISTS，多服务并行启动时安全幂等。
 *
 * 注意：users.role 是 TINYINT(1)，JDBC 驱动需要 tinyInt1isBit=false 参数。
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    @ConditionalOnBean(DataSource.class)
    public CommandLineRunner initDatabase(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {

                // 检查核心表是否存在（只要 users 表不存在就视为全新库）
                var tables = conn.getMetaData().getTables(null, null, "users", null);
                if (!tables.next()) {
                    log.info("=== [DataInit] 检测到新数据库，开始自动建表... ===");
                    executeScript(stmt, "init_schema.sql");
                    log.info("=== [DataInit] 表结构创建完成（47张表） ===");
                    executeScript(stmt, "init_data.sql");
                    log.info("=== [DataInit] 种子数据插入完成 ===");
                } else {
                    log.info("[DataInit] 数据库已初始化，跳过建表。");
                }
            } catch (Exception e) {
                log.error("[DataInit] 数据库初始化失败: {}", e.getMessage(), e);
            }
        };
    }

    /**
     * 执行 classpath 下的 SQL 脚本。
     * 每行独立执行，捕获异常后继续，容忍 CREATE INDEX 重复等幂等问题。
     */
    private void executeScript(Statement stmt, String scriptPath) {
        try {
            ClassPathResource resource = new ClassPathResource(scriptPath);
            if (!resource.exists()) {
                log.warn("[DataInit] 脚本不存在，跳过: {}", scriptPath);
                return;
            }
            String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            String[] lines = sql.split("\n");

            int executed = 0;
            int skipped = 0;
            StringBuilder current = new StringBuilder();

            for (String line : lines) {
                String trimmed = line.trim();
                // 跳过空行和注释
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("#")) {
                    continue;
                }
                current.append(" ").append(trimmed);
                // 分号结尾 = 一条完整语句
                if (trimmed.endsWith(";")) {
                    String statement = current.toString().trim();
                    // 去掉末尾分号（Statement.execute 不加分号也能执行，加了有时报错）
                    if (statement.endsWith(";")) {
                        statement = statement.substring(0, statement.length() - 1).trim();
                    }
                    if (!statement.isEmpty()) {
                        try {
                            stmt.execute(statement);
                            executed++;
                        } catch (Exception e) {
                            String msg = e.getMessage();
                            // 忽略"已存在"类错误（多服务竞争/幂等执行）
                            if (msg != null && (msg.contains("already exists")
                                    || msg.contains("Duplicate")
                                    || msg.contains("duplicate"))) {
                                log.debug("[DataInit] 幂等跳过: {}", msg.split("\n")[0]);
                            } else {
                                log.warn("[DataInit] 语句执行失败 (脚本={}): {} - SQL: {}...",
                                        scriptPath, msg.split("\n")[0],
                                        statement.substring(0, Math.min(80, statement.length())));
                            }
                            skipped++;
                        }
                    }
                    current.setLength(0);
                }
            }
            log.info("[DataInit] 脚本 {} 完成: 成功 {} 条, 跳过 {} 条", scriptPath, executed, skipped);
        } catch (Exception e) {
            log.warn("[DataInit] 脚本读取异常 {}: {}", scriptPath, e.getMessage());
        }
    }
}
