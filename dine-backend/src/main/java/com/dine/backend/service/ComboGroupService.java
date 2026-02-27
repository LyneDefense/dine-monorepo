package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.entity.ComboGroup;

import java.util.List;

public interface ComboGroupService extends IService<ComboGroup> {

    List<ComboGroup> listByComboId(Long comboId);

    void deleteByComboId(Long comboId);
}
