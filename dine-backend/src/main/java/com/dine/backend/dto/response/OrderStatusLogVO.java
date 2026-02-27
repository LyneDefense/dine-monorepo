package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.OrderStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatusLogVO {

    @LongToString
    private Long id;

    @LongToString
    private Long orderId;

    private OrderStatusEnum fromStatus;

    private OrderStatusEnum toStatus;

    private String reason;

    @LongToString
    private Long changedBy;

    private LocalDateTime createdAt;
}
