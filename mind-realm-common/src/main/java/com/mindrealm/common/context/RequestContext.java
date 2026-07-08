package com.mindrealm.common.context;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @className: RequestContext
 * @description: 请求上下文工具类,用于获取当前登录用户的信息
 *               通过SecurityContextHolder获取当前用户的ID、用户名、角色等信息
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public class RequestContext {

    private RequestContext() {}

    /**
     * @description: 获取当前登录用户的ID
     * @return Long 用户ID,如果未登录则返回null
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null) {
            Object principal = auth.getPrincipal();
            // 支持UserPrincipal类型
            if (principal.getClass().getSimpleName().equals("UserPrincipal")) {
                try {
                    return (Long) principal.getClass().getMethod("getUserId").invoke(principal);
                } catch (Exception ignored) {}
            }
            // 兼容旧逻辑
            if (principal instanceof Long) {
                return (Long) principal;
            }
        }
        return null;
    }

    /**
     * @description: 获取当前登录用户的用户名
     * @return String 用户名,如果未登录则返回null
     */
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Object principal = auth.getPrincipal();
            // 支持UserPrincipal类型
            if (principal != null && principal.getClass().getSimpleName().equals("UserPrincipal")) {
                try {
                    return (String) principal.getClass().getMethod("getUsername").invoke(principal);
                } catch (Exception ignored) {}
            }
            // 从auth.getName()获取
            String name = auth.getName();
            if (name != null && !"anonymousUser".equals(name) && !name.startsWith("anonymous")) {
                return name;
            }
            // 从details获取
            if (auth.getDetails() != null) {
                return auth.getDetails().toString();
            }
        }
        return null;
    }

    /**
     * @description: 获取当前用户的角色
     * @return Integer 用户角色(1普通用户,2咨询师,3管理员),默认返回1
     */
    public static Integer getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            for (var authority : auth.getAuthorities()) {
                String role = authority.getAuthority();
                if (role.startsWith("ROLE_")) {
                    return switch (role) {
                        case "ROLE_ADMIN" -> 3;
                        case "ROLE_COUNSELOR" -> 2;
                        case "ROLE_PARENT" -> 5;
                        default -> 1;
                    };
                }
            }
        }
        return 1;
    }

    /**
     * @description: 检查当前用户是否已认证登录
     * @return boolean 已登录返回true,否则返回false
     */
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && auth.getPrincipal() != null 
               && !"anonymousUser".equals(auth.getPrincipal());
    }

    /**
     * @description: 检查当前用户是否是管理员
     * @return boolean 如果是管理员返回true,否则返回false
     */
    public static boolean isAdmin() {
        return getCurrentUserRole() == 3;
    }

    /**
     * @description: 检查当前用户是否是咨询师或管理员
     * @return boolean 如果是咨询师或管理员返回true,否则返回false
     */
    public static boolean isCounselor() {
        Integer role = getCurrentUserRole();
        return role == 2 || role == 3;
    }
    
    /**
     * @description: 检查当前用户是否是家长
     * @return boolean 如果是家长返回true,否则返回false
     */
    public static boolean isParent() {
        return getCurrentUserRole() == 5;
    }

    /**
     * @description: 获取用户信息摘要，用于日志输出
     * @return String 格式: "username(userId)"
     */
    public static String getUserSummary() {
        Long userId = getCurrentUserId();
        String username = getCurrentUsername();
        if (userId == null && username == null) {
            return "匿名用户";
        }
        return (username != null ? username : "?") + "(" + (userId != null ? userId : "-") + ")";
    }
}