package com.dine.backend.dto.request;

import com.dine.backend.entity.enums.OrderTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderCreateRequest {

    @NotNull(message = "Order type is required")
    private OrderTypeEnum orderType;

    private Long tableId;

    private String customerName;

    private String customerPhone;

    private Integer partySize;

    private LocalDateTime scheduledTime;

    private String specialInstructions;

    @NotNull(message = "Order items are required")
    private List<OrderItemRequest> items;
}
