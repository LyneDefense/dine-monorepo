package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.entity.AiPhoneActiveHours;
import com.dine.backend.mapper.AiPhoneActiveHoursMapper;
import com.dine.backend.service.AiPhoneActiveHoursService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AiPhoneActiveHoursServiceImpl extends ServiceImpl<AiPhoneActiveHoursMapper, AiPhoneActiveHours> implements AiPhoneActiveHoursService {

    @Override
    public List<AiPhoneActiveHours> listBySettingsId(Long settingsId) {
        return list(new LambdaQueryWrapper<AiPhoneActiveHours>()
                .eq(AiPhoneActiveHours::getSettingsId, settingsId)
                .orderByAsc(AiPhoneActiveHours::getDayOfWeek));
    }

    @Override
    @Transactional
    public void deleteBySettingsId(Long settingsId) {
        remove(new LambdaQueryWrapper<AiPhoneActiveHours>()
                .eq(AiPhoneActiveHours::getSettingsId, settingsId));
    }
}
