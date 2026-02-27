package com.dine.backend.controller;

import com.dine.backend.common.Result;
import com.dine.backend.dto.request.RestaurantCreateRequest;
import com.dine.backend.dto.request.RestaurantUpdateRequest;
import com.dine.backend.dto.request.OperatingHoursRequest;
import com.dine.backend.dto.request.SpecialDateHoursRequest;
import com.dine.backend.dto.request.RestaurantSettingsRequest;
import com.dine.backend.dto.response.RestaurantVO;
import com.dine.backend.dto.response.OperatingHoursVO;
import com.dine.backend.dto.response.SpecialDateHoursVO;
import com.dine.backend.dto.response.RestaurantSettingsVO;
import com.dine.backend.service.RestaurantService;
import com.dine.backend.service.OperatingHoursService;
import com.dine.backend.service.SpecialDateHoursService;
import com.dine.backend.service.RestaurantSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Restaurant", description = "餐厅管理接口")
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final OperatingHoursService operatingHoursService;
    private final SpecialDateHoursService specialDateHoursService;
    private final RestaurantSettingsService restaurantSettingsService;

    // ==================== Restaurant CRUD ====================

    @Operation(summary = "创建餐厅")
    @PostMapping
    public Result<RestaurantVO> create(@Valid @RequestBody RestaurantCreateRequest request) {
        return Result.success(restaurantService.createRestaurant(request));
    }

    @Operation(summary = "获取餐厅详情")
    @GetMapping("/{id}")
    public Result<RestaurantVO> getById(@PathVariable Long id) {
        return Result.success(restaurantService.getRestaurantById(id));
    }

    @Operation(summary = "更新餐厅信息")
    @PutMapping("/{id}")
    public Result<RestaurantVO> update(@PathVariable Long id, @Valid @RequestBody RestaurantUpdateRequest request) {
        return Result.success(restaurantService.updateRestaurant(id, request));
    }

    @Operation(summary = "删除餐厅")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return Result.success();
    }

    // ==================== Operating Hours ====================

    @Operation(summary = "获取营业时间")
    @GetMapping("/{restaurantId}/operating-hours")
    public Result<List<OperatingHoursVO>> getOperatingHours(@PathVariable Long restaurantId) {
        return Result.success(operatingHoursService.getOperatingHours(restaurantId));
    }

    @Operation(summary = "更新营业时间")
    @PutMapping("/{restaurantId}/operating-hours")
    public Result<List<OperatingHoursVO>> updateOperatingHours(
            @PathVariable Long restaurantId,
            @Valid @RequestBody List<OperatingHoursRequest> requests) {
        return Result.success(operatingHoursService.updateOperatingHours(restaurantId, requests));
    }

    // ==================== Special Date Hours ====================

    @Operation(summary = "获取特殊日期营业时间")
    @GetMapping("/{restaurantId}/special-hours")
    public Result<List<SpecialDateHoursVO>> getSpecialDateHours(@PathVariable Long restaurantId) {
        return Result.success(specialDateHoursService.getSpecialDateHours(restaurantId));
    }

    @Operation(summary = "创建特殊日期营业时间")
    @PostMapping("/{restaurantId}/special-hours")
    public Result<SpecialDateHoursVO> createSpecialDateHours(
            @PathVariable Long restaurantId,
            @Valid @RequestBody SpecialDateHoursRequest request) {
        return Result.success(specialDateHoursService.createSpecialDateHours(restaurantId, request));
    }

    @Operation(summary = "更新特殊日期营业时间")
    @PutMapping("/{restaurantId}/special-hours/{id}")
    public Result<SpecialDateHoursVO> updateSpecialDateHours(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @Valid @RequestBody SpecialDateHoursRequest request) {
        return Result.success(specialDateHoursService.updateSpecialDateHours(restaurantId, id, request));
    }

    @Operation(summary = "删除特殊日期营业时间")
    @DeleteMapping("/{restaurantId}/special-hours/{id}")
    public Result<Void> deleteSpecialDateHours(@PathVariable Long restaurantId, @PathVariable Long id) {
        specialDateHoursService.deleteSpecialDateHours(restaurantId, id);
        return Result.success();
    }

    // ==================== Restaurant Settings ====================

    @Operation(summary = "获取餐厅设置")
    @GetMapping("/{restaurantId}/settings")
    public Result<RestaurantSettingsVO> getSettings(@PathVariable Long restaurantId) {
        return Result.success(restaurantSettingsService.getSettings(restaurantId));
    }

    @Operation(summary = "更新餐厅设置")
    @PutMapping("/{restaurantId}/settings")
    public Result<RestaurantSettingsVO> updateSettings(
            @PathVariable Long restaurantId,
            @Valid @RequestBody RestaurantSettingsRequest request) {
        return Result.success(restaurantSettingsService.updateSettings(restaurantId, request));
    }
}
