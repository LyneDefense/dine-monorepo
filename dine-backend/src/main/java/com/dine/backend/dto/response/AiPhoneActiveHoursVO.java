package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.DayOfWeekEnum;
import lombok.Data;

import java.time.LocalTime;

@Data
public class AiPhoneActiveHoursVO {

    @LongToString
    private Long id;

    @LongToString
    private Long settingsId;

    private DayOfWeekEnum dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;
}
