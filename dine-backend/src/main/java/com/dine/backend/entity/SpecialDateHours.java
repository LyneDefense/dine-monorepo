package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("special_date_hours")
public class SpecialDateHours extends BaseEntity {

    private Long restaurantId;

    private LocalDate date;

    private Boolean isClosed;

    private LocalTime openTime;

    private LocalTime closeTime;

    private String note;
}
