package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.RestaurantCreateRequest;
import com.dine.backend.dto.request.RestaurantUpdateRequest;
import com.dine.backend.dto.response.RestaurantVO;
import com.dine.backend.entity.Restaurant;
import com.dine.backend.entity.enums.RestaurantStatusEnum;

public interface RestaurantService extends IService<Restaurant> {

    RestaurantVO createRestaurant(RestaurantCreateRequest request);

    RestaurantVO getRestaurantById(Long id);

    RestaurantVO updateRestaurant(Long id, RestaurantUpdateRequest request);

    void deleteRestaurant(Long id);

    void updateStatus(Long id, RestaurantStatusEnum status);

    boolean isOpen(Long restaurantId);
}
