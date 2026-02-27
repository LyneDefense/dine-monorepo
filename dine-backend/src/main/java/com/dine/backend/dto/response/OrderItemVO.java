package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class OrderItemVO {

    @LongToString
    private Long id;

    @LongToString
    private Long orderId;

    @LongToString
    private Long menuItemId;

    private String menuItemName;

    @LongToString
    private Long variantId;

    private String variantName;

    @LongToString
    private Long comboId;

    private String comboName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    private Map<String, Object> modifiers;

    private String specialInstructions;
}
