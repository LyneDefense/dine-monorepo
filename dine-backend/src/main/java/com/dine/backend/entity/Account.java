package com.dine.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dine.backend.common.BaseEntity;
import com.dine.backend.entity.enums.AccountRoleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("account")
public class Account extends BaseEntity {

    private Long restaurantId;

    private String email;

    @TableField("password_hash")
    private String passwordHash;

    private String name;

    private AccountRoleEnum role;

    private Boolean active;

    private LocalDateTime lastLoginAt;
}
