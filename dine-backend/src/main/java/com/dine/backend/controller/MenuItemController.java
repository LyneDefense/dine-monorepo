package com.dine.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dine.backend.common.PageResult;
import com.dine.backend.common.Result;
import com.dine.backend.dto.request.MenuItemCreateRequest;
import com.dine.backend.dto.request.MenuItemUpdateRequest;
import com.dine.backend.dto.request.MenuItemVariantRequest;
import com.dine.backend.dto.request.ModifierGroupRequest;
import com.dine.backend.dto.response.MenuItemVO;
import com.dine.backend.dto.response.MenuItemVariantVO;
import com.dine.backend.dto.response.ModifierGroupVO;
import com.dine.backend.security.annotation.RestaurantAccess;
import com.dine.backend.service.MenuItemService;
import com.dine.backend.service.MenuItemVariantService;
import com.dine.backend.service.ModifierGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Menu Item", description = "菜单项管理接口")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final MenuItemVariantService menuItemVariantService;
    private final ModifierGroupService modifierGroupService;

    // ==================== Menu Item CRUD ====================

    @Operation(summary = "获取菜单项列表", description = "支持按分类、关键词筛选")
    @GetMapping
    @RestaurantAccess(allowStaff = true)
    public Result<PageResult<MenuItemVO>> list(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<MenuItemVO> result = menuItemService.getMenuItems(restaurantId, categoryId, keyword, page, size);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "获取菜单项详情")
    @GetMapping("/{id}")
    @RestaurantAccess(allowStaff = true)
    public Result<MenuItemVO> getById(@PathVariable Long restaurantId, @PathVariable Long id) {
        return Result.success(menuItemService.getMenuItemById(restaurantId, id));
    }

    @Operation(summary = "创建菜单项")
    @PostMapping
    @RestaurantAccess
    public Result<MenuItemVO> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemCreateRequest request) {
        return Result.success(menuItemService.createMenuItem(restaurantId, request));
    }

    @Operation(summary = "更新菜单项")
    @PutMapping("/{id}")
    @RestaurantAccess
    public Result<MenuItemVO> update(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @Valid @RequestBody MenuItemUpdateRequest request) {
        return Result.success(menuItemService.updateMenuItem(restaurantId, id, request));
    }

    @Operation(summary = "删除菜单项")
    @DeleteMapping("/{id}")
    @RestaurantAccess
    public Result<Void> delete(@PathVariable Long restaurantId, @PathVariable Long id) {
        menuItemService.deleteMenuItem(restaurantId, id);
        return Result.success();
    }

    @Operation(summary = "更新菜单项可用状态")
    @PatchMapping("/{id}/availability")
    @RestaurantAccess
    public Result<Void> updateAvailability(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @RequestParam Boolean available) {
        menuItemService.updateAvailability(restaurantId, id, available);
        return Result.success();
    }

    // ==================== Menu Item Variants ====================

    @Operation(summary = "获取菜单项规格列表")
    @GetMapping("/{itemId}/variants")
    @RestaurantAccess(allowStaff = true)
    public Result<List<MenuItemVariantVO>> listVariants(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId) {
        return Result.success(menuItemVariantService.getVariants(itemId));
    }

    @Operation(summary = "创建菜单项规格")
    @PostMapping("/{itemId}/variants")
    @RestaurantAccess
    public Result<MenuItemVariantVO> createVariant(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @Valid @RequestBody MenuItemVariantRequest request) {
        return Result.success(menuItemVariantService.createVariant(itemId, request));
    }

    @Operation(summary = "更新菜单项规格")
    @PutMapping("/{itemId}/variants/{variantId}")
    @RestaurantAccess
    public Result<MenuItemVariantVO> updateVariant(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @PathVariable Long variantId,
            @Valid @RequestBody MenuItemVariantRequest request) {
        return Result.success(menuItemVariantService.updateVariant(itemId, variantId, request));
    }

    @Operation(summary = "删除菜单项规格")
    @DeleteMapping("/{itemId}/variants/{variantId}")
    @RestaurantAccess
    public Result<Void> deleteVariant(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @PathVariable Long variantId) {
        menuItemVariantService.deleteVariant(itemId, variantId);
        return Result.success();
    }

    // ==================== Modifier Groups ====================

    @Operation(summary = "获取加料组列表")
    @GetMapping("/{itemId}/modifier-groups")
    @RestaurantAccess(allowStaff = true)
    public Result<List<ModifierGroupVO>> listModifierGroups(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId) {
        return Result.success(modifierGroupService.getModifierGroups(itemId));
    }

    @Operation(summary = "创建加料组")
    @PostMapping("/{itemId}/modifier-groups")
    @RestaurantAccess
    public Result<ModifierGroupVO> createModifierGroup(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @Valid @RequestBody ModifierGroupRequest request) {
        return Result.success(modifierGroupService.createModifierGroup(itemId, request));
    }

    @Operation(summary = "更新加料组")
    @PutMapping("/{itemId}/modifier-groups/{groupId}")
    @RestaurantAccess
    public Result<ModifierGroupVO> updateModifierGroup(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @PathVariable Long groupId,
            @Valid @RequestBody ModifierGroupRequest request) {
        return Result.success(modifierGroupService.updateModifierGroup(itemId, groupId, request));
    }

    @Operation(summary = "删除加料组")
    @DeleteMapping("/{itemId}/modifier-groups/{groupId}")
    @RestaurantAccess
    public Result<Void> deleteModifierGroup(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @PathVariable Long groupId) {
        modifierGroupService.deleteModifierGroup(itemId, groupId);
        return Result.success();
    }

    // ==================== Aliases (for AI recognition) ====================

    @Operation(summary = "更新菜单项别名", description = "用于AI语音识别")
    @PutMapping("/{itemId}/aliases")
    @RestaurantAccess
    public Result<Void> updateAliases(
            @PathVariable Long restaurantId,
            @PathVariable Long itemId,
            @RequestBody List<String> aliases) {
        menuItemService.updateAliases(restaurantId, itemId, aliases);
        return Result.success();
    }
}
