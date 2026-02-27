package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.EscalationTriggerEnum;
import lombok.Data;

import java.util.List;

@Data
public class AiPhoneSettingsVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    private Boolean enabled;

    private String voiceType;

    private String greetingMessage;

    private String fallbackMessage;

    private List<EscalationTriggerEnum> escalationTriggers;

    private String escalationPhone;

    private Integer maxRetries;

    private List<AiPhoneActiveHoursVO> activeHours;

    private List<AiPhoneFaqVO> faqs;

    private List<AiPhoneInstructionVO> instructions;
}
