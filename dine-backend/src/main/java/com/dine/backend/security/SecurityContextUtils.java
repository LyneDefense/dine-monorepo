package com.dine.backend.security;

import com.dine.backend.entity.enums.AccountRoleEnum;
import com.dine.backend.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具类
 */
public final class SecurityContextUtils {

    private SecurityContextUtils() {}

    /**
     * 获取当前登录用户
     */
    public static AccountUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AccountUserDetails)) {
            throw BusinessException.unauthorized("Not authenticated");
        }
        return (AccountUserDetails) auth.getPrincipal();
    }

    /**
     * 获取当前用户的账户ID
     */
    public static Long getCurrentAccountId() {
        return getCurrentUser().getAccountId();
    }

    /**
     * 获取当前用户的餐厅ID（SUPER_ADMIN返回null）
     */
    public static Long getCurrentRestaurantId() {
        return getCurrentUser().getRestaurantId();
    }

    /**
     * 获取当前用户的角色
     */
    public static AccountRoleEnum getCurrentRole() {
        return getCurrentUser().getAccount().getRole();
    }

    /**
     * 检查当前用户是否为超级管理员
     */
    public static boolean isSuperAdmin() {
        return getCurrentRole() == AccountRoleEnum.SUPER_ADMIN;
    }

    /**
     * 检查当前用户是否为商家/店长
     */
    public static boolean isAdmin() {
        return getCurrentRole() == AccountRoleEnum.ADMIN;
    }

    /**
     * 检查当前用户是否为员工
     */
    public static boolean isStaff() {
        return getCurrentRole() == AccountRoleEnum.STAFF;
    }

    /**
     * 验证用户是否有权访问指定餐厅
     * @param restaurantId 要访问的餐厅ID
     * @throws BusinessException 如果无权访问
     */
    public static void validateRestaurantAccess(Long restaurantId) {
        if (isSuperAdmin()) {
            return; // SUPER_ADMIN可访问任何餐厅
        }
        Long userRestaurantId = getCurrentRestaurantId();
        if (userRestaurantId == null || !userRestaurantId.equals(restaurantId)) {
            throw BusinessException.forbidden("Access denied to restaurant: " + restaurantId);
        }
    }

    /**
     * 验证用户是否有权访问指定餐厅（考虑STAFF权限）
     * @param restaurantId 要访问的餐厅ID
     * @param allowStaff 是否允许STAFF访问
     * @throws BusinessException 如果无权访问
     */
    public static void validateRestaurantAccess(Long restaurantId, boolean allowStaff) {
        if (isSuperAdmin()) {
            return;
        }

        // 检查角色权限
        if (isStaff() && !allowStaff) {
            throw BusinessException.forbidden("Staff access not allowed for this operation");
        }

        // 检查餐厅归属
        validateRestaurantAccess(restaurantId);
    }
}
