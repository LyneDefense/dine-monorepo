package com.dine.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建餐厅+管理员账号请求
 */
@Data
public class CreateRestaurantWithAdminRequest {
    // 餐厅信息
    @NotBlank(message = "Restaurant name is required")
    private String restaurantName;

    @NotBlank(message = "Restaurant address is required")
    private String restaurantAddress;

    @NotBlank(message = "Restaurant phone is required")
    private String restaurantPhone;

    private String timezone = "America/New_York";

    // 管理员账号信息
    @NotBlank(message = "Admin email is required")
    @Email(message = "Invalid email format")
    private String adminEmail;

    @NotBlank(message = "Admin password is required")
    private String adminPassword;

    @NotBlank(message = "Admin name is required")
    private String adminName;
}
