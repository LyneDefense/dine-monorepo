package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.OrderStatusEnum;
import com.dine.backend.entity.enums.OrderTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderListVO {

    @LongToString
    private Long id;

    private String orderNumber;

    private OrderTypeEnum orderType;

    private OrderStatusEnum status;

    private String tableName;

    private String customerName;

    private String customerPhone;

    private Integer partySize;

    private LocalDateTime scheduledTime;

    private BigDecimal total;

    private Integer itemCount;

    private LocalDateTime confirmedAt;

    private LocalDateTime createdAt;
}
