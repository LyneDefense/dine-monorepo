package com.dine.backend.controller;

import com.dine.backend.common.Result;
import com.dine.backend.dto.request.AccountCreateRequest;
import com.dine.backend.dto.request.AccountUpdateRequest;
import com.dine.backend.dto.request.PasswordChangeRequest;
import com.dine.backend.dto.response.AccountVO;
import com.dine.backend.entity.enums.AccountRoleEnum;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.security.SecurityContextUtils;
import com.dine.backend.security.annotation.RestaurantAccess;
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
    @RestaurantAccess
    public Result<List<AccountVO>> list(@PathVariable Long restaurantId) {
        return Result.success(accountService.getAccounts(restaurantId));
    }

    @Operation(summary = "获取账户详情")
    @GetMapping("/{id}")
    @RestaurantAccess
    public Result<AccountVO> getById(@PathVariable Long restaurantId, @PathVariable Long id) {
        return Result.success(accountService.getAccountById(restaurantId, id));
    }

    @Operation(summary = "创建账户")
    @PostMapping
    @RestaurantAccess
    public Result<AccountVO> create(
            @PathVariable Long restaurantId,
            @Valid @RequestBody AccountCreateRequest request) {
        // ADMIN只能创建STAFF，不能创建ADMIN或SUPER_ADMIN
        if (!SecurityContextUtils.isSuperAdmin()) {
            if (request.getRole() != AccountRoleEnum.STAFF) {
                throw BusinessException.forbidden("Only SUPER_ADMIN can create ADMIN accounts");
            }
        }
        return Result.success(accountService.createAccount(restaurantId, request));
    }

    @Operation(summary = "更新账户")
    @PutMapping("/{id}")
    @RestaurantAccess
    public Result<AccountVO> update(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @Valid @RequestBody AccountUpdateRequest request) {
        // ADMIN不能将角色提升为ADMIN或SUPER_ADMIN
        if (!SecurityContextUtils.isSuperAdmin() && request.getRole() != null) {
            if (request.getRole() != AccountRoleEnum.STAFF) {
                throw BusinessException.forbidden("Only SUPER_ADMIN can assign ADMIN role");
            }
        }
        return Result.success(accountService.updateAccount(restaurantId, id, request));
    }

    @Operation(summary = "删除账户")
    @DeleteMapping("/{id}")
    @RestaurantAccess
    public Result<Void> delete(@PathVariable Long restaurantId, @PathVariable Long id) {
        accountService.deleteAccount(restaurantId, id);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PostMapping("/{id}/reset-password")
    @RestaurantAccess
    public Result<Void> resetPassword(
            @PathVariable Long restaurantId,
            @PathVariable Long id,
            @RequestBody String newPassword) {
        accountService.resetPassword(restaurantId, id, newPassword);
        return Result.success();
    }

    @Operation(summary = "激活账户")
    @PatchMapping("/{id}/activate")
    @RestaurantAccess
    public Result<AccountVO> activate(@PathVariable Long restaurantId, @PathVariable Long id) {
        AccountUpdateRequest request = new AccountUpdateRequest();
        request.setActive(true);
        return Result.success(accountService.updateAccount(restaurantId, id, request));
    }

    @Operation(summary = "禁用账户")
    @PatchMapping("/{id}/deactivate")
    @RestaurantAccess
    public Result<AccountVO> deactivate(@PathVariable Long restaurantId, @PathVariable Long id) {
        AccountUpdateRequest request = new AccountUpdateRequest();
        request.setActive(false);
        return Result.success(accountService.updateAccount(restaurantId, id, request));
    }
}
