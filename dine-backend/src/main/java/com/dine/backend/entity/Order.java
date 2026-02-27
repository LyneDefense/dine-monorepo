package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.entity.enums.OrderStatusEnum;
import com.dine.backend.entity.enums.OrderTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("orders")
public class Order extends BaseEntity {

    private Long restaurantId;

    private String orderNumber;

    private OrderTypeEnum type;

    private OrderStatusEnum status;

    // Customer
    private String customerName;

    private String customerPhone;

    private String customerNotes;

    // Dine-in fields
    private LocalDate reservationDate;

    private LocalTime reservationTime;

    private Integer partySize;

    private String sectionPreference;

    // Takeout fields
    private LocalDateTime pickupTime;

    // Totals
    private BigDecimal subtotal;

    private BigDecimal tax;

    private BigDecimal total;

    // Meta
    private String callId;

    private String cancelReason;

    private LocalDateTime confirmedAt;
}
