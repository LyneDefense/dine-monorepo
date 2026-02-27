package com.dine.backend.dto.request;

import com.dine.backend.entity.enums.OrderStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private OrderStatusEnum status;

    private String reason;
}
