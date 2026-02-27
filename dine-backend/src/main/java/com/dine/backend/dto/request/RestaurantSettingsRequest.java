package com.dine.backend.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RestaurantSettingsRequest {

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
    private String parkingType;
    private Integer parkingCapacity;
    private String parkingFeeType;
    private BigDecimal parkingFreeWithMinSpend;
    private BigDecimal parkingHourlyRate;
    private String parkingAddress;
    private String parkingNotes;
}
