package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.entity.AiPhoneActiveHours;

import java.util.List;

public interface AiPhoneActiveHoursService extends IService<AiPhoneActiveHours> {

    List<AiPhoneActiveHours> listBySettingsId(Long settingsId);

    void deleteBySettingsId(Long settingsId);
}
