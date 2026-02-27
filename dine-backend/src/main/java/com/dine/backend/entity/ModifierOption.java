package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("modifier_option")
public class ModifierOption extends BaseEntity {

    private Long groupId;

    private String name;

    private BigDecimal extraPrice;

    private Boolean isDefault;

    private Integer sortOrder;
}
