package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.OrderStatusEnum;
import com.dine.backend.entity.enums.OrderTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    private String orderNumber;

    private OrderTypeEnum orderType;

    private OrderStatusEnum status;

    @LongToString
    private Long tableId;

    private String tableName;

    private String customerName;

    private String customerPhone;

    private Integer partySize;

    private LocalDateTime scheduledTime;

    private BigDecimal subtotal;

    private BigDecimal tax;

    private BigDecimal total;

    private String specialInstructions;

    private String cancellationReason;

    private LocalDateTime confirmedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<OrderItemVO> items;

    private List<OrderStatusLogVO> statusLogs;
}
