package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.ParkingFeeTypeEnum;
import com.dine.backend.entity.enums.ParkingTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestaurantSettingsVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    // Tax
    private BigDecimal taxRate;
    private String taxName;
    private Boolean taxInclusive;

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
