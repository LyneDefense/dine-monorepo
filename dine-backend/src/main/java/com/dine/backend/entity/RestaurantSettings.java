package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.config.handler.StringListTypeHandler;
import com.dine.backend.entity.enums.ParkingFeeTypeEnum;
import com.dine.backend.entity.enums.ParkingTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "restaurant_settings", autoResultMap = true)
public class RestaurantSettings extends BaseEntity {

    private Long restaurantId;

    // Order types
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> acceptedOrderTypes;

    // Last order
    private Integer lastOrderMinutesBeforeClose;

    // Tax
    private BigDecimal taxRate;
    private String taxName;
    private Boolean taxInclusive;

    // Dine-in
    private Boolean reservationEnabled;
    private Integer maxAdvanceDays;
    private Integer minAdvanceMinutes;
    private Integer maxReservationsPerSlot;
    private Boolean sectionPreferenceEnabled;
    private Boolean preOrderEnabled;
    private Boolean preOrderRequired;

    // Takeout
    private Integer minPrepMinutes;
    private Boolean scheduledPickupEnabled;
    private Integer maxScheduledAdvanceHours;
    private Integer maxOrdersPerSlot;
    private String pickupInstructions;

    // Cancellation
    private Boolean allowCancellation;
    private Integer cancelDeadlineHours;
    private Boolean cancelReasonRequired;
    private String cancelPolicyNote;

    // Queue
    private Boolean queueEnabled;
    private String estimatedWaitStrategy;

    // Capacity
    private Integer peakHourOrderLimit;

    // Auto confirm
    private Boolean autoConfirmEnabled;

    // Parking
    private Boolean hasParking;
    private ParkingTypeEnum parkingType;
    private Integer parkingCapacity;
    private ParkingFeeTypeEnum parkingFeeType;
    private BigDecimal parkingFreeWithMinSpend;
    private BigDecimal parkingHourlyRate;
    private String parkingAddress;
    private String parkingNotes;
}
