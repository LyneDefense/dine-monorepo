package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.config.handler.StringListTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "ai_phone_settings", autoResultMap = true)
public class AiPhoneSettings extends BaseEntity {

    private Long restaurantId;

    private String language;

    private String greetingMessage;

    private String escalationPhone;

    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> escalationTriggers;
}
