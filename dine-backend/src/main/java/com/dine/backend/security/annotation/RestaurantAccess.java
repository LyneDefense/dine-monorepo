package com.dine.backend.security.annotation;

import java.lang.annotation.*;

/**
 * 餐厅访问权限验证注解
 * 验证当前用户是否有权访问URL中的restaurantId
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestaurantAccess {
    /**
     * 是否允许STAFF角色访问（默认不允许，只有ADMIN及以上可访问）
     */
    boolean allowStaff() default false;
}
