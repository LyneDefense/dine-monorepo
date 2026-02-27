package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.ModifierGroupRequest;
import com.dine.backend.dto.request.ModifierOptionRequest;
import com.dine.backend.dto.response.ModifierGroupVO;
import com.dine.backend.entity.ModifierGroup;
import com.dine.backend.entity.ModifierOption;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.ModifierGroupMapper;
import com.dine.backend.service.ModifierGroupService;
import com.dine.backend.service.ModifierOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModifierGroupServiceImpl extends ServiceImpl<ModifierGroupMapper, ModifierGroup> implements ModifierGroupService {

    private final EntityConverter converter;
    private final ModifierOptionService modifierOptionService;

    @Override
    public List<ModifierGroup> listByItemId(Long itemId) {
        return list(new LambdaQueryWrapper<ModifierGroup>()
                .eq(ModifierGroup::getItemId, itemId)
                .orderByAsc(ModifierGroup::getSortOrder));
    }

    @Override
    public List<ModifierGroupVO> getModifierGroups(Long itemId) {
        List<ModifierGroup> groups = listByItemId(itemId);
        return groups.stream().map(g -> {
            List<ModifierOption> options = modifierOptionService.listByGroupId(g.getId());
            return converter.toModifierGroupVO(g, options);
        }).toList();
    }

    @Override
    @Transactional
    public ModifierGroupVO createModifierGroup(Long itemId, ModifierGroupRequest request) {
        ModifierGroup group = new ModifierGroup();
        BeanUtils.copyProperties(request, group);
        group.setItemId(itemId);

        if (group.getSortOrder() == null) {
            Long count = count(new LambdaQueryWrapper<ModifierGroup>()
                    .eq(ModifierGroup::getItemId, itemId));
            group.setSortOrder(count.intValue());
        }

        save(group);

        // Create options
        if (request.getOptions() != null) {
            for (ModifierOptionRequest optionReq : request.getOptions()) {
                ModifierOption option = new ModifierOption();
                BeanUtils.copyProperties(optionReq, option);
                option.setGroupId(group.getId());
                modifierOptionService.save(option);
            }
        }

        List<ModifierOption> options = modifierOptionService.listByGroupId(group.getId());
        return converter.toModifierGroupVO(group, options);
    }

    @Override
    @Transactional
    public ModifierGroupVO updateModifierGroup(Long itemId, Long groupId, ModifierGroupRequest request) {
        ModifierGroup group = getById(groupId);
        if (group == null || !group.getItemId().equals(itemId)) {
            throw BusinessException.notFound("Modifier group not found");
        }

        if (request.getName() != null) group.setName(request.getName());
        if (request.getSelectionType() != null) group.setSelectionType(request.getSelectionType());
        if (request.getRequired() != null) group.setRequired(request.getRequired());
        if (request.getMaxSelections() != null) group.setMaxSelections(request.getMaxSelections());
        if (request.getSortOrder() != null) group.setSortOrder(request.getSortOrder());

        updateById(group);

        // Update options if provided
        if (request.getOptions() != null) {
            modifierOptionService.deleteByGroupId(groupId);
            for (ModifierOptionRequest optionReq : request.getOptions()) {
                ModifierOption option = new ModifierOption();
                BeanUtils.copyProperties(optionReq, option);
                option.setGroupId(groupId);
                modifierOptionService.save(option);
            }
        }

        List<ModifierOption> options = modifierOptionService.listByGroupId(groupId);
        return converter.toModifierGroupVO(group, options);
    }

    @Override
    @Transactional
    public void deleteModifierGroup(Long itemId, Long groupId) {
        ModifierGroup group = getById(groupId);
        if (group == null || !group.getItemId().equals(itemId)) {
            throw BusinessException.notFound("Modifier group not found");
        }
        modifierOptionService.deleteByGroupId(groupId);
        removeById(groupId);
    }

    @Override
    @Transactional
    public void deleteByItemId(Long itemId) {
        List<ModifierGroup> groups = listByItemId(itemId);
        for (ModifierGroup group : groups) {
            modifierOptionService.deleteByGroupId(group.getId());
        }
        remove(new LambdaQueryWrapper<ModifierGroup>()
                .eq(ModifierGroup::getItemId, itemId));
    }
}
