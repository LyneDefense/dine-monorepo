package com.dine.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.MenuItemCreateRequest;
import com.dine.backend.dto.request.MenuItemUpdateRequest;
import com.dine.backend.dto.response.MenuItemVO;
import com.dine.backend.entity.MenuItem;

import java.util.List;

public interface MenuItemService extends IService<MenuItem> {

    List<MenuItem> listByRestaurantId(Long restaurantId);

    List<MenuItem> listByCategoryId(Long categoryId);

    List<MenuItem> searchByKeyword(Long restaurantId, String keyword);

    IPage<MenuItemVO> getMenuItems(Long restaurantId, Long categoryId, String keyword, Integer page, Integer size);

    MenuItemVO getMenuItemById(Long restaurantId, Long id);

    MenuItemVO createMenuItem(Long restaurantId, MenuItemCreateRequest request);

    MenuItemVO updateMenuItem(Long restaurantId, Long id, MenuItemUpdateRequest request);

    void deleteMenuItem(Long restaurantId, Long id);

    void updateAvailability(Long restaurantId, Long id, Boolean available);

    void updateAliases(Long restaurantId, Long itemId, List<String> aliases);

    boolean isAvailable(Long restaurantId, Long itemId);
}
