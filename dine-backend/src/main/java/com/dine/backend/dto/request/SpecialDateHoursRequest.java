package com.dine.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SpecialDateHoursRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Is closed flag is required")
    private Boolean isClosed;

    private LocalTime openTime;

    private LocalTime closeTime;

    private String note;
}
