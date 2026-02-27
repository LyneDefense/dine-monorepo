package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.AiPhoneActiveHoursRequest;
import com.dine.backend.dto.request.AiPhoneFaqRequest;
import com.dine.backend.dto.request.AiPhoneInstructionRequest;
import com.dine.backend.dto.request.AiPhoneSettingsRequest;
import com.dine.backend.dto.response.AiPhoneActiveHoursVO;
import com.dine.backend.dto.response.AiPhoneFaqVO;
import com.dine.backend.dto.response.AiPhoneInstructionVO;
import com.dine.backend.dto.response.AiPhoneSettingsVO;
import com.dine.backend.entity.AiPhoneActiveHours;
import com.dine.backend.entity.AiPhoneFaq;
import com.dine.backend.entity.AiPhoneInstruction;
import com.dine.backend.entity.AiPhoneSettings;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.AiPhoneSettingsMapper;
import com.dine.backend.service.AiPhoneActiveHoursService;
import com.dine.backend.service.AiPhoneFaqService;
import com.dine.backend.service.AiPhoneInstructionService;
import com.dine.backend.service.AiPhoneSettingsService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiPhoneSettingsServiceImpl extends ServiceImpl<AiPhoneSettingsMapper, AiPhoneSettings> implements AiPhoneSettingsService {

    private final EntityConverter converter;
    private final AiPhoneActiveHoursService activeHoursService;
    private final AiPhoneFaqService faqService;
    private final AiPhoneInstructionService instructionService;

    @Override
    public AiPhoneSettings getByRestaurantId(Long restaurantId) {
        return getOne(new LambdaQueryWrapper<AiPhoneSettings>()
                .eq(AiPhoneSettings::getRestaurantId, restaurantId));
    }

    @Override
    public AiPhoneSettingsVO getSettings(Long restaurantId) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        return buildSettingsVO(settings);
    }

    private AiPhoneSettings getOrCreateSettings(Long restaurantId) {
        AiPhoneSettings settings = getByRestaurantId(restaurantId);
        if (settings == null) {
            settings = new AiPhoneSettings();
            settings.setRestaurantId(restaurantId);
            settings.setLanguage("en-US");
            save(settings);
        }
        return settings;
    }

    private AiPhoneSettingsVO buildSettingsVO(AiPhoneSettings settings) {
        List<AiPhoneActiveHours> activeHours = activeHoursService.listBySettingsId(settings.getId());
        List<AiPhoneFaq> faqs = faqService.listBySettingsId(settings.getId());
        List<AiPhoneInstruction> instructions = instructionService.listBySettingsId(settings.getId());
        return converter.toAiPhoneSettingsVO(settings, activeHours, faqs, instructions);
    }

    @Override
    @Transactional
    public AiPhoneSettingsVO updateSettings(Long restaurantId, AiPhoneSettingsRequest request) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        if (request.getGreetingMessage() != null) {
            settings.setGreetingMessage(request.getGreetingMessage());
        }
        if (request.getEscalationPhone() != null) {
            settings.setEscalationPhone(request.getEscalationPhone());
        }
        if (request.getEscalationTriggers() != null) {
            settings.setEscalationTriggers(
                    request.getEscalationTriggers().stream()
                            .map(Enum::name)
                            .collect(Collectors.toList())
            );
        }
        updateById(settings);
        return buildSettingsVO(settings);
    }

    // Active Hours
    @Override
    public List<AiPhoneActiveHoursVO> getActiveHours(Long restaurantId) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        List<AiPhoneActiveHours> activeHours = activeHoursService.listBySettingsId(settings.getId());
        return converter.toAiPhoneActiveHoursVOList(activeHours);
    }

    @Override
    @Transactional
    public AiPhoneActiveHoursVO addActiveHours(Long restaurantId, AiPhoneActiveHoursRequest request) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        AiPhoneActiveHours activeHours = new AiPhoneActiveHours();
        activeHours.setSettingsId(settings.getId());
        activeHours.setDayOfWeek(request.getDayOfWeek());
        activeHours.setStartTime(request.getStartTime());
        activeHours.setEndTime(request.getEndTime());
        activeHoursService.save(activeHours);
        return converter.toAiPhoneActiveHoursVO(activeHours);
    }

    @Override
    @Transactional
    public void deleteActiveHours(Long restaurantId, Long id) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        AiPhoneActiveHours activeHours = activeHoursService.getById(id);
        if (activeHours == null || !activeHours.getSettingsId().equals(settings.getId())) {
            throw BusinessException.notFound("Active hours not found");
        }
        activeHoursService.removeById(id);
    }

    // FAQs
    @Override
    public List<AiPhoneFaqVO> getFaqs(Long restaurantId) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        List<AiPhoneFaq> faqs = faqService.listBySettingsId(settings.getId());
        return converter.toAiPhoneFaqVOList(faqs);
    }

    @Override
    @Transactional
    public AiPhoneFaqVO addFaq(Long restaurantId, AiPhoneFaqRequest request) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        AiPhoneFaq faq = new AiPhoneFaq();
        faq.setSettingsId(settings.getId());
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        faq.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        faqService.save(faq);
        return converter.toAiPhoneFaqVO(faq);
    }

    @Override
    @Transactional
    public AiPhoneFaqVO updateFaq(Long restaurantId, Long id, AiPhoneFaqRequest request) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        AiPhoneFaq faq = faqService.getById(id);
        if (faq == null || !faq.getSettingsId().equals(settings.getId())) {
            throw BusinessException.notFound("FAQ not found");
        }
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        if (request.getSortOrder() != null) {
            faq.setSortOrder(request.getSortOrder());
        }
        faqService.updateById(faq);
        return converter.toAiPhoneFaqVO(faq);
    }

    @Override
    @Transactional
    public void deleteFaq(Long restaurantId, Long id) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        AiPhoneFaq faq = faqService.getById(id);
        if (faq == null || !faq.getSettingsId().equals(settings.getId())) {
            throw BusinessException.notFound("FAQ not found");
        }
        faqService.removeById(id);
    }

    // Instructions
    @Override
    public List<AiPhoneInstructionVO> getInstructions(Long restaurantId) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        List<AiPhoneInstruction> instructions = instructionService.listBySettingsId(settings.getId());
        return converter.toAiPhoneInstructionVOList(instructions);
    }

    @Override
    @Transactional
    public AiPhoneInstructionVO addInstruction(Long restaurantId, AiPhoneInstructionRequest request) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        AiPhoneInstruction instruction = new AiPhoneInstruction();
        instruction.setSettingsId(settings.getId());
        instruction.setInstruction(request.getInstruction());
        instruction.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        instructionService.save(instruction);
        return converter.toAiPhoneInstructionVO(instruction);
    }

    @Override
    @Transactional
    public AiPhoneInstructionVO updateInstruction(Long restaurantId, Long id, AiPhoneInstructionRequest request) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        AiPhoneInstruction instruction = instructionService.getById(id);
        if (instruction == null || !instruction.getSettingsId().equals(settings.getId())) {
            throw BusinessException.notFound("Instruction not found");
        }
        instruction.setInstruction(request.getInstruction());
        if (request.getSortOrder() != null) {
            instruction.setSortOrder(request.getSortOrder());
        }
        instructionService.updateById(instruction);
        return converter.toAiPhoneInstructionVO(instruction);
    }

    @Override
    @Transactional
    public void deleteInstruction(Long restaurantId, Long id) {
        AiPhoneSettings settings = getOrCreateSettings(restaurantId);
        AiPhoneInstruction instruction = instructionService.getById(id);
        if (instruction == null || !instruction.getSettingsId().equals(settings.getId())) {
            throw BusinessException.notFound("Instruction not found");
        }
        instructionService.removeById(id);
    }
}
