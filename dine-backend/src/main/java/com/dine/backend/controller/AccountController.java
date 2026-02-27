package com.dine.backend.controller;

import com.dine.backend.common.Result;
import com.dine.backend.dto.request.AccountCreateRequest;
import com.dine.backend.dto.request.AccountUpdateRequest;
import com.dine.backend.dto.request.PasswordChangeRequest;
import com.dine.backend.dto.response.AccountVO;
import com.dine.backend.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Account", description = "账户管理接口")
@RestController
@RequestMapping("/api/restaurants/{restaurantId}/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "获取账户列表")
    @GetMapping
    public Result<List<AccountVO>> list(@PathVariable Long restaurantId) {
        return Result.success(accountService.getAccounts(restaurantId));
    }

    @Operation(summary = "获取账户详情")
    @GetMapping("/{id}")
    public Result<AccountVO> getById(@PathVariable Long restaurantId, @PathVariable Long id) {
        return Result.success(accountService.getAccountById(restaurantId, id));
    }

    @Operation(summary = "创建账户")
    @PostMapping
    public Result<AccountVO> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody AccountCreateRequest request) {
        return Result.success(accountService.createAccount(restaurantId, request));
    }

    @Operation(summary = "更新账户")
    @PutMapping("/{id}")
    public Result<AccountVO> update(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @Valid @RequestBody AccountUpdateRequest request) {
        return Result.success(accountService.updateAccount(restaurantId, id, request));
    }

    @Operation(summary = "删除账户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long restaurantId, @PathVariable Long id) {
        accountService.deleteAccount(restaurantId, id);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @RequestBody String newPassword) {
        accountService.resetPassword(restaurantId, id, newPassword);
        return Result.success();
    }

    @Operation(summary = "激活账户")
    @PatchMapping("/{id}/activate")
    public Result<AccountVO> activate(@PathVariable Long restaurantId, @PathVariable Long id) {
        AccountUpdateRequest request = new AccountUpdateRequest();
        request.setActive(true);
        return Result.success(accountService.updateAccount(restaurantId, id, request));
    }

    @Operation(summary = "禁用账户")
    @PatchMapping("/{id}/deactivate")
    public Result<AccountVO> deactivate(@PathVariable Long restaurantId, @PathVariable Long id) {
        AccountUpdateRequest request = new AccountUpdateRequest();
        request.setActive(false);
        return Result.success(accountService.updateAccount(restaurantId, id, request));
    }
}
