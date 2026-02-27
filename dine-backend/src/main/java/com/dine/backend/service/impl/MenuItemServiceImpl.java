package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.MenuItemCreateRequest;
import com.dine.backend.dto.request.MenuItemUpdateRequest;
import com.dine.backend.dto.request.MenuItemVariantRequest;
import com.dine.backend.dto.request.ModifierGroupRequest;
import com.dine.backend.dto.request.ModifierOptionRequest;
import com.dine.backend.dto.response.MenuItemVO;
import com.dine.backend.entity.MenuCategory;
import com.dine.backend.entity.MenuItem;
import com.dine.backend.entity.MenuItemAlias;
import com.dine.backend.entity.MenuItemVariant;
import com.dine.backend.entity.ModifierGroup;
import com.dine.backend.entity.ModifierOption;
import com.dine.backend.entity.Restaurant;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.MenuItemMapper;
import com.dine.backend.service.MenuCategoryService;
import com.dine.backend.service.MenuItemAliasService;
import com.dine.backend.service.MenuItemService;
import com.dine.backend.service.MenuItemVariantService;
import com.dine.backend.service.ModifierGroupService;
import com.dine.backend.service.ModifierOptionService;
import com.dine.backend.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl extends ServiceImpl<MenuItemMapper, MenuItem> implements MenuItemService {

    private final EntityConverter converter;
    private final MenuItemVariantService variantService;
    private final ModifierGroupService modifierGroupService;
    private final ModifierOptionService modifierOptionService;
    private final MenuItemAliasService aliasService;
    private final RestaurantService restaurantService;
    private final MenuCategoryService menuCategoryService;

    private void validateRestaurantExists(Long restaurantId) {
        Restaurant restaurant = restaurantService.getById(restaurantId);
        if (restaurant == null) {
            throw BusinessException.notFound("Restaurant not found: " + restaurantId);
        }
    }

    private void validateCategoryExists(Long restaurantId, Long categoryId) {
        if (categoryId == null) return;
        MenuCategory category = menuCategoryService.getById(categoryId);
        if (category == null) {
            throw BusinessException.notFound("Category not found: " + categoryId);
        }
        if (!category.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.badRequest("Category does not belong to this restaurant");
        }
    }

    @Override
    public List<MenuItem> listByRestaurantId(Long restaurantId) {
        return list(new LambdaQueryWrapper<MenuItem>()
                .eq(MenuItem::getRestaurantId, restaurantId)
                .orderByAsc(MenuItem::getSortOrder));
    }

    @Override
    public List<MenuItem> listByCategoryId(Long categoryId) {
        return list(new LambdaQueryWrapper<MenuItem>()
                .eq(MenuItem::getCategoryId, categoryId)
                .orderByAsc(MenuItem::getSortOrder));
    }

    @Override
    public List<MenuItem> searchByKeyword(Long restaurantId, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return listByRestaurantId(restaurantId);
        }

        // Search by item name
        List<MenuItem> byName = list(new LambdaQueryWrapper<MenuItem>()
                .eq(MenuItem::getRestaurantId, restaurantId)
                .like(MenuItem::getName, keyword));

        // Search by alias
        List<MenuItemAlias> aliases = aliasService.searchByAlias(restaurantId, keyword);
        List<Long> aliasItemIds = aliases.stream()
                .map(MenuItemAlias::getItemId)
                .distinct()
                .collect(Collectors.toList());

        if (!aliasItemIds.isEmpty()) {
            List<MenuItem> byAlias = listByIds(aliasItemIds);
            // Merge results
            byAlias.stream()
                    .filter(item -> byName.stream().noneMatch(i -> i.getId().equals(item.getId())))
                    .forEach(byName::add);
        }

        return byName;
    }

    @Override
    public IPage<MenuItemVO> getMenuItems(Long restaurantId, Long categoryId, String keyword, Integer page, Integer size) {
        validateRestaurantExists(restaurantId);
        if (categoryId != null) {
            validateCategoryExists(restaurantId, categoryId);
        }

        LambdaQueryWrapper<MenuItem> wrapper = new LambdaQueryWrapper<MenuItem>()
                .eq(MenuItem::getRestaurantId, restaurantId)
                .eq(categoryId != null, MenuItem::getCategoryId, categoryId)
                .like(StringUtils.hasText(keyword), MenuItem::getName, keyword)
                .orderByAsc(MenuItem::getSortOrder);

        IPage<MenuItem> itemPage = page(new Page<>(page, size), wrapper);

        return itemPage.convert(item -> {
            List<MenuItemVariant> variants = variantService.listByItemId(item.getId());
            List<ModifierGroup> groups = modifierGroupService.listByItemId(item.getId());
            List<ModifierOption> allOptions = new ArrayList<>();
            for (ModifierGroup group : groups) {
                allOptions.addAll(modifierOptionService.listByGroupId(group.getId()));
            }
            List<MenuItemAlias> aliases = aliasService.listByItemId(item.getId());
            return converter.toMenuItemVO(item, variants, groups, allOptions, aliases);
        });
    }

    @Override
    public MenuItemVO getMenuItemById(Long restaurantId, Long id) {
        MenuItem item = getById(id);
        if (item == null || !item.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Menu item not found");
        }

        List<MenuItemVariant> variants = variantService.listByItemId(id);
        List<ModifierGroup> groups = modifierGroupService.listByItemId(id);
        List<ModifierOption> allOptions = new ArrayList<>();
        for (ModifierGroup group : groups) {
            allOptions.addAll(modifierOptionService.listByGroupId(group.getId()));
        }
        List<MenuItemAlias> aliases = aliasService.listByItemId(id);

        return converter.toMenuItemVO(item, variants, groups, allOptions, aliases);
    }

    @Override
    @Transactional
    public MenuItemVO createMenuItem(Long restaurantId, MenuItemCreateRequest request) {
        validateRestaurantExists(restaurantId);
        validateCategoryExists(restaurantId, request.getCategoryId());

        // 验证价格
        if (request.getPrice() != null && request.getPrice().signum() < 0) {
            throw BusinessException.badRequest("Price cannot be negative");
        }

        MenuItem item = new MenuItem();
        BeanUtils.copyProperties(request, item);
        item.setRestaurantId(restaurantId);
        if (item.getAvailable() == null) item.setAvailable(true);
        save(item);

        // Create variants
        if (request.getVariants() != null) {
            for (MenuItemVariantRequest variantReq : request.getVariants()) {
                MenuItemVariant variant = new MenuItemVariant();
                BeanUtils.copyProperties(variantReq, variant);
                variant.setItemId(item.getId());
                variantService.save(variant);
            }
        }

        // Create modifier groups and options
        if (request.getModifierGroups() != null) {
            for (ModifierGroupRequest groupReq : request.getModifierGroups()) {
                ModifierGroup group = new ModifierGroup();
                BeanUtils.copyProperties(groupReq, group);
                group.setItemId(item.getId());
                modifierGroupService.save(group);

                if (groupReq.getOptions() != null) {
                    for (ModifierOptionRequest optionReq : groupReq.getOptions()) {
                        ModifierOption option = new ModifierOption();
                        BeanUtils.copyProperties(optionReq, option);
                        option.setGroupId(group.getId());
                        modifierOptionService.save(option);
                    }
                }
            }
        }

        // Create aliases
        if (request.getAliases() != null) {
            for (String aliasName : request.getAliases()) {
                MenuItemAlias alias = new MenuItemAlias();
                alias.setItemId(item.getId());
                alias.setAliasName(aliasName);
                aliasService.save(alias);
            }
        }

        log.info("Menu item created: id={}, name={}, restaurantId={}", item.getId(), item.getName(), restaurantId);

        return getMenuItemById(restaurantId, item.getId());
    }

    @Override
    @Transactional
    public MenuItemVO updateMenuItem(Long restaurantId, Long id, MenuItemUpdateRequest request) {
        validateRestaurantExists(restaurantId);
        MenuItem item = getById(id);
        if (item == null || !item.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Menu item not found");
        }

        // 验证新分类是否存在
        if (request.getCategoryId() != null) {
            validateCategoryExists(restaurantId, request.getCategoryId());
        }

        // 验证价格
        if (request.getPrice() != null && request.getPrice().signum() < 0) {
            throw BusinessException.badRequest("Price cannot be negative");
        }

        if (request.getCategoryId() != null) item.setCategoryId(request.getCategoryId());
        if (request.getName() != null) item.setName(request.getName());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getImage() != null) item.setImage(request.getImage());
        if (request.getAllergens() != null) item.setAllergens(request.getAllergens());
        if (request.getTags() != null) item.setTags(request.getTags());
        if (request.getAvailable() != null) item.setAvailable(request.getAvailable());
        if (request.getSortOrder() != null) item.setSortOrder(request.getSortOrder());
        if (request.getAvailableDays() != null) item.setAvailableDays(request.getAvailableDays());
        if (request.getAvailableStartTime() != null) item.setAvailableStartTime(request.getAvailableStartTime());
        if (request.getAvailableEndTime() != null) item.setAvailableEndTime(request.getAvailableEndTime());

        updateById(item);

        log.info("Menu item updated: id={}, name={}", id, item.getName());

        return getMenuItemById(restaurantId, id);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long restaurantId, Long id) {
        MenuItem item = getById(id);
        if (item == null || !item.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Menu item not found");
        }

        // Delete related data
        variantService.deleteByItemId(id);
        List<ModifierGroup> groups = modifierGroupService.listByItemId(id);
        for (ModifierGroup group : groups) {
            modifierOptionService.deleteByGroupId(group.getId());
        }
        modifierGroupService.deleteByItemId(id);
        aliasService.deleteByItemId(id);

        removeById(id);

        log.info("Menu item deleted: id={}, name={}", id, item.getName());
    }

    @Override
    @Transactional
    public void updateAvailability(Long restaurantId, Long id, Boolean available) {
        MenuItem item = getById(id);
        if (item == null || !item.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Menu item not found");
        }
        item.setAvailable(available);
        updateById(item);

        log.info("Menu item availability updated: id={}, available={}", id, available);
    }

    @Override
    @Transactional
    public void updateAliases(Long restaurantId, Long itemId, List<String> aliases) {
        MenuItem item = getById(itemId);
        if (item == null || !item.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Menu item not found");
        }

        aliasService.deleteByItemId(itemId);

        if (aliases != null) {
            for (String aliasName : aliases) {
                MenuItemAlias alias = new MenuItemAlias();
                alias.setItemId(itemId);
                alias.setAliasName(aliasName);
                aliasService.save(alias);
            }
        }
    }

    @Override
    public boolean isAvailable(Long restaurantId, Long itemId) {
        MenuItem item = getById(itemId);
        if (item == null || !item.getRestaurantId().equals(restaurantId)) {
            return false;
        }

        if (!item.getAvailable()) {
            return false;
        }

        // Check day availability
        if (item.getAvailableDays() != null && !item.getAvailableDays().isEmpty()) {
            String today = LocalDate.now().getDayOfWeek().name().substring(0, 3);
            if (!item.getAvailableDays().contains(today)) {
                return false;
            }
        }

        // Check time availability
        LocalTime now = LocalTime.now();
        if (item.getAvailableStartTime() != null && now.isBefore(item.getAvailableStartTime())) {
            return false;
        }
        if (item.getAvailableEndTime() != null && now.isAfter(item.getAvailableEndTime())) {
            return false;
        }

        return true;
    }
}
