package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.entity.AiPhoneInstruction;

import java.util.List;

public interface AiPhoneInstructionService extends IService<AiPhoneInstruction> {

    List<AiPhoneInstruction> listBySettingsId(Long settingsId);

    void deleteBySettingsId(Long settingsId);
}
