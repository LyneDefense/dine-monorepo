package com.dine.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemVariantRequest {

    @NotBlank(message = "Variant name is required")
    private String name;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private Integer sortOrder;
}
