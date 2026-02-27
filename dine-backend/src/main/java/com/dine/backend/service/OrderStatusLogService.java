package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.entity.OrderStatusLog;

import java.util.List;

public interface OrderStatusLogService extends IService<OrderStatusLog> {

    List<OrderStatusLog> listByOrderId(Long orderId);
}
