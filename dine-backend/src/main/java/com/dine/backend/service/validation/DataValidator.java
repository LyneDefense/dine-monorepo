package com.dine.backend.service.validation;

import com.dine.backend.entity.*;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 数据验证工具类
 * 集中处理所有实体存在性和业务规则验证
 */
@Component
@RequiredArgsConstructor
public class DataValidator {

    private final RestaurantService restaurantService;
    private final MenuCategoryService menuCategoryService;
    private final MenuItemService menuItemService;
    private final ComboService comboService;
    private final DiningSectionService diningSectionService;
    private final DiningTableService diningTableService;

    // ==================== Restaurant Validation ====================

    /**
     * 验证餐厅存在
     */
    public Restaurant validateRestaurantExists(Long restaurantId) {
        Restaurant restaurant = restaurantService.getById(restaurantId);
        if (restaurant == null) {
            throw BusinessException.notFound("Restaurant not found: " + restaurantId);
        }
        return restaurant;
    }

    /**
     * 验证餐厅处于营业状态
     */
    public void validateRestaurantOpen(Long restaurantId) {
        if (!restaurantService.isOpen(restaurantId)) {
            throw BusinessException.badRequest("Restaurant is currently closed");
        }
    }

    // ==================== Category Validation ====================

    /**
     * 验证分类存在且属于指定餐厅
     */
    public MenuCategory validateCategoryExists(Long restaurantId, Long categoryId) {
        MenuCategory category = menuCategoryService.getById(categoryId);
        if (category == null) {
            throw BusinessException.notFound("Category not found: " + categoryId);
        }
        if (!category.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.badRequest("Category does not belong to this restaurant");
        }
        return category;
    }

    /**
     * 验证分类下没有菜单项（用于删除前检查）
     */
    public void validateCategoryEmpty(Long categoryId) {
        long itemCount = menuItemService.count(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MenuItem>()
                .eq(MenuItem::getCategoryId, categoryId)
        );
        if (itemCount > 0) {
            throw BusinessException.badRequest("Cannot delete category with " + itemCount + " items. Please delete or move items first.");
        }
    }

    // ==================== Menu Item Validation ====================

    /**
     * 验证菜单项存在且属于指定餐厅
     */
    public MenuItem validateMenuItemExists(Long restaurantId, Long itemId) {
        MenuItem item = menuItemService.getById(itemId);
        if (item == null) {
            throw BusinessException.notFound("Menu item not found: " + itemId);
        }
        if (!item.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.badRequest("Menu item does not belong to this restaurant");
        }
        return item;
    }

    /**
     * 验证菜单项可用（用于下单）
     */
    public void validateMenuItemAvailable(Long restaurantId, Long itemId) {
        if (!menuItemService.isAvailable(restaurantId, itemId)) {
            MenuItem item = menuItemService.getById(itemId);
            String itemName = item != null ? item.getName() : "Item " + itemId;
            throw BusinessException.badRequest("Menu item '" + itemName + "' is not available");
        }
    }

    // ==================== Combo Validation ====================

    /**
     * 验证套餐存在且属于指定餐厅
     */
    public Combo validateComboExists(Long restaurantId, Long comboId) {
        Combo combo = comboService.getById(comboId);
        if (combo == null) {
            throw BusinessException.notFound("Combo not found: " + comboId);
        }
        if (!combo.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.badRequest("Combo does not belong to this restaurant");
        }
        return combo;
    }

    /**
     * 验证套餐可用
     */
    public void validateComboAvailable(Long restaurantId, Long comboId) {
        if (!comboService.isAvailable(restaurantId, comboId)) {
            Combo combo = comboService.getById(comboId);
            String comboName = combo != null ? combo.getName() : "Combo " + comboId;
            throw BusinessException.badRequest("Combo '" + comboName + "' is not available");
        }
    }

    // ==================== Dining Section Validation ====================

    /**
     * 验证餐区存在且属于指定餐厅
     */
    public DiningSection validateSectionExists(Long restaurantId, Long sectionId) {
        DiningSection section = diningSectionService.getById(sectionId);
        if (section == null) {
            throw BusinessException.notFound("Dining section not found: " + sectionId);
        }
        if (!section.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.badRequest("Dining section does not belong to this restaurant");
        }
        return section;
    }

    /**
     * 验证餐区下没有餐桌（用于删除前检查）
     */
    public void validateSectionEmpty(Long sectionId) {
        long tableCount = diningTableService.listBySectionId(sectionId).size();
        if (tableCount > 0) {
            throw BusinessException.badRequest("Cannot delete section with " + tableCount + " tables. Please delete tables first.");
        }
    }

    // ==================== Dining Table Validation ====================

    /**
     * 验证餐桌存在且属于指定餐厅
     */
    public DiningTable validateTableExists(Long restaurantId, Long tableId) {
        DiningTable table = diningTableService.getById(tableId);
        if (table == null) {
            throw BusinessException.notFound("Dining table not found: " + tableId);
        }
        if (!table.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.badRequest("Dining table does not belong to this restaurant");
        }
        return table;
    }

    // ==================== Order Validation ====================

    /**
     * 验证订单中的所有菜单项都可用
     */
    public void validateOrderItemsAvailable(Long restaurantId, java.util.List<Long> itemIds) {
        for (Long itemId : itemIds) {
            validateMenuItemAvailable(restaurantId, itemId);
        }
    }
}
