package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.entity.enums.TableStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dining_table")
public class DiningTable extends BaseEntity {

    private Long restaurantId;

    private Long sectionId;

    private String tableNumber;

    private Integer capacity;

    private TableStatusEnum status;
}
