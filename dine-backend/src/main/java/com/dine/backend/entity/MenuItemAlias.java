package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("menu_item_alias")
public class MenuItemAlias extends BaseEntity {

    private Long itemId;

    private String aliasName;
}
