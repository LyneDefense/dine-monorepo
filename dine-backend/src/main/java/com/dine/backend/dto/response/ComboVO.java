package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ComboVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    private String name;

    private String description;

    private BigDecimal price;

    private String image;

    private Boolean available;

    private Integer sortOrder;

    private List<ComboGroupVO> groups;
}
