package com.dine.backend.dto.request;

import com.dine.backend.entity.enums.DayOfWeekEnum;
import com.dine.backend.entity.enums.OrderTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class OperatingHoursRequest {

    @NotNull(message = "Day of week is required")
    private DayOfWeekEnum dayOfWeek;

    private OrderTypeEnum orderType;

    @NotNull(message = "Open time is required")
    private LocalTime openTime;

    @NotNull(message = "Close time is required")
    private LocalTime closeTime;
}
