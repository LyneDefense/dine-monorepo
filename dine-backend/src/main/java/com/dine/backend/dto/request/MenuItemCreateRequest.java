package com.dine.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
public class MenuItemCreateRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Item name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private String image;

    private List<String> allergens;

    private List<String> tags;

    private Boolean available;

    private Integer sortOrder;

    // Availability schedule
    private List<String> availableDays;

    private LocalTime availableStartTime;

    private LocalTime availableEndTime;

    // Nested data
    private List<MenuItemVariantRequest> variants;

    private List<ModifierGroupRequest> modifierGroups;

    private List<String> aliases;
}
