package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.ParkingFeeTypeEnum;
import com.dine.backend.entity.enums.ParkingTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RestaurantSettingsVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    // Order types
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
