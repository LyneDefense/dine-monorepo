package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

@Data
public class MenuCategoryVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    private String name;

    private String description;

    private Integer sortOrder;
}
