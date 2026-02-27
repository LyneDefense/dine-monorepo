package com.dine.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ComboGroupRequest {

    @NotBlank(message = "Group name is required")
    private String name;

    @NotNull(message = "Selection count is required")
    private Integer selectionCount;

    private Integer sortOrder;

    private List<ComboGroupItemRequest> items;
}
