package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.RestaurantSettingsRequest;
import com.dine.backend.dto.response.RestaurantSettingsVO;
import com.dine.backend.entity.RestaurantSettings;
import com.dine.backend.entity.enums.ParkingFeeTypeEnum;
import com.dine.backend.entity.enums.ParkingTypeEnum;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.RestaurantSettingsMapper;
import com.dine.backend.service.RestaurantSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantSettingsServiceImpl extends ServiceImpl<RestaurantSettingsMapper, RestaurantSettings> implements RestaurantSettingsService {

    private final EntityConverter converter;

    @Override
    public RestaurantSettings getByRestaurantId(Long restaurantId) {
        return getOne(new LambdaQueryWrapper<RestaurantSettings>()
                .eq(RestaurantSettings::getRestaurantId, restaurantId));
    }

    @Override
    public RestaurantSettingsVO getSettings(Long restaurantId) {
        RestaurantSettings settings = getByRestaurantId(restaurantId);
        if (settings == null) {
            // Auto-create default settings if not exists
            return createDefaultSettings(restaurantId);
        }
        return converter.toRestaurantSettingsVO(settings);
    }

    @Override
    @Transactional
    public RestaurantSettingsVO updateSettings(Long restaurantId, RestaurantSettingsRequest request) {
        RestaurantSettings settings = getByRestaurantId(restaurantId);
        if (settings == null) {
            throw BusinessException.notFound("Restaurant settings not found");
        }

        // Update tax fields if provided
        if (request.getTaxRate() != null) settings.setTaxRate(request.getTaxRate());
        if (request.getTaxName() != null) settings.setTaxName(request.getTaxName());
        if (request.getTaxInclusive() != null) settings.setTaxInclusive(request.getTaxInclusive());

        // Update parking fields if provided
        if (request.getHasParking() != null) settings.setHasParking(request.getHasParking());
        if (request.getParkingType() != null) {
            settings.setParkingType(ParkingTypeEnum.valueOf(request.getParkingType()));
        }
        if (request.getParkingCapacity() != null) settings.setParkingCapacity(request.getParkingCapacity());
        if (request.getParkingFeeType() != null) {
            settings.setParkingFeeType(ParkingFeeTypeEnum.valueOf(request.getParkingFeeType()));
        }
        if (request.getParkingFreeWithMinSpend() != null) settings.setParkingFreeWithMinSpend(request.getParkingFreeWithMinSpend());
        if (request.getParkingHourlyRate() != null) settings.setParkingHourlyRate(request.getParkingHourlyRate());
        if (request.getParkingAddress() != null) settings.setParkingAddress(request.getParkingAddress());
        if (request.getParkingNotes() != null) settings.setParkingNotes(request.getParkingNotes());

        updateById(settings);
        return converter.toRestaurantSettingsVO(settings);
    }

    @Override
    @Transactional
    public RestaurantSettingsVO createDefaultSettings(Long restaurantId) {
        RestaurantSettings settings = new RestaurantSettings();
        settings.setRestaurantId(restaurantId);
        // Default values are set in entity or database
        save(settings);
        return converter.toRestaurantSettingsVO(settings);
    }
}
