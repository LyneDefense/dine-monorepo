package com.dine.backend.dto.request;

import com.dine.backend.entity.enums.TableStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiningTableRequest {

    @NotBlank(message = "Table name is required")
    private String name;

    @NotNull(message = "Capacity is required")
    private Integer capacity;

    private TableStatusEnum status;

    private Integer sortOrder;
}
