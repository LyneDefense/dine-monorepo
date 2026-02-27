package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.AccountRoleEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountVO {

    @LongToString
    private Long id;

    @LongToString
    private Long restaurantId;

    private String email;

    private String name;

    private AccountRoleEnum role;

    private Boolean active;

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
