package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.OperatingHoursRequest;
import com.dine.backend.dto.response.OperatingHoursVO;
import com.dine.backend.entity.OperatingHours;
import com.dine.backend.mapper.OperatingHoursMapper;
import com.dine.backend.service.OperatingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatingHoursServiceImpl extends ServiceImpl<OperatingHoursMapper, OperatingHours> implements OperatingHoursService {

    private final EntityConverter converter;

    @Override
    public List<OperatingHours> listByRestaurantId(Long restaurantId) {
        return list(new LambdaQueryWrapper<OperatingHours>()
                .eq(OperatingHours::getRestaurantId, restaurantId)
                .orderByAsc(OperatingHours::getDayOfWeek));
    }

    @Override
    public List<OperatingHoursVO> getOperatingHours(Long restaurantId) {
        List<OperatingHours> hours = listByRestaurantId(restaurantId);
        return converter.toOperatingHoursVOList(hours);
    }

    @Override
    @Transactional
    public List<OperatingHoursVO> updateOperatingHours(Long restaurantId, List<OperatingHoursRequest> requests) {
        // Delete existing hours
        remove(new LambdaQueryWrapper<OperatingHours>()
                .eq(OperatingHours::getRestaurantId, restaurantId));

        // Create new hours
        List<OperatingHours> newHours = new ArrayList<>();
        for (OperatingHoursRequest request : requests) {
            OperatingHours hours = new OperatingHours();
            BeanUtils.copyProperties(request, hours);
            hours.setRestaurantId(restaurantId);
            newHours.add(hours);
        }

        if (!newHours.isEmpty()) {
            saveBatch(newHours);
        }

        return converter.toOperatingHoursVOList(newHours);
    }
}
