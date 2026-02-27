package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemVariantVO {

    @LongToString
    private Long id;

    @LongToString
    private Long itemId;

    private String name;

    private BigDecimal price;

    private Integer sortOrder;
}
