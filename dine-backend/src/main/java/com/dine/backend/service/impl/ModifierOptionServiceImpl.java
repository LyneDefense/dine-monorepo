package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.entity.ModifierOption;
import com.dine.backend.mapper.ModifierOptionMapper;
import com.dine.backend.service.ModifierOptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ModifierOptionServiceImpl extends ServiceImpl<ModifierOptionMapper, ModifierOption> implements ModifierOptionService {

    @Override
    public List<ModifierOption> listByGroupId(Long groupId) {
        return list(new LambdaQueryWrapper<ModifierOption>()
                .eq(ModifierOption::getGroupId, groupId)
                .orderByAsc(ModifierOption::getSortOrder));
    }

    @Override
    @Transactional
    public void deleteByGroupId(Long groupId) {
        remove(new LambdaQueryWrapper<ModifierOption>()
                .eq(ModifierOption::getGroupId, groupId));
    }
}
