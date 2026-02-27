package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.entity.enums.DayOfWeekEnum;
import com.dine.backend.entity.enums.OrderTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("operating_hours")
public class OperatingHours extends BaseEntity {

    private Long restaurantId;

    private DayOfWeekEnum dayOfWeek;

    private OrderTypeEnum orderType;

    private LocalTime openTime;

    private LocalTime closeTime;
}
