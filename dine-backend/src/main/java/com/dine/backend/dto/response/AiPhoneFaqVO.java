package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

@Data
public class AiPhoneFaqVO {

    @LongToString
    private Long id;

    @LongToString
    private Long settingsId;

    private String question;

    private String answer;

    private Integer sortOrder;
}
