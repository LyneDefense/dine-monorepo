package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.entity.ComboGroupItem;

import java.util.List;

public interface ComboGroupItemService extends IService<ComboGroupItem> {

    List<ComboGroupItem> listByGroupId(Long groupId);

    List<ComboGroupItem> listByGroupIds(List<Long> groupIds);

    void deleteByGroupId(Long groupId);

    void deleteByGroupIds(List<Long> groupIds);
}
