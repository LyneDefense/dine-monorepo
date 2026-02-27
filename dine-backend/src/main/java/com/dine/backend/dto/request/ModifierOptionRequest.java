package com.dine.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ModifierOptionRequest {

    @NotBlank(message = "Option name is required")
    private String name;

    private BigDecimal extraPrice;

    private Boolean isDefault;

    private Integer sortOrder;
}
