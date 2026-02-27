package com.dine.backend.controller;

import com.dine.backend.common.Result;
import com.dine.backend.dto.request.OrderTypeConfigRequest;
import com.dine.backend.dto.response.OrderTypeConfigVO;
import com.dine.backend.entity.enums.OrderTypeEnum;
import com.dine.backend.security.annotation.RestaurantAccess;
import com.dine.backend.service.OrderTypeConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order Type Config", description = "订单类型配置接口")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/order-types")
@RequiredArgsConstructor
public class OrderTypeConfigController {

    private final OrderTypeConfigService orderTypeConfigService;

    @Operation(summary = "获取所有订单类型配置")
    @GetMapping
    @RestaurantAccess(allowStaff = true)
    public Result<List<OrderTypeConfigVO>> getConfigs(@PathVariable Long restaurantId) {
        return Result.success(orderTypeConfigService.getConfigs(restaurantId));
    }

    @Operation(summary = "获取指定订单类型配置")
    @GetMapping("/{orderType}")
    @RestaurantAccess(allowStaff = true)
    public Result<OrderTypeConfigVO> getConfig(
            @PathVariable Long restaurantId,
            @PathVariable OrderTypeEnum orderType) {
        return Result.success(orderTypeConfigService.getConfig(restaurantId, orderType));
    }

    @Operation(summary = "创建或更新订单类型配置")
    @PutMapping
    @RestaurantAccess
    public Result<OrderTypeConfigVO> saveOrUpdateConfig(
            @PathVariable Long restaurantId,
            @Valid @RequestBody OrderTypeConfigRequest request) {
        return Result.success(orderTypeConfigService.saveOrUpdateConfig(restaurantId, request));
    }

    @Operation(summary = "切换订单类型启用状态")
    @PatchMapping("/{orderType}/toggle")
    @RestaurantAccess
    public Result<OrderTypeConfigVO> toggleEnabled(
            @PathVariable Long restaurantId,
            @PathVariable OrderTypeEnum orderType,
            @RequestParam Boolean enabled) {
        return Result.success(orderTypeConfigService.toggleEnabled(restaurantId, orderType, enabled));
    }

    @Operation(summary = "初始化默认订单类型配置")
    @PostMapping("/init")
    @RestaurantAccess
    public Result<List<OrderTypeConfigVO>> initDefaultConfigs(@PathVariable Long restaurantId) {
        orderTypeConfigService.initDefaultConfigs(restaurantId);
        return Result.success(orderTypeConfigService.getConfigs(restaurantId));
    }
}
