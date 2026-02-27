package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.SelectionTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class ModifierGroupVO {

    @LongToString
    private Long id;

    @LongToString
    private Long itemId;

    private String name;

    private SelectionTypeEnum selectionType;

    private Boolean required;

    private Integer maxSelections;

    private Integer sortOrder;

    private List<ModifierOptionVO> options;
}
