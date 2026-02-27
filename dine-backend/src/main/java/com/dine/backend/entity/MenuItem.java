package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.config.handler.StringListTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "menu_item", autoResultMap = true)
public class MenuItem extends BaseEntity {

    private Long restaurantId;

    private Long categoryId;

    private String name;

    private String description;

    private BigDecimal price;

    private String image;

    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> allergens;

    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> tags;

    private Boolean available;

    private Integer sortOrder;

    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> availableDays;

    private LocalTime availableStartTime;

    private LocalTime availableEndTime;
}
