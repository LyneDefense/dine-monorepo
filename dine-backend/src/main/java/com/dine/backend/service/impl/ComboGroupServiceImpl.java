package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.entity.ComboGroup;
import com.dine.backend.mapper.ComboGroupMapper;
import com.dine.backend.service.ComboGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComboGroupServiceImpl extends ServiceImpl<ComboGroupMapper, ComboGroup> implements ComboGroupService {

    @Override
    public List<ComboGroup> listByComboId(Long comboId) {
        return list(new LambdaQueryWrapper<ComboGroup>()
                .eq(ComboGroup::getComboId, comboId)
                .orderByAsc(ComboGroup::getSortOrder));
    }

    @Override
    @Transactional
    public void deleteByComboId(Long comboId) {
        remove(new LambdaQueryWrapper<ComboGroup>()
                .eq(ComboGroup::getComboId, comboId));
    }
}
