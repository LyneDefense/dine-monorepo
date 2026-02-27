package com.dine.backend.controller;

import com.dine.backend.common.Result;
import com.dine.backend.dto.request.MenuCategoryRequest;
import com.dine.backend.dto.response.MenuCategoryVO;
import com.dine.backend.service.MenuCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Menu Category", description = "菜单分类管理接口")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/categories")
@RequiredArgsConstructor
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @Operation(summary = "获取分类列表")
    @GetMapping
    public Result<List<MenuCategoryVO>> list(@PathVariable Long restaurantId) {
        return Result.success(menuCategoryService.getCategories(restaurantId));
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<MenuCategoryVO> getById(@PathVariable Long restaurantId, @PathVariable Long id) {
        return Result.success(menuCategoryService.getCategoryById(restaurantId, id));
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<MenuCategoryVO> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuCategoryRequest request) {
        return Result.success(menuCategoryService.createCategory(restaurantId, request));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<MenuCategoryVO> update(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @Valid @RequestBody MenuCategoryRequest request) {
        return Result.success(menuCategoryService.updateCategory(restaurantId, id, request));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long restaurantId, @PathVariable Long id) {
        menuCategoryService.deleteCategory(restaurantId, id);
        return Result.success();
    }

    @Operation(summary = "更新分类排序")
    @PutMapping("/sort")
    public Result<Void> updateSortOrder(
            @PathVariable Long restaurantId,
            @RequestBody List<Long> categoryIds) {
        menuCategoryService.updateSortOrder(restaurantId, categoryIds);
        return Result.success();
    }
}
