package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.ModifierGroupRequest;
import com.dine.backend.dto.response.ModifierGroupVO;
import com.dine.backend.entity.ModifierGroup;

import java.util.List;

public interface ModifierGroupService extends IService<ModifierGroup> {

    List<ModifierGroup> listByItemId(Long itemId);

    List<ModifierGroupVO> getModifierGroups(Long itemId);

    ModifierGroupVO createModifierGroup(Long itemId, ModifierGroupRequest request);

    ModifierGroupVO updateModifierGroup(Long itemId, Long groupId, ModifierGroupRequest request);

    void deleteModifierGroup(Long itemId, Long groupId);

    void deleteByItemId(Long itemId);
}
