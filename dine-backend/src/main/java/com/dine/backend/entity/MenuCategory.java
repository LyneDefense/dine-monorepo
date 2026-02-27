package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("menu_category")
public class MenuCategory extends BaseEntity {

    private Long restaurantId;

    private String name;

    private String description;

    private Integer sortOrder;
}
