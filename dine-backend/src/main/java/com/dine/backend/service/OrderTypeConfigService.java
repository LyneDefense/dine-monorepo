package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.OrderTypeConfigRequest;
import com.dine.backend.dto.response.OrderTypeConfigVO;
import com.dine.backend.entity.OrderTypeConfig;
import com.dine.backend.entity.enums.OrderTypeEnum;

import java.util.List;

public interface OrderTypeConfigService extends IService<OrderTypeConfig> {

    /**
     * 获取餐厅的所有订单类型配置
     */
    List<OrderTypeConfigVO> getConfigs(Long restaurantId);

    /**
     * 获取餐厅指定类型的配置
     */
    OrderTypeConfigVO getConfig(Long restaurantId, OrderTypeEnum orderType);

    /**
     * 创建或更新订单类型配置
     */
    OrderTypeConfigVO saveOrUpdateConfig(Long restaurantId, OrderTypeConfigRequest request);

    /**
     * 切换订单类型启用状态
     */
    OrderTypeConfigVO toggleEnabled(Long restaurantId, OrderTypeEnum orderType, Boolean enabled);

    /**
     * 初始化餐厅的订单类型配置（创建默认配置）
     */
    void initDefaultConfigs(Long restaurantId);
}
