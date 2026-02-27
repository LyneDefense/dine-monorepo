package com.dine.backend.controller;

import com.dine.backend.common.Result;
import com.dine.backend.dto.request.ComboRequest;
import com.dine.backend.dto.response.ComboVO;
import com.dine.backend.security.annotation.RestaurantAccess;
import com.dine.backend.service.ComboService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Combo", description = "套餐管理接口")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/combos")
@RequiredArgsConstructor
public class ComboController {

    private final ComboService comboService;

    @Operation(summary = "获取套餐列表")
    @GetMapping
    @RestaurantAccess(allowStaff = true)
    public Result<List<ComboVO>> list(@PathVariable Long restaurantId) {
        return Result.success(comboService.getCombos(restaurantId));
    }

    @Operation(summary = "获取套餐详情")
    @GetMapping("/{id}")
    @RestaurantAccess(allowStaff = true)
    public Result<ComboVO> getById(@PathVariable Long restaurantId, @PathVariable Long id) {
        return Result.success(comboService.getComboById(restaurantId, id));
    }

    @Operation(summary = "创建套餐")
    @PostMapping
    @RestaurantAccess
    public Result<ComboVO> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody ComboRequest request) {
        return Result.success(comboService.createCombo(restaurantId, request));
    }

    @Operation(summary = "更新套餐")
    @PutMapping("/{id}")
    @RestaurantAccess
    public Result<ComboVO> update(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @Valid @RequestBody ComboRequest request) {
        return Result.success(comboService.updateCombo(restaurantId, id, request));
    }

    @Operation(summary = "删除套餐")
    @DeleteMapping("/{id}")
    @RestaurantAccess
    public Result<Void> delete(@PathVariable Long restaurantId, @PathVariable Long id) {
        comboService.deleteCombo(restaurantId, id);
        return Result.success();
    }

    @Operation(summary = "更新套餐可用状态")
    @PatchMapping("/{id}/availability")
    @RestaurantAccess
    public Result<Void> updateAvailability(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @RequestParam Boolean available) {
        comboService.updateAvailability(restaurantId, id, available);
        return Result.success();
    }
}
