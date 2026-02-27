package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

/**
 * 创建餐厅+管理员账号响应
 */
@Data
public class RestaurantWithAdminVO {
    // 餐厅信息
    @LongToString
    private Long restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantPhone;
    private String timezone;

    // 管理员账号信息
    @LongToString
    private Long adminId;
    private String adminEmail;
    private String adminName;
}
