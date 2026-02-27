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
@TableName(value = "combo", autoResultMap = true)
public class Combo extends BaseEntity {

    private Long restaurantId;

    private String name;

    private String description;

    private BigDecimal price;

    private String image;

    private Boolean available;

    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> availableDays;

    private LocalTime availableStartTime;

    private LocalTime availableEndTime;
}
