package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.AccountCreateRequest;
import com.dine.backend.dto.request.AccountUpdateRequest;
import com.dine.backend.dto.response.AccountVO;
import com.dine.backend.entity.Account;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.AccountMapper;
import com.dine.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    private final EntityConverter converter;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Account findByEmail(String email) {
        return getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getEmail, email));
    }

    @Override
    public List<Account> listByRestaurantId(Long restaurantId) {
        return list(new LambdaQueryWrapper<Account>()
                .eq(Account::getRestaurantId, restaurantId));
    }

    @Override
    public List<AccountVO> getAccounts(Long restaurantId) {
        List<Account> accounts = listByRestaurantId(restaurantId);
        return accounts.stream()
                .map(converter::toAccountVO)
                .collect(Collectors.toList());
    }

    @Override
    public AccountVO getAccountById(Long restaurantId, Long id) {
        Account account = getById(id);
        if (account == null || !account.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Account not found");
        }
        return converter.toAccountVO(account);
    }

    @Override
    @Transactional
    public AccountVO createAccount(Long restaurantId, AccountCreateRequest request) {
        // Check if email already exists
        Account existing = findByEmail(request.getEmail());
        if (existing != null) {
            throw BusinessException.badRequest("Email already exists");
        }

        Account account = new Account();
        account.setRestaurantId(restaurantId);
        account.setEmail(request.getEmail());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setName(request.getName());
        account.setRole(request.getRole());
        account.setActive(true);
        save(account);

        return converter.toAccountVO(account);
    }

    @Override
    @Transactional
    public AccountVO updateAccount(Long restaurantId, Long id, AccountUpdateRequest request) {
        Account account = getById(id);
        if (account == null || !account.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Account not found");
        }

        if (request.getName() != null) {
            account.setName(request.getName());
        }
        if (request.getRole() != null) {
            account.setRole(request.getRole());
        }
        if (request.getActive() != null) {
            account.setActive(request.getActive());
        }
        updateById(account);

        return converter.toAccountVO(account);
    }

    @Override
    @Transactional
    public void deleteAccount(Long restaurantId, Long id) {
        Account account = getById(id);
        if (account == null || !account.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Account not found");
        }
        removeById(id);
    }

    @Override
    @Transactional
    public void updateLastLogin(Long id) {
        Account account = getById(id);
        if (account != null) {
            account.setLastLoginAt(LocalDateTime.now());
            updateById(account);
        }
    }

    @Override
    @Transactional
    public void resetPassword(Long restaurantId, Long id, String newPassword) {
        Account account = getById(id);
        if (account == null || !account.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Account not found");
        }
        account.setPasswordHash(passwordEncoder.encode(newPassword));
        updateById(account);
    }
}
