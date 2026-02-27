package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.config.handler.StringListTypeHandler;
import com.dine.backend.entity.enums.RestaurantStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "restaurant", autoResultMap = true)
public class Restaurant extends BaseEntity {

    private String name;

    private String description;

    private String address;

    private String phone;

    private String timezone;

    private String logo;

    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> images;

    private RestaurantStatusEnum status;
}
