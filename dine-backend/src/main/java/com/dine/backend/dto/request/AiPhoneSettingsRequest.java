package com.dine.backend.dto.request;

import com.dine.backend.entity.enums.EscalationTriggerEnum;
import lombok.Data;

import java.util.List;

@Data
public class AiPhoneSettingsRequest {

    private Boolean enabled;

    private String voiceType;

    private String greetingMessage;

    private String fallbackMessage;

    private List<EscalationTriggerEnum> escalationTriggers;

    private String escalationPhone;

    private Integer maxRetries;
}
