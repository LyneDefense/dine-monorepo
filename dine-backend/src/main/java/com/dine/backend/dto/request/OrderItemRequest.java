package com.dine.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class OrderItemRequest {

    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    private Long variantId;

    private Long comboId;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    private BigDecimal unitPrice;

    private Map<String, Object> modifiers;

    private String specialInstructions;
}
