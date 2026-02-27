package com.dine.backend.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestaurantSettingsRequest {

    // Tax
    private BigDecimal taxRate;
    private String taxName;
    private Boolean taxInclusive;

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
