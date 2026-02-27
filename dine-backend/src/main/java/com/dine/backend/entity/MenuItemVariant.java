package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("menu_item_variant")
public class MenuItemVariant extends BaseEntity {

    private Long itemId;

    private String name;

    private BigDecimal price;

    private Integer sortOrder;
}
