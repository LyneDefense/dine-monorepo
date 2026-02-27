package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.entity.enums.DayOfWeekEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_phone_active_hours")
public class AiPhoneActiveHours extends BaseEntity {

    private Long settingsId;

    private DayOfWeekEnum dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;
}
