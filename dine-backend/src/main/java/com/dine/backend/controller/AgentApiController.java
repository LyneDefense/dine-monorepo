package com.dine.backend.controller;

import com.dine.backend.common.Result;
import com.dine.backend.dto.request.OrderCreateRequest;
import com.dine.backend.dto.response.*;
import com.dine.backend.entity.MenuItem;
import com.dine.backend.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API endpoints for AI Agent integration.
 * These endpoints are called by the Python AI Agent for phone ordering.
 */
@Tag(name = "AI Phone Agent", description = "AI电话代理集成接口")
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentApiController {

    private final RestaurantService restaurantService;
    private final MenuCategoryService menuCategoryService;
    private final MenuItemService menuItemService;
    private final ComboService comboService;
    private final OrderService orderService;
    private final DiningTableService diningTableService;
    private final AiPhoneSettingsService aiPhoneSettingsService;

    // ==================== Restaurant Info ====================

    @Operation(summary = "获取餐厅信息")
    @GetMapping("/restaurants/{restaurantId}")
    public Result<RestaurantVO> getRestaurantInfo(@PathVariable Long restaurantId) {
        return Result.success(restaurantService.getRestaurantById(restaurantId));
    }

    @Operation(summary = "检查餐厅是否营业")
    @GetMapping("/restaurants/{restaurantId}/is-open")
    public Result<Boolean> isRestaurantOpen(@PathVariable Long restaurantId) {
        return Result.success(restaurantService.isOpen(restaurantId));
    }

    // ==================== Menu ====================

    @Operation(summary = "获取完整菜单")
    @GetMapping("/restaurants/{restaurantId}/menu")
    public Result<List<MenuCategoryVO>> getFullMenu(@PathVariable Long restaurantId) {
        return Result.success(menuCategoryService.getCategories(restaurantId));
    }

    @Operation(summary = "搜索菜单项", description = "支持名称和别名搜索")
    @GetMapping("/restaurants/{restaurantId}/items/search")
    public Result<List<MenuItem>> searchMenuItems(
            @PathVariable Long restaurantId,
            @RequestParam String keyword) {
        return Result.success(menuItemService.searchByKeyword(restaurantId, keyword));
    }

    @Operation(summary = "获取菜单项详情")
    @GetMapping("/restaurants/{restaurantId}/items/{itemId}")
    public Result<MenuItemVO> getMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId) {
        return Result.success(menuItemService.getMenuItemById(restaurantId, itemId));
    }

    @Operation(summary = "检查菜单项是否可用")
    @GetMapping("/restaurants/{restaurantId}/items/{itemId}/available")
    public Result<Boolean> isItemAvailable(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId) {
        return Result.success(menuItemService.isAvailable(restaurantId, itemId));
    }

    // ==================== Combos ====================

    @Operation(summary = "获取套餐列表")
    @GetMapping("/restaurants/{restaurantId}/combos")
    public Result<List<ComboVO>> getCombos(@PathVariable Long restaurantId) {
        return Result.success(comboService.getCombos(restaurantId));
    }

    @Operation(summary = "检查套餐是否可用")
    @GetMapping("/restaurants/{restaurantId}/combos/{comboId}/available")
    public Result<Boolean> isComboAvailable(
            @PathVariable Long restaurantId,
            @PathVariable Long comboId) {
        return Result.success(comboService.isAvailable(restaurantId, comboId));
    }

    // ==================== Tables (for Dine-in) ====================

    @Operation(summary = "获取可用餐桌")
    @GetMapping("/restaurants/{restaurantId}/tables/available")
    public Result<List<DiningTableVO>> getAvailableTables(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Long sectionId) {
        return Result.success(diningTableService.getTables(restaurantId, sectionId));
    }

    // ==================== Orders ====================

    @Operation(summary = "创建订单", description = "AI代理创建电话订单")
    @PostMapping("/restaurants/{restaurantId}/orders")
    public Result<OrderVO> createOrder(
            @PathVariable Long restaurantId,
            @Valid @RequestBody OrderCreateRequest request) {
        return Result.success(orderService.createOrder(restaurantId, request));
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/restaurants/{restaurantId}/orders/{orderId}")
    public Result<OrderVO> getOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {
        return Result.success(orderService.getOrderById(restaurantId, orderId));
    }

    @Operation(summary = "根据订单号获取订单")
    @GetMapping("/restaurants/{restaurantId}/orders/number/{orderNumber}")
    public Result<OrderVO> getOrderByNumber(
            @PathVariable Long restaurantId,
            @PathVariable String orderNumber) {
        return Result.success(orderService.getOrderByNumber(restaurantId, orderNumber));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/restaurants/{restaurantId}/orders/{orderId}/cancel")
    public Result<OrderVO> cancelOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason) {
        return Result.success(orderService.cancelOrder(restaurantId, orderId, reason));
    }

    // ==================== AI Phone Settings ====================

    @Operation(summary = "获取AI配置", description = "获取AI电话代理的配置信息")
    @GetMapping("/restaurants/{restaurantId}/ai-config")
    public Result<AiPhoneSettingsVO> getAiConfig(@PathVariable Long restaurantId) {
        return Result.success(aiPhoneSettingsService.getSettings(restaurantId));
    }
}
