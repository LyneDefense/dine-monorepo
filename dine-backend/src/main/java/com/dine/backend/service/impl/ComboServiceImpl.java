package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.ComboGroupItemRequest;
import com.dine.backend.dto.request.ComboGroupRequest;
import com.dine.backend.dto.request.ComboRequest;
import com.dine.backend.dto.response.ComboVO;
import com.dine.backend.entity.Combo;
import com.dine.backend.entity.ComboGroup;
import com.dine.backend.entity.ComboGroupItem;
import com.dine.backend.entity.MenuItem;
import com.dine.backend.entity.Restaurant;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.ComboMapper;
import com.dine.backend.service.ComboGroupItemService;
import com.dine.backend.service.ComboGroupService;
import com.dine.backend.service.ComboService;
import com.dine.backend.service.MenuItemService;
import com.dine.backend.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComboServiceImpl extends ServiceImpl<ComboMapper, Combo> implements ComboService {

    private final EntityConverter converter;
    private final ComboGroupService comboGroupService;
    private final ComboGroupItemService comboGroupItemService;
    private final MenuItemService menuItemService;
    private final RestaurantService restaurantService;

    private void validateRestaurantExists(Long restaurantId) {
        Restaurant restaurant = restaurantService.getById(restaurantId);
        if (restaurant == null) {
            throw BusinessException.notFound("Restaurant not found: " + restaurantId);
        }
    }

    private void validateMenuItemExists(Long restaurantId, Long itemId) {
        MenuItem item = menuItemService.getById(itemId);
        if (item == null) {
            throw BusinessException.notFound("Menu item not found: " + itemId);
        }
        if (!item.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.badRequest("Menu item does not belong to this restaurant");
        }
    }

    private void validateComboRequest(Long restaurantId, ComboRequest request) {
        if (request.getPrice() != null && request.getPrice().signum() < 0) {
            throw BusinessException.badRequest("Combo price cannot be negative");
        }

        if (request.getGroups() != null) {
            for (ComboGroupRequest groupReq : request.getGroups()) {
                if (groupReq.getItems() != null) {
                    for (ComboGroupItemRequest itemReq : groupReq.getItems()) {
                        validateMenuItemExists(restaurantId, itemReq.getMenuItemId());
                    }
                }
            }
        }
    }

    @Override
    public List<Combo> listByRestaurantId(Long restaurantId) {
        return list(new LambdaQueryWrapper<Combo>()
                .eq(Combo::getRestaurantId, restaurantId));
    }

    @Override
    public List<ComboVO> getCombos(Long restaurantId) {
        validateRestaurantExists(restaurantId);
        List<Combo> combos = listByRestaurantId(restaurantId);
        return combos.stream()
                .map(this::buildComboVO)
                .collect(Collectors.toList());
    }

    @Override
    public ComboVO getComboById(Long restaurantId, Long id) {
        validateRestaurantExists(restaurantId);
        Combo combo = getById(id);
        if (combo == null || !combo.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Combo not found");
        }
        return buildComboVO(combo);
    }

    private ComboVO buildComboVO(Combo combo) {
        List<ComboGroup> groups = comboGroupService.listByComboId(combo.getId());
        List<Long> groupIds = groups.stream().map(ComboGroup::getId).collect(Collectors.toList());
        List<ComboGroupItem> allItems = comboGroupItemService.listByGroupIds(groupIds);

        List<Long> itemIds = allItems.stream().map(ComboGroupItem::getItemId).distinct().collect(Collectors.toList());
        List<MenuItem> menuItems = itemIds.isEmpty() ? new ArrayList<>() :
                menuItemService.listByIds(itemIds);

        return converter.toComboVO(combo, groups, allItems, menuItems);
    }

    @Override
    @Transactional
    public ComboVO createCombo(Long restaurantId, ComboRequest request) {
        validateRestaurantExists(restaurantId);
        validateComboRequest(restaurantId, request);

        Combo combo = new Combo();
        combo.setRestaurantId(restaurantId);
        BeanUtils.copyProperties(request, combo, "groups");
        if (combo.getAvailable() == null) {
            combo.setAvailable(true);
        }
        save(combo);

        if (request.getGroups() != null) {
            createGroupsAndItems(restaurantId, combo.getId(), request.getGroups());
        }

        log.info("Combo created: id={}, name={}, restaurantId={}", combo.getId(), combo.getName(), restaurantId);

        return getComboById(restaurantId, combo.getId());
    }

    @Override
    @Transactional
    public ComboVO updateCombo(Long restaurantId, Long id, ComboRequest request) {
        validateRestaurantExists(restaurantId);
        Combo combo = getById(id);
        if (combo == null || !combo.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Combo not found");
        }

        validateComboRequest(restaurantId, request);

        BeanUtils.copyProperties(request, combo, "groups", "id", "restaurantId");
        updateById(combo);

        // Delete existing groups and items
        List<ComboGroup> existingGroups = comboGroupService.listByComboId(id);
        List<Long> groupIds = existingGroups.stream().map(ComboGroup::getId).collect(Collectors.toList());
        if (!groupIds.isEmpty()) {
            comboGroupItemService.deleteByGroupIds(groupIds);
        }
        comboGroupService.deleteByComboId(id);

        // Recreate groups and items
        if (request.getGroups() != null) {
            createGroupsAndItems(restaurantId, id, request.getGroups());
        }

        log.info("Combo updated: id={}, name={}", id, combo.getName());

        return getComboById(restaurantId, id);
    }

    private void createGroupsAndItems(Long restaurantId, Long comboId, List<ComboGroupRequest> groupRequests) {
        int sortOrder = 0;
        for (ComboGroupRequest groupReq : groupRequests) {
            ComboGroup group = new ComboGroup();
            group.setComboId(comboId);
            group.setName(groupReq.getName());
            group.setPickCount(groupReq.getSelectionCount());
            group.setRequired(true);
            group.setSortOrder(groupReq.getSortOrder() != null ? groupReq.getSortOrder() : sortOrder++);
            comboGroupService.save(group);

            if (groupReq.getItems() != null) {
                for (ComboGroupItemRequest itemReq : groupReq.getItems()) {
                    ComboGroupItem item = new ComboGroupItem();
                    item.setGroupId(group.getId());
                    item.setItemId(itemReq.getMenuItemId());
                    comboGroupItemService.save(item);
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteCombo(Long restaurantId, Long id) {
        validateRestaurantExists(restaurantId);
        Combo combo = getById(id);
        if (combo == null || !combo.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Combo not found");
        }

        List<ComboGroup> groups = comboGroupService.listByComboId(id);
        List<Long> groupIds = groups.stream().map(ComboGroup::getId).collect(Collectors.toList());
        if (!groupIds.isEmpty()) {
            comboGroupItemService.deleteByGroupIds(groupIds);
        }
        comboGroupService.deleteByComboId(id);

        removeById(id);

        log.info("Combo deleted: id={}, name={}", id, combo.getName());
    }

    @Override
    @Transactional
    public void updateAvailability(Long restaurantId, Long id, Boolean available) {
        validateRestaurantExists(restaurantId);
        Combo combo = getById(id);
        if (combo == null || !combo.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Combo not found");
        }
        combo.setAvailable(available);
        updateById(combo);

        log.info("Combo availability updated: id={}, available={}", id, available);
    }

    @Override
    public boolean isAvailable(Long restaurantId, Long comboId) {
        Combo combo = getById(comboId);
        if (combo == null || !combo.getRestaurantId().equals(restaurantId)) {
            return false;
        }

        if (!Boolean.TRUE.equals(combo.getAvailable())) {
            return false;
        }

        if (combo.getAvailableDays() != null && !combo.getAvailableDays().isEmpty()) {
            DayOfWeek today = LocalDateTime.now().getDayOfWeek();
            String dayName = today.name();
            if (!combo.getAvailableDays().contains(dayName)) {
                return false;
            }
        }

        if (combo.getAvailableStartTime() != null && combo.getAvailableEndTime() != null) {
            LocalTime now = LocalTime.now();
            if (now.isBefore(combo.getAvailableStartTime()) || now.isAfter(combo.getAvailableEndTime())) {
                return false;
            }
        }

        return true;
    }
}
