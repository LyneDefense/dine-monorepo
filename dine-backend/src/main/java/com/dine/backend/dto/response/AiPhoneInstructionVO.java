package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

@Data
public class AiPhoneInstructionVO {

    @LongToString
    private Long id;

    @LongToString
    private Long settingsId;

    private String instruction;

    private Integer sortOrder;
}
