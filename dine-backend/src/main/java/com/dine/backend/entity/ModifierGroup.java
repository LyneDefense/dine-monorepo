package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.entity.enums.SelectionTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("modifier_group")
public class ModifierGroup extends BaseEntity {

    private Long itemId;

    private String name;

    private SelectionTypeEnum selectionType;

    private Boolean required;

    private Integer maxSelections;

    private Integer sortOrder;
}
