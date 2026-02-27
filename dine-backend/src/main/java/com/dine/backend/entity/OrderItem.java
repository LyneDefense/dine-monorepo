package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.config.handler.JsonbTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "order_item", autoResultMap = true)
public class OrderItem extends BaseEntity {

    private Long orderId;

    private Long itemId;

    private Long comboId;

    private Integer quantity;

    private String variantName;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<Map<String, Object>> selectedModifiers;

    private String itemNote;

    private BigDecimal subtotal;
}
