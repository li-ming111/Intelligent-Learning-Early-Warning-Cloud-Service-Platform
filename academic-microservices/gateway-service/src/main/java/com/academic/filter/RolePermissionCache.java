package com.academic.filter;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加载并缓存 auth-service 中的角色-权限映射，
 * 供 AuthenticationFilter 的 RBAC 鉴权使用。
 */
@Component
public class RolePermissionCache {

    private static final Logger log = LoggerFactory.getLogger(RolePermissionCache.class);

    /** role_code → Set<permission_code> */
    private final Map<String, Set<String>> rbacMap = new ConcurrentHashMap<>();

    /** 是否有 RBAC 数据（RBAC 未配置时回退到路径前缀模式） */
    private volatile boolean hasRbacData = false;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8072")
            .build();

    @PostConstruct
    public void init() {
        loadRbacMap();
    }

    @SuppressWarnings("unchecked")
    private void loadRbacMap() {
        try {
            Map<String, Object> response = webClient.get()
                    .uri("/role-permissions/rbac-map")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                log.warn("RBAC响应为空，回退到路径前缀鉴权");
                return;
            }

            // 解析 JSON: {code:200, data:{"student":["score:read",...], ...}}
            Object dataObj = response.get("data");
            if (!(dataObj instanceof Map)) {
                log.warn("RBAC data 格式异常，回退到路径前缀鉴权");
                return;
            }

            Map<String, Object> data = (Map<String, Object>) dataObj;
            rbacMap.clear();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String roleCode = entry.getKey();
                Object permList = entry.getValue();
                if (permList instanceof List) {
                    List<String> perms = (List<String>) permList;
                    rbacMap.put(roleCode, new HashSet<>(perms));
                }
            }

            hasRbacData = !rbacMap.isEmpty();
            log.info("RBAC映射加载完成: {} 个角色, 共 {} 条权限", rbacMap.size(),
                    rbacMap.values().stream().mapToInt(Set::size).sum());
        } catch (Exception e) {
            log.warn("RBAC映射加载失败，回退到路径前缀鉴权: {}", e.getMessage());
        }
    }

    /**
     * 检查指定角色是否拥有某个权限。
     * @param username 用户名（用于日志）
     * @param role 角色编码 (1-5)
     * @param permissionCode 权限编码，如 "student:read"
     * @return true=有权限 false=无权限
     */
    public boolean hasPermission(String username, int role, String permissionCode) {
        if (!hasRbacData) {
            return false; // RBAC未加载，统一拒绝
        }

        String roleCode = roleToCode(role);
        Set<String> permissions = rbacMap.get(roleCode);
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        return permissions.contains(permissionCode);
    }

    /** 是否已加载RBAC数据 */
    public boolean hasRbacData() {
        return hasRbacData;
    }

    /** 手动重新加载 */
    public void reload() {
        loadRbacMap();
    }

    private String roleToCode(int role) {
        switch (role) {
            case 1: return "student";
            case 2: return "teacher";
            case 3: return "counselor";
            case 4: return "admin";
            case 5: return "college_admin";
            default: return "unknown";
        }
    }
}
