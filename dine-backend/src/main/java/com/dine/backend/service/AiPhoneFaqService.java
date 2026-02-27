package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.entity.AiPhoneFaq;

import java.util.List;

public interface AiPhoneFaqService extends IService<AiPhoneFaq> {

    List<AiPhoneFaq> listBySettingsId(Long settingsId);

    void deleteBySettingsId(Long settingsId);
}
