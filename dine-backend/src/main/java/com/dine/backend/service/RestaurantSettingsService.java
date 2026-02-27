package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.RestaurantSettingsRequest;
import com.dine.backend.dto.response.RestaurantSettingsVO;
import com.dine.backend.entity.RestaurantSettings;

public interface RestaurantSettingsService extends IService<RestaurantSettings> {

    RestaurantSettings getByRestaurantId(Long restaurantId);

    RestaurantSettingsVO getSettings(Long restaurantId);

    RestaurantSettingsVO updateSettings(Long restaurantId, RestaurantSettingsRequest request);

    RestaurantSettingsVO createDefaultSettings(Long restaurantId);
}
