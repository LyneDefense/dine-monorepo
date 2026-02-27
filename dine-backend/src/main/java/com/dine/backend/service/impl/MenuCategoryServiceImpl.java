package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.MenuCategoryRequest;
import com.dine.backend.dto.response.MenuCategoryVO;
import com.dine.backend.entity.MenuCategory;
import com.dine.backend.entity.MenuItem;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.MenuCategoryMapper;
import com.dine.backend.mapper.MenuItemMapper;
import com.dine.backend.service.MenuCategoryService;
import com.dine.backend.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuCategoryServiceImpl extends ServiceImpl<MenuCategoryMapper, MenuCategory> implements MenuCategoryService {

    private final EntityConverter converter;
    private final RestaurantService restaurantService;
    private final MenuItemMapper menuItemMapper;

    private void validateRestaurantExists(Long restaurantId) {
        if (restaurantService.getById(restaurantId) == null) {
            throw BusinessException.notFound("Restaurant not found: " + restaurantId);
        }
    }

    @Override
    public List<MenuCategory> listByRestaurantId(Long restaurantId) {
        return list(new LambdaQueryWrapper<MenuCategory>()
                .eq(MenuCategory::getRestaurantId, restaurantId)
                .orderByAsc(MenuCategory::getSortOrder));
    }

    @Override
    public List<MenuCategoryVO> getCategories(Long restaurantId) {
        validateRestaurantExists(restaurantId);
        List<MenuCategory> categories = listByRestaurantId(restaurantId);
        return converter.toMenuCategoryVOList(categories);
    }

    @Override
    public MenuCategoryVO getCategoryById(Long restaurantId, Long id) {
        validateRestaurantExists(restaurantId);
        MenuCategory category = getById(id);
        if (category == null || !category.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Category not found");
        }
        return converter.toMenuCategoryVO(category);
    }

    @Override
    @Transactional
    public MenuCategoryVO createCategory(Long restaurantId, MenuCategoryRequest request) {
        validateRestaurantExists(restaurantId);

        // 检查同名分类是否已存在
        long nameCount = count(new LambdaQueryWrapper<MenuCategory>()
                .eq(MenuCategory::getRestaurantId, restaurantId)
                .eq(MenuCategory::getName, request.getName()));
        if (nameCount > 0) {
            throw BusinessException.badRequest("Category with name '" + request.getName() + "' already exists");
        }

        MenuCategory category = new MenuCategory();
        BeanUtils.copyProperties(request, category);
        category.setRestaurantId(restaurantId);

        if (category.getSortOrder() == null) {
            Long count = count(new LambdaQueryWrapper<MenuCategory>()
                    .eq(MenuCategory::getRestaurantId, restaurantId));
            category.setSortOrder(count.intValue());
        }

        save(category);
        return converter.toMenuCategoryVO(category);
    }

    @Override
    @Transactional
    public MenuCategoryVO updateCategory(Long restaurantId, Long id, MenuCategoryRequest request) {
        validateRestaurantExists(restaurantId);
        MenuCategory category = getById(id);
        if (category == null || !category.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Category not found");
        }

        // 检查同名分类是否已存在（排除自身）
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            long nameCount = count(new LambdaQueryWrapper<MenuCategory>()
                    .eq(MenuCategory::getRestaurantId, restaurantId)
                    .eq(MenuCategory::getName, request.getName())
                    .ne(MenuCategory::getId, id));
            if (nameCount > 0) {
                throw BusinessException.badRequest("Category with name '" + request.getName() + "' already exists");
            }
        }

        if (request.getName() != null) category.setName(request.getName());
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getSortOrder() != null) category.setSortOrder(request.getSortOrder());

        updateById(category);
        return converter.toMenuCategoryVO(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long restaurantId, Long id) {
        validateRestaurantExists(restaurantId);
        MenuCategory category = getById(id);
        if (category == null || !category.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Category not found");
        }

        // 检查分类下是否有菜单项
        long itemCount = menuItemMapper.selectCount(new LambdaQueryWrapper<MenuItem>()
                .eq(MenuItem::getCategoryId, id));
        if (itemCount > 0) {
            throw BusinessException.badRequest("Cannot delete category with " + itemCount + " items. Please delete or move items first.");
        }

        removeById(id);
    }

    @Override
    @Transactional
    public void updateSortOrder(Long restaurantId, List<Long> categoryIds) {
        validateRestaurantExists(restaurantId);
        for (int i = 0; i < categoryIds.size(); i++) {
            MenuCategory category = getById(categoryIds.get(i));
            if (category != null && category.getRestaurantId().equals(restaurantId)) {
                category.setSortOrder(i);
                updateById(category);
            }
        }
    }
}
