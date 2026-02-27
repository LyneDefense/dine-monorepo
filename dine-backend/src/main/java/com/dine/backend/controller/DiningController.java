package com.dine.backend.controller;

import com.dine.backend.common.Result;
import com.dine.backend.dto.request.DiningSectionRequest;
import com.dine.backend.dto.request.DiningTableRequest;
import com.dine.backend.dto.response.DiningSectionVO;
import com.dine.backend.dto.response.DiningTableVO;
import com.dine.backend.entity.enums.TableStatusEnum;
import com.dine.backend.security.annotation.RestaurantAccess;
import com.dine.backend.service.DiningSectionService;
import com.dine.backend.service.DiningTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Dining Section", description = "餐区和餐桌管理接口")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/dining")
@RequiredArgsConstructor
public class DiningController {

    private final DiningSectionService diningSectionService;
    private final DiningTableService diningTableService;

    // ==================== Dining Sections ====================

    @Operation(summary = "获取餐区列表")
    @GetMapping("/sections")
    @RestaurantAccess(allowStaff = true)
    public Result<List<DiningSectionVO>> listSections(@PathVariable Long restaurantId) {
        return Result.success(diningSectionService.getSections(restaurantId));
    }

    @Operation(summary = "获取餐区详情")
    @GetMapping("/sections/{id}")
    @RestaurantAccess(allowStaff = true)
    public Result<DiningSectionVO> getSectionById(@PathVariable Long restaurantId, @PathVariable Long id) {
        return Result.success(diningSectionService.getSectionById(restaurantId, id));
    }

    @Operation(summary = "创建餐区")
    @PostMapping("/sections")
    @RestaurantAccess
    public Result<DiningSectionVO> createSection(
            @PathVariable Long restaurantId,
            @Valid @RequestBody DiningSectionRequest request) {
        return Result.success(diningSectionService.createSection(restaurantId, request));
    }

    @Operation(summary = "更新餐区")
    @PutMapping("/sections/{id}")
    @RestaurantAccess
    public Result<DiningSectionVO> updateSection(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @Valid @RequestBody DiningSectionRequest request) {
        return Result.success(diningSectionService.updateSection(restaurantId, id, request));
    }

    @Operation(summary = "删除餐区")
    @DeleteMapping("/sections/{id}")
    @RestaurantAccess
    public Result<Void> deleteSection(@PathVariable Long restaurantId, @PathVariable Long id) {
        diningSectionService.deleteSection(restaurantId, id);
        return Result.success();
    }

    // ==================== Dining Tables ====================

    @Operation(summary = "获取所有餐桌", description = "可选按餐区筛选")
    @GetMapping("/tables")
    @RestaurantAccess(allowStaff = true)
    public Result<List<DiningTableVO>> listAllTables(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Long sectionId) {
        return Result.success(diningTableService.getTables(restaurantId, sectionId));
    }

    @Operation(summary = "获取餐区下的餐桌")
    @GetMapping("/sections/{sectionId}/tables")
    @RestaurantAccess(allowStaff = true)
    public Result<List<DiningTableVO>> listTables(
            @PathVariable Long restaurantId,
            @PathVariable Long sectionId) {
        return Result.success(diningTableService.getTables(restaurantId, sectionId));
    }

    @Operation(summary = "获取餐桌详情")
    @GetMapping("/tables/{tableId}")
    @RestaurantAccess(allowStaff = true)
    public Result<DiningTableVO> getTableById(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId) {
        return Result.success(diningTableService.getTableById(restaurantId, tableId));
    }

    @Operation(summary = "创建餐桌")
    @PostMapping("/sections/{sectionId}/tables")
    @RestaurantAccess
    public Result<DiningTableVO> createTable(
            @PathVariable Long restaurantId,
            @PathVariable Long sectionId,
            @Valid @RequestBody DiningTableRequest request) {
        return Result.success(diningTableService.createTable(restaurantId, sectionId, request));
    }

    @Operation(summary = "更新餐桌")
    @PutMapping("/tables/{tableId}")
    @RestaurantAccess
    public Result<DiningTableVO> updateTable(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId,
            @Valid @RequestBody DiningTableRequest request) {
        return Result.success(diningTableService.updateTable(restaurantId, tableId, request));
    }

    @Operation(summary = "删除餐桌")
    @DeleteMapping("/tables/{tableId}")
    @RestaurantAccess
    public Result<Void> deleteTable(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId) {
        diningTableService.deleteTable(restaurantId, tableId);
        return Result.success();
    }

    @Operation(summary = "更新餐桌状态")
    @PatchMapping("/tables/{tableId}/status")
    @RestaurantAccess(allowStaff = true)
    public Result<Void> updateTableStatus(
            @PathVariable Long restaurantId,
            @PathVariable Long tableId,
            @RequestParam TableStatusEnum status) {
        diningTableService.updateStatus(restaurantId, tableId, status);
        return Result.success();
    }
}
