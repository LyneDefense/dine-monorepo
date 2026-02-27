package com.dine.backend.dto.request;

import com.dine.backend.entity.enums.AccountRoleEnum;
import lombok.Data;

@Data
public class AccountUpdateRequest {

    private String name;

    private AccountRoleEnum role;

    private Boolean active;
}
