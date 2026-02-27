package com.dine.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComboGroupItemRequest {

    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    private BigDecimal priceAdjustment;

    private Integer sortOrder;
}
