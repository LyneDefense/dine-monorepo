package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.DayOfWeekEnum;
import com.dine.backend.entity.enums.OrderTypeEnum;
import lombok.Data;

import java.time.LocalTime;

@Data
public class OperatingHoursVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    private DayOfWeekEnum dayOfWeek;

    private OrderTypeEnum orderType;

    private LocalTime openTime;

    private LocalTime closeTime;
}
