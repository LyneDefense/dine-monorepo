package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.AiPhoneActiveHoursRequest;
import com.dine.backend.dto.request.AiPhoneFaqRequest;
import com.dine.backend.dto.request.AiPhoneInstructionRequest;
import com.dine.backend.dto.request.AiPhoneSettingsRequest;
import com.dine.backend.dto.response.AiPhoneActiveHoursVO;
import com.dine.backend.dto.response.AiPhoneFaqVO;
import com.dine.backend.dto.response.AiPhoneInstructionVO;
import com.dine.backend.dto.response.AiPhoneSettingsVO;
import com.dine.backend.entity.AiPhoneSettings;
import java.util.List;

public interface AiPhoneSettingsService extends IService<AiPhoneSettings> {

    AiPhoneSettings getByRestaurantId(Long restaurantId);

    AiPhoneSettingsVO getSettings(Long restaurantId);

    AiPhoneSettingsVO updateSettings(Long restaurantId, AiPhoneSettingsRequest request);

    // Active Hours
    List<AiPhoneActiveHoursVO> getActiveHours(Long restaurantId);

    AiPhoneActiveHoursVO addActiveHours(Long restaurantId, AiPhoneActiveHoursRequest request);

    void deleteActiveHours(Long restaurantId, Long id);

    // FAQs
    List<AiPhoneFaqVO> getFaqs(Long restaurantId);

    AiPhoneFaqVO addFaq(Long restaurantId, AiPhoneFaqRequest request);

    AiPhoneFaqVO updateFaq(Long restaurantId, Long id, AiPhoneFaqRequest request);

    void deleteFaq(Long restaurantId, Long id);

    // Instructions
    List<AiPhoneInstructionVO> getInstructions(Long restaurantId);

    AiPhoneInstructionVO addInstruction(Long restaurantId, AiPhoneInstructionRequest request);

    AiPhoneInstructionVO updateInstruction(Long restaurantId, Long id, AiPhoneInstructionRequest request);

    void deleteInstruction(Long restaurantId, Long id);
}
