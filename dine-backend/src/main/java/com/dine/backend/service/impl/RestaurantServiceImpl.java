package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.RestaurantCreateRequest;
import com.dine.backend.dto.request.RestaurantUpdateRequest;
import com.dine.backend.dto.response.RestaurantVO;
import com.dine.backend.entity.OperatingHours;
import com.dine.backend.entity.Restaurant;
import com.dine.backend.entity.SpecialDateHours;
import com.dine.backend.entity.enums.DayOfWeekEnum;
import com.dine.backend.entity.enums.RestaurantStatusEnum;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.RestaurantMapper;
import com.dine.backend.service.OperatingHoursService;
import com.dine.backend.service.RestaurantService;
import com.dine.backend.service.SpecialDateHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl extends ServiceImpl<RestaurantMapper, Restaurant> implements RestaurantService {

    private final EntityConverter converter;
    private final OperatingHoursService operatingHoursService;
    private final SpecialDateHoursService specialDateHoursService;

    @Override
    @Transactional
    public RestaurantVO createRestaurant(RestaurantCreateRequest request) {
        Restaurant restaurant = new Restaurant();
        BeanUtils.copyProperties(request, restaurant);
        restaurant.setStatus(RestaurantStatusEnum.OPEN);
        save(restaurant);
        return converter.toRestaurantVO(restaurant);
    }

    @Override
    public RestaurantVO getRestaurantById(Long id) {
        Restaurant restaurant = getById(id);
        if (restaurant == null) {
            throw BusinessException.notFound("Restaurant not found");
        }
        return converter.toRestaurantVO(restaurant);
    }

    @Override
    @Transactional
    public RestaurantVO updateRestaurant(Long id, RestaurantUpdateRequest request) {
        Restaurant restaurant = getById(id);
        if (restaurant == null) {
            throw BusinessException.notFound("Restaurant not found");
        }

        if (request.getName() != null) restaurant.setName(request.getName());
        if (request.getDescription() != null) restaurant.setDescription(request.getDescription());
        if (request.getAddress() != null) restaurant.setAddress(request.getAddress());
        if (request.getPhone() != null) restaurant.setPhone(request.getPhone());
        if (request.getTimezone() != null) restaurant.setTimezone(request.getTimezone());
        if (request.getLogo() != null) restaurant.setLogo(request.getLogo());
        if (request.getImages() != null) restaurant.setImages(request.getImages());
        if (request.getStatus() != null) restaurant.setStatus(request.getStatus());

        updateById(restaurant);
        return converter.toRestaurantVO(restaurant);
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long id) {
        Restaurant restaurant = getById(id);
        if (restaurant == null) {
            throw BusinessException.notFound("Restaurant not found");
        }
        removeById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, RestaurantStatusEnum status) {
        Restaurant restaurant = getById(id);
        if (restaurant == null) {
            throw BusinessException.notFound("Restaurant not found");
        }
        restaurant.setStatus(status);
        updateById(restaurant);
    }

    @Override
    public boolean isOpen(Long restaurantId) {
        Restaurant restaurant = getById(restaurantId);
        if (restaurant == null || restaurant.getStatus() != RestaurantStatusEnum.OPEN) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Check special date hours first
        List<SpecialDateHours> specialHours = specialDateHoursService.listByRestaurantIdAndDate(restaurantId, today);
        if (!specialHours.isEmpty()) {
            SpecialDateHours special = specialHours.get(0);
            if (special.getIsClosed()) {
                return false;
            }
            if (special.getOpenTime() != null && special.getCloseTime() != null) {
                return !now.isBefore(special.getOpenTime()) && now.isBefore(special.getCloseTime());
            }
        }

        // Check regular operating hours
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        DayOfWeekEnum dayEnum = DayOfWeekEnum.valueOf(dayOfWeek.name().substring(0, 3));

        List<OperatingHours> hours = operatingHoursService.listByRestaurantId(restaurantId);
        for (OperatingHours hour : hours) {
            if (hour.getDayOfWeek() == dayEnum) {
                if (!now.isBefore(hour.getOpenTime()) && now.isBefore(hour.getCloseTime())) {
                    return true;
                }
            }
        }

        return false;
    }
}
