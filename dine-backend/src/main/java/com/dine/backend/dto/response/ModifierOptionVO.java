package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ModifierOptionVO {

    @LongToString
    private Long id;

    @LongToString
    private Long groupId;

    private String name;

    private BigDecimal extraPrice;

    private Boolean isDefault;

    private Integer sortOrder;
}
