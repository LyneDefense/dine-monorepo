package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.entity.MenuItemAlias;

import java.util.List;

public interface MenuItemAliasService extends IService<MenuItemAlias> {

    List<MenuItemAlias> listByItemId(Long itemId);

    List<MenuItemAlias> searchByAlias(Long restaurantId, String keyword);

    void deleteByItemId(Long itemId);
}
