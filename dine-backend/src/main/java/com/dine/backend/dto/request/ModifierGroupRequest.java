package com.dine.backend.dto.request;

import com.dine.backend.entity.enums.SelectionTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ModifierGroupRequest {

    @NotBlank(message = "Group name is required")
    private String name;

    @NotNull(message = "Selection type is required")
    private SelectionTypeEnum selectionType;

    private Boolean required;

    private Integer maxSelections;

    private Integer sortOrder;

    private List<ModifierOptionRequest> options;
}
