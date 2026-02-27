package com.dine.backend.controller;

import com.dine.backend.common.Result;
import com.dine.backend.dto.request.LoginRequest;
import com.dine.backend.dto.response.AccountVO;
import com.dine.backend.dto.response.LoginVO;
import com.dine.backend.entity.Account;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.security.AccountUserDetails;
import com.dine.backend.security.JwtUtil;
import com.dine.backend.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Authentication", description = "用户认证相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AccountService accountService;

    @Operation(summary = "用户登录", description = "使用邮箱和密码登录，返回JWT令牌")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            AccountUserDetails userDetails = (AccountUserDetails) authentication.getPrincipal();
            Account account = userDetails.getAccount();

            // Update last login time
            account.setLastLoginAt(LocalDateTime.now());
            accountService.updateById(account);

            // Generate token
            String token = jwtUtil.generateToken(
                    account.getId(),
                    account.getRestaurantId(),
                    account.getEmail(),
                    account.getRole().name()
            );

            // Build response
            AccountVO accountVO = new AccountVO();
            accountVO.setId(account.getId());
            accountVO.setRestaurantId(account.getRestaurantId());
            accountVO.setEmail(account.getEmail());
            accountVO.setName(account.getName());
            accountVO.setRole(account.getRole());
            accountVO.setActive(account.getActive());
            accountVO.setLastLoginAt(account.getLastLoginAt());
            accountVO.setCreatedAt(account.getCreatedAt());
            accountVO.setUpdatedAt(account.getUpdatedAt());

            LoginVO loginVO = new LoginVO(token, jwtUtil.getExpirationInSeconds(), accountVO);

            return Result.success(loginVO);
        } catch (BadCredentialsException e) {
            throw BusinessException.unauthorized("Invalid email or password");
        }
    }

    @Operation(summary = "用户登出", description = "清除当前会话")
    @PostMapping("/logout")
    public Result<Void> logout() {
        SecurityContextHolder.clearContext();
        return Result.success();
    }

    @Operation(summary = "刷新令牌", description = "获取新的JWT令牌")
    @PostMapping("/refresh")
    public Result<LoginVO> refreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AccountUserDetails)) {
            throw BusinessException.unauthorized("Not authenticated");
        }

        AccountUserDetails userDetails = (AccountUserDetails) authentication.getPrincipal();
        Account account = userDetails.getAccount();

        // Generate new token
        String token = jwtUtil.generateToken(
                account.getId(),
                account.getRestaurantId(),
                account.getEmail(),
                account.getRole().name()
        );

        AccountVO accountVO = new AccountVO();
        accountVO.setId(account.getId());
        accountVO.setRestaurantId(account.getRestaurantId());
        accountVO.setEmail(account.getEmail());
        accountVO.setName(account.getName());
        accountVO.setRole(account.getRole());
        accountVO.setActive(account.getActive());
        accountVO.setLastLoginAt(account.getLastLoginAt());
        accountVO.setCreatedAt(account.getCreatedAt());
        accountVO.setUpdatedAt(account.getUpdatedAt());

        LoginVO loginVO = new LoginVO(token, jwtUtil.getExpirationInSeconds(), accountVO);

        return Result.success(loginVO);
    }

    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    @GetMapping("/me")
    public Result<AccountVO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AccountUserDetails)) {
            throw BusinessException.unauthorized("Not authenticated");
        }

        AccountUserDetails userDetails = (AccountUserDetails) authentication.getPrincipal();
        Account account = userDetails.getAccount();

        AccountVO accountVO = new AccountVO();
        accountVO.setId(account.getId());
        accountVO.setRestaurantId(account.getRestaurantId());
        accountVO.setEmail(account.getEmail());
        accountVO.setName(account.getName());
        accountVO.setRole(account.getRole());
        accountVO.setActive(account.getActive());
        accountVO.setLastLoginAt(account.getLastLoginAt());
        accountVO.setCreatedAt(account.getCreatedAt());
        accountVO.setUpdatedAt(account.getUpdatedAt());

        return Result.success(accountVO);
    }
}
