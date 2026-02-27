package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.entity.OrderItem;

import java.util.List;

public interface OrderItemService extends IService<OrderItem> {

    List<OrderItem> listByOrderId(Long orderId);

    Integer countByOrderId(Long orderId);
}
