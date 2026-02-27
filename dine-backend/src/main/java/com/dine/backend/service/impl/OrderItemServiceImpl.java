package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.entity.OrderItem;
import com.dine.backend.mapper.OrderItemMapper;
import com.dine.backend.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Override
    public List<OrderItem> listByOrderId(Long orderId) {
        return list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
    }

    @Override
    public Integer countByOrderId(Long orderId) {
        return Math.toIntExact(count(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)));
    }
}
