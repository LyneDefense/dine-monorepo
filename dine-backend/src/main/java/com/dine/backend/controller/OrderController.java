package com.dine.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dine.backend.common.PageResult;
import com.dine.backend.common.Result;
import com.dine.backend.dto.request.OrderCreateRequest;
import com.dine.backend.dto.request.OrderStatusUpdateRequest;
import com.dine.backend.dto.response.OrderVO;
import com.dine.backend.dto.response.OrderListVO;
import com.dine.backend.entity.enums.OrderStatusEnum;
import com.dine.backend.entity.enums.OrderTypeEnum;
import com.dine.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Order", description = "订单管理接口")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "获取订单列表", description = "支持按类型、状态、日期、关键词筛选")
    @GetMapping
    public Result<PageResult<OrderListVO>> list(
            @PathVariable Long restaurantId,
            @Parameter(description = "订单类型") @RequestParam(required = false) OrderTypeEnum orderType,
            @Parameter(description = "订单状态") @RequestParam(required = false) OrderStatusEnum status,
            @Parameter(description = "日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<OrderListVO> result = orderService.getOrders(restaurantId, orderType, status, date, keyword, page, size);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getById(@PathVariable Long restaurantId, @PathVariable Long id) {
        return Result.success(orderService.getOrderById(restaurantId, id));
    }

    @Operation(summary = "根据订单号获取订单")
    @GetMapping("/number/{orderNumber}")
    public Result<OrderVO> getByOrderNumber(
            @PathVariable Long restaurantId,
            @PathVariable String orderNumber) {
        return Result.success(orderService.getOrderByNumber(restaurantId, orderNumber));
    }

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<OrderVO> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody OrderCreateRequest request) {
        return Result.success(orderService.createOrder(restaurantId, request));
    }

    @Operation(summary = "更新订单状态")
    @PatchMapping("/{id}/status")
    public Result<OrderVO> updateStatus(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        return Result.success(orderService.updateStatus(restaurantId, id, request.getStatus(), request.getReason(), null));
    }

    @Operation(summary = "确认订单")
    @PostMapping("/{id}/confirm")
    public Result<OrderVO> confirm(@PathVariable Long restaurantId, @PathVariable Long id) {
        return Result.success(orderService.confirmOrder(restaurantId, id));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<OrderVO> cancel(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @Parameter(description = "取消原因") @RequestParam(required = false) String reason) {
        return Result.success(orderService.cancelOrder(restaurantId, id, reason));
    }

    @Operation(summary = "完成订单")
    @PostMapping("/{id}/complete")
    public Result<OrderVO> complete(@PathVariable Long restaurantId, @PathVariable Long id) {
        return Result.success(orderService.completeOrder(restaurantId, id));
    }

    // ==================== Statistics ====================

    @Operation(summary = "获取待处理订单数量")
    @GetMapping("/stats/pending")
    public Result<Integer> getPendingCount(@PathVariable Long restaurantId) {
        return Result.success(orderService.getPendingCount(restaurantId));
    }
}
