package com.dine.backend.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
public class MenuItemUpdateRequest {

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
}
