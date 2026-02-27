package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_phone_instruction")
public class AiPhoneInstruction extends BaseEntity {

    private Long settingsId;

    private String instruction;

    private Integer sortOrder;
}
