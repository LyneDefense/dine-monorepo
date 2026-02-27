package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.entity.ComboGroupItem;
import com.dine.backend.mapper.ComboGroupItemMapper;
import com.dine.backend.service.ComboGroupItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ComboGroupItemServiceImpl extends ServiceImpl<ComboGroupItemMapper, ComboGroupItem> implements ComboGroupItemService {

    @Override
    public List<ComboGroupItem> listByGroupId(Long groupId) {
        return list(new LambdaQueryWrapper<ComboGroupItem>()
                .eq(ComboGroupItem::getGroupId, groupId));
    }

    @Override
    public List<ComboGroupItem> listByGroupIds(List<Long> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            return new ArrayList<>();
        }
        return list(new LambdaQueryWrapper<ComboGroupItem>()
                .in(ComboGroupItem::getGroupId, groupIds));
    }

    @Override
    @Transactional
    public void deleteByGroupId(Long groupId) {
        remove(new LambdaQueryWrapper<ComboGroupItem>()
                .eq(ComboGroupItem::getGroupId, groupId));
    }

    @Override
    @Transactional
    public void deleteByGroupIds(List<Long> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            return;
        }
        remove(new LambdaQueryWrapper<ComboGroupItem>()
                .in(ComboGroupItem::getGroupId, groupIds));
    }
}
