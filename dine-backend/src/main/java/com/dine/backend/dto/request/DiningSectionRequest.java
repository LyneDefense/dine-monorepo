package com.dine.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DiningSectionRequest {

    @NotBlank(message = "Section name is required")
    private String name;

    private String description;

    private Integer sortOrder;
}
