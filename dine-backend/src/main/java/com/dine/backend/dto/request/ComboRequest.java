package com.dine.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ComboRequest {

    @NotBlank(message = "Combo name is required")
    private String name;

    private String description;

    @NotNull(message = "Combo price is required")
    private BigDecimal price;

    private String image;

    private Boolean available;

    private Integer sortOrder;

    private List<ComboGroupRequest> groups;
}
