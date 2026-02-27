package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.entity.MenuItem;
import com.dine.backend.entity.MenuItemAlias;
import com.dine.backend.mapper.MenuItemAliasMapper;
import com.dine.backend.mapper.MenuItemMapper;
import com.dine.backend.service.MenuItemAliasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemAliasServiceImpl extends ServiceImpl<MenuItemAliasMapper, MenuItemAlias> implements MenuItemAliasService {

    private final MenuItemMapper menuItemMapper;

    @Override
    public List<MenuItemAlias> listByItemId(Long itemId) {
        return list(new LambdaQueryWrapper<MenuItemAlias>()
                .eq(MenuItemAlias::getItemId, itemId));
    }

    @Override
    public List<MenuItemAlias> searchByAlias(Long restaurantId, String keyword) {
        // First get all item IDs for this restaurant
        List<MenuItem> items = menuItemMapper.selectList(new LambdaQueryWrapper<MenuItem>()
                .eq(MenuItem::getRestaurantId, restaurantId)
                .select(MenuItem::getId));

        if (items.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> itemIds = items.stream().map(MenuItem::getId).collect(Collectors.toList());

        // Search aliases
        return list(new LambdaQueryWrapper<MenuItemAlias>()
                .in(MenuItemAlias::getItemId, itemIds)
                .like(MenuItemAlias::getAliasName, keyword));
    }

    @Override
    @Transactional
    public void deleteByItemId(Long itemId) {
        remove(new LambdaQueryWrapper<MenuItemAlias>()
                .eq(MenuItemAlias::getItemId, itemId));
    }
}
