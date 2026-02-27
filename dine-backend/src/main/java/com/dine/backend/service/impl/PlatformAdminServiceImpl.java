package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dine.backend.common.PageResult;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.CreateRestaurantWithAdminRequest;
import com.dine.backend.dto.response.AccountVO;
import com.dine.backend.dto.response.RestaurantVO;
import com.dine.backend.dto.response.RestaurantWithAdminVO;
import com.dine.backend.entity.Account;
import com.dine.backend.entity.Restaurant;
import com.dine.backend.entity.enums.AccountRoleEnum;
import com.dine.backend.entity.enums.RestaurantStatusEnum;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.AccountMapper;
import com.dine.backend.mapper.RestaurantMapper;
import com.dine.backend.service.PlatformAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlatformAdminServiceImpl implements PlatformAdminService {

    private final RestaurantMapper restaurantMapper;
    private final AccountMapper accountMapper;
    private final EntityConverter converter;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RestaurantWithAdminVO createRestaurantWithAdmin(CreateRestaurantWithAdminRequest request) {
        // 检查邮箱是否已存在
        Account existing = accountMapper.selectOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getEmail, request.getAdminEmail()));
        if (existing != null) {
            throw BusinessException.badRequest("Email already exists: " + request.getAdminEmail());
        }

        // 1. 创建餐厅
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getRestaurantName());
        restaurant.setAddress(request.getRestaurantAddress());
        restaurant.setPhone(request.getRestaurantPhone());
        restaurant.setTimezone(request.getTimezone());
        restaurant.setStatus(RestaurantStatusEnum.OPEN);
        restaurantMapper.insert(restaurant);

        // 2. 创建管理员账号
        Account admin = new Account();
        admin.setRestaurantId(restaurant.getId());
        admin.setEmail(request.getAdminEmail());
        admin.setPasswordHash(passwordEncoder.encode(request.getAdminPassword()));
        admin.setName(request.getAdminName());
        admin.setRole(AccountRoleEnum.ADMIN);
        admin.setActive(true);
        accountMapper.insert(admin);

        // 3. 构建响应
        RestaurantWithAdminVO vo = new RestaurantWithAdminVO();
        vo.setRestaurantId(restaurant.getId());
        vo.setRestaurantName(restaurant.getName());
        vo.setRestaurantAddress(restaurant.getAddress());
        vo.setRestaurantPhone(restaurant.getPhone());
        vo.setTimezone(restaurant.getTimezone());
        vo.setAdminId(admin.getId());
        vo.setAdminEmail(admin.getEmail());
        vo.setAdminName(admin.getName());

        return vo;
    }

    @Override
    public PageResult<RestaurantVO> listAllRestaurants(Integer page, Integer size) {
        Page<Restaurant> pageParam = new Page<>(page, size);
        Page<Restaurant> result = restaurantMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Restaurant>()
                        .orderByDesc(Restaurant::getCreatedAt));

        List<RestaurantVO> records = result.getRecords().stream()
                .map(converter::toRestaurantVO)
                .collect(Collectors.toList());

        return PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PageResult<AccountVO> listAllAccounts(Integer page, Integer size) {
        Page<Account> pageParam = new Page<>(page, size);
        Page<Account> result = accountMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Account>()
                        .orderByDesc(Account::getCreatedAt));

        List<AccountVO> records = result.getRecords().stream()
                .map(converter::toAccountVO)
                .collect(Collectors.toList());

        return PageResult.of(records, result.getTotal(), result.getCurrent(), result.getSize());
    }
}
