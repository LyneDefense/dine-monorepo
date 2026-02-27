package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.MenuCategoryRequest;
import com.dine.backend.dto.response.MenuCategoryVO;
import com.dine.backend.entity.MenuCategory;

import java.util.List;

public interface MenuCategoryService extends IService<MenuCategory> {

    List<MenuCategory> listByRestaurantId(Long restaurantId);

    List<MenuCategoryVO> getCategories(Long restaurantId);

    MenuCategoryVO getCategoryById(Long restaurantId, Long id);

    MenuCategoryVO createCategory(Long restaurantId, MenuCategoryRequest request);

    MenuCategoryVO updateCategory(Long restaurantId, Long id, MenuCategoryRequest request);

    void deleteCategory(Long restaurantId, Long id);

    void updateSortOrder(Long restaurantId, List<Long> categoryIds);
}
