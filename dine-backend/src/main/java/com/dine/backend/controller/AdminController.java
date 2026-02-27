package com.dine.backend.controller;

import com.dine.backend.common.PageResult;
import com.dine.backend.common.Result;
import com.dine.backend.dto.request.CreateRestaurantWithAdminRequest;
import com.dine.backend.dto.response.AccountVO;
import com.dine.backend.dto.response.RestaurantVO;
import com.dine.backend.dto.response.RestaurantWithAdminVO;
import com.dine.backend.service.PlatformAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Platform Admin", description = "平台管理接口（仅SUPER_ADMIN）")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PlatformAdminService platformAdminService;

    @Operation(summary = "创建餐厅和管理员账号")
    @PostMapping("/restaurants")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<RestaurantWithAdminVO> createRestaurantWithAdmin(
            @Valid @RequestBody CreateRestaurantWithAdminRequest request) {
        return Result.success(platformAdminService.createRestaurantWithAdmin(request));
    }

    @Operation(summary = "获取所有餐厅列表")
    @GetMapping("/restaurants")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<PageResult<RestaurantVO>> listAllRestaurants(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(platformAdminService.listAllRestaurants(page, size));
    }

    @Operation(summary = "获取所有账号列表")
    @GetMapping("/accounts")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<PageResult<AccountVO>> listAllAccounts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(platformAdminService.listAllAccounts(page, size));
    }
}
