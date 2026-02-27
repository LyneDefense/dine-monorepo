package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

import java.util.List;

@Data
public class DiningSectionVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    private String name;

    private String description;

    private Integer sortOrder;

    private List<DiningTableVO> tables;
}
