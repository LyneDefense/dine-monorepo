package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.RestaurantSettingsRequest;
import com.dine.backend.dto.response.RestaurantSettingsVO;
import com.dine.backend.entity.RestaurantSettings;
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

        // Update fields if provided
        if (request.getAcceptedOrderTypes() != null) settings.setAcceptedOrderTypes(request.getAcceptedOrderTypes());
        if (request.getLastOrderMinutesBeforeClose() != null) settings.setLastOrderMinutesBeforeClose(request.getLastOrderMinutesBeforeClose());
        if (request.getTaxRate() != null) settings.setTaxRate(request.getTaxRate());
        if (request.getTaxName() != null) settings.setTaxName(request.getTaxName());
        if (request.getTaxInclusive() != null) settings.setTaxInclusive(request.getTaxInclusive());
        if (request.getReservationEnabled() != null) settings.setReservationEnabled(request.getReservationEnabled());
        if (request.getMaxAdvanceDays() != null) settings.setMaxAdvanceDays(request.getMaxAdvanceDays());
        if (request.getMinAdvanceMinutes() != null) settings.setMinAdvanceMinutes(request.getMinAdvanceMinutes());
        if (request.getMaxReservationsPerSlot() != null) settings.setMaxReservationsPerSlot(request.getMaxReservationsPerSlot());
        if (request.getSectionPreferenceEnabled() != null) settings.setSectionPreferenceEnabled(request.getSectionPreferenceEnabled());
        if (request.getPreOrderEnabled() != null) settings.setPreOrderEnabled(request.getPreOrderEnabled());
        if (request.getPreOrderRequired() != null) settings.setPreOrderRequired(request.getPreOrderRequired());
        if (request.getMinPrepMinutes() != null) settings.setMinPrepMinutes(request.getMinPrepMinutes());
        if (request.getScheduledPickupEnabled() != null) settings.setScheduledPickupEnabled(request.getScheduledPickupEnabled());
        if (request.getMaxScheduledAdvanceHours() != null) settings.setMaxScheduledAdvanceHours(request.getMaxScheduledAdvanceHours());
        if (request.getMaxOrdersPerSlot() != null) settings.setMaxOrdersPerSlot(request.getMaxOrdersPerSlot());
        if (request.getPickupInstructions() != null) settings.setPickupInstructions(request.getPickupInstructions());
        if (request.getAllowCancellation() != null) settings.setAllowCancellation(request.getAllowCancellation());
        if (request.getCancelDeadlineHours() != null) settings.setCancelDeadlineHours(request.getCancelDeadlineHours());
        if (request.getCancelReasonRequired() != null) settings.setCancelReasonRequired(request.getCancelReasonRequired());
        if (request.getCancelPolicyNote() != null) settings.setCancelPolicyNote(request.getCancelPolicyNote());
        if (request.getAutoConfirmEnabled() != null) settings.setAutoConfirmEnabled(request.getAutoConfirmEnabled());

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
