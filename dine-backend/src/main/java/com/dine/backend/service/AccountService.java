package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.AccountCreateRequest;
import com.dine.backend.dto.request.AccountUpdateRequest;
import com.dine.backend.dto.response.AccountVO;
import com.dine.backend.entity.Account;

import java.util.List;

public interface AccountService extends IService<Account> {

    Account findByEmail(String email);

    List<Account> listByRestaurantId(Long restaurantId);

    List<AccountVO> getAccounts(Long restaurantId);

    AccountVO getAccountById(Long restaurantId, Long id);

    AccountVO createAccount(Long restaurantId, AccountCreateRequest request);

    AccountVO updateAccount(Long restaurantId, Long id, AccountUpdateRequest request);

    void deleteAccount(Long restaurantId, Long id);

    void updateLastLogin(Long id);

    void resetPassword(Long restaurantId, Long id, String newPassword);
}
