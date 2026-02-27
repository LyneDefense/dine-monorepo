package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.entity.AiPhoneInstruction;
import com.dine.backend.mapper.AiPhoneInstructionMapper;
import com.dine.backend.service.AiPhoneInstructionService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiPhoneInstructionServiceImpl
    extends ServiceImpl<AiPhoneInstructionMapper, AiPhoneInstruction>
    implements AiPhoneInstructionService {

  @Override
  public List<AiPhoneInstruction> listBySettingsId(Long settingsId) {
    return list(
        new LambdaQueryWrapper<AiPhoneInstruction>()
            .eq(AiPhoneInstruction::getSettingsId, settingsId)
            .orderByAsc(AiPhoneInstruction::getSortOrder));
  }

  @Override
  @Transactional
  public void deleteBySettingsId(Long settingsId) {
    remove(
        new LambdaQueryWrapper<AiPhoneInstruction>()
            .eq(AiPhoneInstruction::getSettingsId, settingsId));
  }
}
