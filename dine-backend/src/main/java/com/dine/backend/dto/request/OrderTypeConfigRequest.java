package com.dine.backend.dto.request;

import com.dine.backend.entity.enums.OrderTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderTypeConfigRequest {

    @NotNull(message = "订单类型不能为空")
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

    // TAKEOUT 专属配置
    private Integer minPrepMinutes;
    private Boolean scheduledPickupEnabled;
    private Integer maxScheduledAdvanceHours;
    private Integer maxOrdersPerSlot;
    private String pickupInstructions;
    private String pickupLocationNote;
    private Boolean smsNotificationEnabled;
    private Boolean callNotificationEnabled;
}
