package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.TableStatusEnum;
import lombok.Data;

@Data
public class DiningTableVO {

    @LongToString
    private Long id;

    @LongToString
    private Long sectionId;

    private String name;

    private Integer capacity;

    private TableStatusEnum status;

    private Integer sortOrder;
}
