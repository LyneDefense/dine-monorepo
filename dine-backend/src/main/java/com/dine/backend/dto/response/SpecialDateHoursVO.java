package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SpecialDateHoursVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    private LocalDate date;

    private Boolean isClosed;

    private LocalTime openTime;

    private LocalTime closeTime;

    private String note;
}
