package com.dine.backend.service;

import com.dine.backend.common.PageResult;
import com.dine.backend.dto.request.CreateRestaurantWithAdminRequest;
import com.dine.backend.dto.response.AccountVO;
import com.dine.backend.dto.response.RestaurantVO;
import com.dine.backend.dto.response.RestaurantWithAdminVO;

/**
 * 平台管理服务（仅SUPER_ADMIN可用）
 */
public interface PlatformAdminService {

    /**
     * 创建餐厅和管理员账号（同时创建）
     */
    RestaurantWithAdminVO createRestaurantWithAdmin(CreateRestaurantWithAdminRequest request);

    /**
     * 获取所有餐厅列表（分页）
     */
    PageResult<RestaurantVO> listAllRestaurants(Integer page, Integer size);

    /**
     * 获取所有账号列表（分页）
     */
    PageResult<AccountVO> listAllAccounts(Integer page, Integer size);
}
