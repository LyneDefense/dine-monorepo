package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.entity.enums.OrderStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_status_log")
public class OrderStatusLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long orderId;

    private OrderStatusEnum fromStatus;

    private OrderStatusEnum toStatus;

    private String reason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
