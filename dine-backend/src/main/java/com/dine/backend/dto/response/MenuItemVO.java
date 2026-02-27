package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
public class MenuItemVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    @LongToString
    private Long categoryId;

    private String name;

    private String description;

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
    private List<MenuItemVariantVO> variants;

    private List<ModifierGroupVO> modifierGroups;

    private List<String> aliases;
}
