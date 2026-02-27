package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComboGroupItemVO {

    @LongToString
    private Long id;

    @LongToString
    private Long groupId;

    @LongToString
    private Long menuItemId;

    private String menuItemName;

    private BigDecimal priceAdjustment;

    private Integer sortOrder;
}
