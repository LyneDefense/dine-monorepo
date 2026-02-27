package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

import java.util.List;

@Data
public class ComboGroupVO {

    @LongToString
    private Long id;

    @LongToString
    private Long comboId;

    private String name;

    private Integer selectionCount;

    private Integer sortOrder;

    private List<ComboGroupItemVO> items;
}
