package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.entity.enums.ParkingFeeTypeEnum;
import com.dine.backend.entity.enums.ParkingTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "restaurant_settings", autoResultMap = true)
public class RestaurantSettings extends BaseEntity {

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
