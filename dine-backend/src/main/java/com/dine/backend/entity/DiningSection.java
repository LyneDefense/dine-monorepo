package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dining_section")
public class DiningSection extends BaseEntity {

    private Long restaurantId;

    private String name;

    private Boolean smoking;

    private BigDecimal surcharge;

    private String note;
}
