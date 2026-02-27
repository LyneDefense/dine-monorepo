package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.OrderTypeConfigRequest;
import com.dine.backend.dto.response.OrderTypeConfigVO;
import com.dine.backend.entity.OrderTypeConfig;
import com.dine.backend.entity.enums.OrderTypeEnum;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.OrderTypeConfigMapper;
import com.dine.backend.service.OrderTypeConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderTypeConfigServiceImpl extends ServiceImpl<OrderTypeConfigMapper, OrderTypeConfig>
        implements OrderTypeConfigService {

    private final EntityConverter converter;

    @Override
    public List<OrderTypeConfigVO> getConfigs(Long restaurantId) {
        List<OrderTypeConfig> configs = list(new LambdaQueryWrapper<OrderTypeConfig>()
                .eq(OrderTypeConfig::getRestaurantId, restaurantId)
                .orderByAsc(OrderTypeConfig::getOrderType));
        return configs.stream()
                .map(converter::toOrderTypeConfigVO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderTypeConfigVO getConfig(Long restaurantId, OrderTypeEnum orderType) {
        OrderTypeConfig config = getOne(new LambdaQueryWrapper<OrderTypeConfig>()
                .eq(OrderTypeConfig::getRestaurantId, restaurantId)
                .eq(OrderTypeConfig::getOrderType, orderType));

        if (config == null) {
            throw BusinessException.notFound("Order type config not found: " + orderType);
        }

        return converter.toOrderTypeConfigVO(config);
    }

    @Override
    @Transactional
    public OrderTypeConfigVO saveOrUpdateConfig(Long restaurantId, OrderTypeConfigRequest request) {
        OrderTypeConfig config = getOne(new LambdaQueryWrapper<OrderTypeConfig>()
                .eq(OrderTypeConfig::getRestaurantId, restaurantId)
                .eq(OrderTypeConfig::getOrderType, request.getOrderType()));

        if (config == null) {
            // 创建新配置
            config = new OrderTypeConfig();
            config.setRestaurantId(restaurantId);
            config.setOrderType(request.getOrderType());
            // 设置默认值
            setDefaultValues(config, request.getOrderType());
        }

        // 复制请求中的非空字段
        copyNonNullProperties(request, config);

        saveOrUpdate(config);

        return converter.toOrderTypeConfigVO(config);
    }

    @Override
    @Transactional
    public OrderTypeConfigVO toggleEnabled(Long restaurantId, OrderTypeEnum orderType, Boolean enabled) {
        OrderTypeConfig config = getOne(new LambdaQueryWrapper<OrderTypeConfig>()
                .eq(OrderTypeConfig::getRestaurantId, restaurantId)
                .eq(OrderTypeConfig::getOrderType, orderType));

        if (config == null) {
            throw BusinessException.notFound("Order type config not found: " + orderType);
        }

        config.setEnabled(enabled);
        updateById(config);

        return converter.toOrderTypeConfigVO(config);
    }

    @Override
    @Transactional
    public void initDefaultConfigs(Long restaurantId) {
        // 检查是否已有配置
        long count = count(new LambdaQueryWrapper<OrderTypeConfig>()
                .eq(OrderTypeConfig::getRestaurantId, restaurantId));

        if (count > 0) {
            return; // 已有配置，不重复初始化
        }

        // 创建 DINE_IN 默认配置
        OrderTypeConfig dineInConfig = createDefaultConfig(restaurantId, OrderTypeEnum.DINE_IN);
        save(dineInConfig);

        // 创建 TAKEOUT 默认配置
        OrderTypeConfig takeoutConfig = createDefaultConfig(restaurantId, OrderTypeEnum.TAKEOUT);
        save(takeoutConfig);
    }

    private OrderTypeConfig createDefaultConfig(Long restaurantId, OrderTypeEnum orderType) {
        OrderTypeConfig config = new OrderTypeConfig();
        config.setRestaurantId(restaurantId);
        config.setOrderType(orderType);
        setDefaultValues(config, orderType);
        return config;
    }

    private void setDefaultValues(OrderTypeConfig config, OrderTypeEnum orderType) {
        // 通用默认值
        config.setEnabled(true);
        config.setLastOrderMinutesBeforeClose(30);
        config.setAutoConfirmEnabled(true);
        config.setAllowCancellation(true);
        config.setCancelDeadlineMinutes(0);
        config.setCancelReasonRequired(false);

        if (orderType == OrderTypeEnum.DINE_IN) {
            // DINE_IN 默认值
            config.setReservationEnabled(true);
            config.setWalkInEnabled(true);
            config.setMaxAdvanceDays(30);
            config.setMinAdvanceMinutes(60);
            config.setMaxReservationsPerSlot(10);
            config.setSectionPreferenceEnabled(true);
            config.setPreOrderEnabled(false);
            config.setPreOrderRequired(false);
            config.setQueueEnabled(false);
            config.setReservationDepositRequired(false);
        } else if (orderType == OrderTypeEnum.TAKEOUT) {
            // TAKEOUT 默认值
            config.setMinPrepMinutes(20);
            config.setScheduledPickupEnabled(true);
            config.setMaxScheduledAdvanceHours(24);
            config.setMaxOrdersPerSlot(20);
            config.setSmsNotificationEnabled(true);
            config.setCallNotificationEnabled(false);
        }
    }

    private void copyNonNullProperties(OrderTypeConfigRequest source, OrderTypeConfig target) {
        // 通用配置
        if (source.getEnabled() != null) target.setEnabled(source.getEnabled());
        if (source.getMinOrderAmount() != null) target.setMinOrderAmount(source.getMinOrderAmount());
        if (source.getLastOrderMinutesBeforeClose() != null) target.setLastOrderMinutesBeforeClose(source.getLastOrderMinutesBeforeClose());
        if (source.getAutoConfirmEnabled() != null) target.setAutoConfirmEnabled(source.getAutoConfirmEnabled());
        if (source.getPeakHourOrderLimit() != null) target.setPeakHourOrderLimit(source.getPeakHourOrderLimit());

        // 取消政策
        if (source.getAllowCancellation() != null) target.setAllowCancellation(source.getAllowCancellation());
        if (source.getCancelDeadlineMinutes() != null) target.setCancelDeadlineMinutes(source.getCancelDeadlineMinutes());
        if (source.getCancelReasonRequired() != null) target.setCancelReasonRequired(source.getCancelReasonRequired());
        if (source.getCancelPolicyNote() != null) target.setCancelPolicyNote(source.getCancelPolicyNote());

        // DINE_IN 专属
        if (source.getReservationEnabled() != null) target.setReservationEnabled(source.getReservationEnabled());
        if (source.getWalkInEnabled() != null) target.setWalkInEnabled(source.getWalkInEnabled());
        if (source.getMaxAdvanceDays() != null) target.setMaxAdvanceDays(source.getMaxAdvanceDays());
        if (source.getMinAdvanceMinutes() != null) target.setMinAdvanceMinutes(source.getMinAdvanceMinutes());
        if (source.getMaxReservationsPerSlot() != null) target.setMaxReservationsPerSlot(source.getMaxReservationsPerSlot());
        if (source.getSectionPreferenceEnabled() != null) target.setSectionPreferenceEnabled(source.getSectionPreferenceEnabled());
        if (source.getPreOrderEnabled() != null) target.setPreOrderEnabled(source.getPreOrderEnabled());
        if (source.getPreOrderRequired() != null) target.setPreOrderRequired(source.getPreOrderRequired());
        if (source.getQueueEnabled() != null) target.setQueueEnabled(source.getQueueEnabled());
        if (source.getEstimatedWaitStrategy() != null) target.setEstimatedWaitStrategy(source.getEstimatedWaitStrategy());
        if (source.getReservationDepositAmount() != null) target.setReservationDepositAmount(source.getReservationDepositAmount());
        if (source.getReservationDepositRequired() != null) target.setReservationDepositRequired(source.getReservationDepositRequired());
        if (source.getNoShowPolicyNote() != null) target.setNoShowPolicyNote(source.getNoShowPolicyNote());

        // TAKEOUT 专属
        if (source.getMinPrepMinutes() != null) target.setMinPrepMinutes(source.getMinPrepMinutes());
        if (source.getScheduledPickupEnabled() != null) target.setScheduledPickupEnabled(source.getScheduledPickupEnabled());
        if (source.getMaxScheduledAdvanceHours() != null) target.setMaxScheduledAdvanceHours(source.getMaxScheduledAdvanceHours());
        if (source.getMaxOrdersPerSlot() != null) target.setMaxOrdersPerSlot(source.getMaxOrdersPerSlot());
        if (source.getPickupInstructions() != null) target.setPickupInstructions(source.getPickupInstructions());
        if (source.getPickupLocationNote() != null) target.setPickupLocationNote(source.getPickupLocationNote());
        if (source.getSmsNotificationEnabled() != null) target.setSmsNotificationEnabled(source.getSmsNotificationEnabled());
        if (source.getCallNotificationEnabled() != null) target.setCallNotificationEnabled(source.getCallNotificationEnabled());
    }
}
