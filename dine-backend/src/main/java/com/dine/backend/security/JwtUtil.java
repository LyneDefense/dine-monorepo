package com.dine.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long accountId, Long restaurantId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        // SUPER_ADMIN 的 restaurantId 为 null，不写入 JWT
        if (restaurantId != null) {
            claims.put("restaurantId", restaurantId);
        }
        claims.put("email", email);
        claims.put("role", role);
        return createToken(claims, accountId.toString(), jwtProperties.getExpiration());
    }

    public String generateRefreshToken(Long accountId) {
        return createToken(new HashMap<>(), accountId.toString(), jwtProperties.getRefreshExpiration());
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Long getAccountIdFromToken(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        return Long.parseLong(subject);
    }

    public Long getRestaurantIdFromToken(String token) {
        Claims claims = extractAllClaims(token);
        Object restaurantId = claims.get("restaurantId");
        if (restaurantId == null) {
            return null; // SUPER_ADMIN 没有 restaurantId
        }
        if (restaurantId instanceof Integer) {
            return ((Integer) restaurantId).longValue();
        }
        return (Long) restaurantId;
    }

    public String getEmailFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("email");
    }

    public String getRoleFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }

    public Date getExpirationFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public Long getExpirationInSeconds() {
        return jwtProperties.getExpiration() / 1000;
    }
}
