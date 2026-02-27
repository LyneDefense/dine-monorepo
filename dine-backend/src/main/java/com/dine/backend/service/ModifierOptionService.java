package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.entity.ModifierOption;

import java.util.List;

public interface ModifierOptionService extends IService<ModifierOption> {

    List<ModifierOption> listByGroupId(Long groupId);

    void deleteByGroupId(Long groupId);
}
