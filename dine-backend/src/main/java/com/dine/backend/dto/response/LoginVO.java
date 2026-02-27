package com.dine.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    private String accessToken;

    private String tokenType = "Bearer";

    private Long expiresIn;

    private AccountVO account;

    public LoginVO(String accessToken, Long expiresIn, AccountVO account) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.account = account;
    }
}
