package com.dine.backend.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret = "dine-backend-jwt-secret-key-must-be-at-least-256-bits-long-for-hs256";

    private Long expiration = 86400000L; // 24 hours in milliseconds

    private Long refreshExpiration = 604800000L; // 7 days in milliseconds

    private String header = "Authorization";

    private String prefix = "Bearer ";
}
