package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.OrderTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderTypeConfigVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    private OrderTypeEnum orderType;

    // 通用配置
    private Boolean enabled;
    private BigDecimal minOrderAmount;
    private Integer lastOrderMinutesBeforeClose;
    private Boolean autoConfirmEnabled;
    private Integer peakHourOrderLimit;

    // 取消政策
    private Boolean allowCancellation;
    private Integer cancelDeadlineMinutes;
    private Boolean cancelReasonRequired;
    private String cancelPolicyNote;

    // DINE_IN 专属配置
    private Boolean reservationEnabled;
    private Boolean walkInEnabled;
    private Integer maxAdvanceDays;
    private Integer minAdvanceMinutes;
    private Integer maxReservationsPerSlot;
    private Boolean sectionPreferenceEnabled;
    private Boolean preOrderEnabled;
    private Boolean preOrderRequired;
    private Boolean queueEnabled;
    private String estimatedWaitStrategy;
    private BigDecimal reservationDepositAmount;
    private Boolean reservationDepositRequired;
    private String noShowPolicyNote;
    private Integer defaultDiningDurationMinutes;    // 默认用餐时长（分钟）
    private Boolean tableMergingEnabled;             // 是否允许拼桌
    private Boolean tableMergingRequiresApproval;    // 拼桌是否需要人工审核

    // TAKEOUT 专属配置
    private Integer minPrepMinutes;
    private Boolean scheduledPickupEnabled;
    private Integer maxScheduledAdvanceHours;
    private Integer maxOrdersPerSlot;
    private String pickupInstructions;
    private String pickupLocationNote;
    private Boolean smsNotificationEnabled;
    private Boolean callNotificationEnabled;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
