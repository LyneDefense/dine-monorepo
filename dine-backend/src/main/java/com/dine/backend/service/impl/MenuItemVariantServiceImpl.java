package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.MenuItemVariantRequest;
import com.dine.backend.dto.response.MenuItemVariantVO;
import com.dine.backend.entity.MenuItemVariant;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.MenuItemVariantMapper;
import com.dine.backend.service.MenuItemVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemVariantServiceImpl extends ServiceImpl<MenuItemVariantMapper, MenuItemVariant> implements MenuItemVariantService {

    private final EntityConverter converter;

    @Override
    public List<MenuItemVariant> listByItemId(Long itemId) {
        return list(new LambdaQueryWrapper<MenuItemVariant>()
                .eq(MenuItemVariant::getItemId, itemId)
                .orderByAsc(MenuItemVariant::getSortOrder));
    }

    @Override
    public List<MenuItemVariantVO> getVariants(Long itemId) {
        List<MenuItemVariant> variants = listByItemId(itemId);
        return converter.toMenuItemVariantVOList(variants);
    }

    @Override
    @Transactional
    public MenuItemVariantVO createVariant(Long itemId, MenuItemVariantRequest request) {
        MenuItemVariant variant = new MenuItemVariant();
        BeanUtils.copyProperties(request, variant);
        variant.setItemId(itemId);

        if (variant.getSortOrder() == null) {
            Long count = count(new LambdaQueryWrapper<MenuItemVariant>()
                    .eq(MenuItemVariant::getItemId, itemId));
            variant.setSortOrder(count.intValue());
        }

        save(variant);
        return converter.toMenuItemVariantVO(variant);
    }

    @Override
    @Transactional
    public MenuItemVariantVO updateVariant(Long itemId, Long variantId, MenuItemVariantRequest request) {
        MenuItemVariant variant = getById(variantId);
        if (variant == null || !variant.getItemId().equals(itemId)) {
            throw BusinessException.notFound("Variant not found");
        }

        if (request.getName() != null) variant.setName(request.getName());
        if (request.getPrice() != null) variant.setPrice(request.getPrice());
        if (request.getSortOrder() != null) variant.setSortOrder(request.getSortOrder());

        updateById(variant);
        return converter.toMenuItemVariantVO(variant);
    }

    @Override
    @Transactional
    public void deleteVariant(Long itemId, Long variantId) {
        MenuItemVariant variant = getById(variantId);
        if (variant == null || !variant.getItemId().equals(itemId)) {
            throw BusinessException.notFound("Variant not found");
        }
        removeById(variantId);
    }

    @Override
    @Transactional
    public void deleteByItemId(Long itemId) {
        remove(new LambdaQueryWrapper<MenuItemVariant>()
                .eq(MenuItemVariant::getItemId, itemId));
    }
}
