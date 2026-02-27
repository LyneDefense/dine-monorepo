package com.dine.backend.security.aspect;

import com.dine.backend.entity.enums.AccountRoleEnum;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.security.AccountUserDetails;
import com.dine.backend.security.annotation.RestaurantAccess;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 餐厅访问权限验证切面
 */
@Slf4j
@Aspect
@Component
public class RestaurantAccessAspect {

    @Before("@annotation(restaurantAccess)")
    public void checkRestaurantAccess(JoinPoint joinPoint, RestaurantAccess restaurantAccess) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AccountUserDetails)) {
            throw BusinessException.unauthorized("Not authenticated");
        }

        AccountUserDetails userDetails = (AccountUserDetails) authentication.getPrincipal();
        AccountRoleEnum role = userDetails.getAccount().getRole();
        Long userRestaurantId = userDetails.getRestaurantId();

        // SUPER_ADMIN 可以访问任何餐厅
        if (role == AccountRoleEnum.SUPER_ADMIN) {
            return;
        }

        // STAFF 角色检查
        if (role == AccountRoleEnum.STAFF && !restaurantAccess.allowStaff()) {
            throw BusinessException.forbidden("Staff access not allowed for this operation");
        }

        // 获取方法参数中的 restaurantId
        Long pathRestaurantId = extractRestaurantId(joinPoint);
        if (pathRestaurantId == null) {
            log.warn("No restaurantId found in method parameters for {}", joinPoint.getSignature().getName());
            return;
        }

        // 验证用户是否有权访问该餐厅
        if (!pathRestaurantId.equals(userRestaurantId)) {
            log.warn("User {} (restaurant={}) attempted to access restaurant {}",
                    userDetails.getAccountId(), userRestaurantId, pathRestaurantId);
            throw BusinessException.forbidden("Access denied to restaurant: " + pathRestaurantId);
        }
    }

    /**
     * 从方法参数中提取 restaurantId
     * 优先查找名为 "restaurantId" 的参数，其次查找带 @PathVariable 且名为 "restaurantId" 或 "id" 的参数
     */
    private Long extractRestaurantId(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();

        // 首先查找名为 "restaurantId" 的参数
        for (int i = 0; i < parameterNames.length; i++) {
            if ("restaurantId".equals(parameterNames[i]) && args[i] instanceof Long) {
                return (Long) args[i];
            }
        }

        // 其次查找带 @PathVariable 注解且名为 "id" 的参数（用于 /restaurants/{id} 这种路径）
        for (int i = 0; i < parameters.length; i++) {
            PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
            if (pathVariable != null) {
                String name = pathVariable.value().isEmpty() ? parameterNames[i] : pathVariable.value();
                if ("id".equals(name) && args[i] instanceof Long) {
                    // 检查方法路径是否是直接操作餐厅的（如 /restaurants/{id}）
                    String methodName = method.getName();
                    if (methodName.contains("Restaurant") ||
                        method.getDeclaringClass().getSimpleName().equals("RestaurantController")) {
                        return (Long) args[i];
                    }
                }
            }
        }

        return null;
    }
}
