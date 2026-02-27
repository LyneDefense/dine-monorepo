package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.entity.AiPhoneFaq;
import com.dine.backend.mapper.AiPhoneFaqMapper;
import com.dine.backend.service.AiPhoneFaqService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AiPhoneFaqServiceImpl extends ServiceImpl<AiPhoneFaqMapper, AiPhoneFaq> implements AiPhoneFaqService {

    @Override
    public List<AiPhoneFaq> listBySettingsId(Long settingsId) {
        return list(new LambdaQueryWrapper<AiPhoneFaq>()
                .eq(AiPhoneFaq::getSettingsId, settingsId)
                .orderByAsc(AiPhoneFaq::getSortOrder));
    }

    @Override
    @Transactional
    public void deleteBySettingsId(Long settingsId) {
        remove(new LambdaQueryWrapper<AiPhoneFaq>()
                .eq(AiPhoneFaq::getSettingsId, settingsId));
    }
}
