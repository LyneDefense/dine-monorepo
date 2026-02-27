package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.MenuItemVariantRequest;
import com.dine.backend.dto.response.MenuItemVariantVO;
import com.dine.backend.entity.MenuItemVariant;

import java.util.List;

public interface MenuItemVariantService extends IService<MenuItemVariant> {

    List<MenuItemVariant> listByItemId(Long itemId);

    List<MenuItemVariantVO> getVariants(Long itemId);

    MenuItemVariantVO createVariant(Long itemId, MenuItemVariantRequest request);

    MenuItemVariantVO updateVariant(Long itemId, Long variantId, MenuItemVariantRequest request);

    void deleteVariant(Long itemId, Long variantId);

    void deleteByItemId(Long itemId);
}
