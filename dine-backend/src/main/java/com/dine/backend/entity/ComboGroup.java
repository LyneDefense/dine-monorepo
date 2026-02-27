package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("combo_group")
public class ComboGroup extends BaseEntity {

    private Long comboId;

    private String name;

    private Integer pickCount;

    private Boolean required;

    private Integer sortOrder;
}
