package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.entity.OrderStatusLog;
import com.dine.backend.mapper.OrderStatusLogMapper;
import com.dine.backend.service.OrderStatusLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderStatusLogServiceImpl extends ServiceImpl<OrderStatusLogMapper, OrderStatusLog> implements OrderStatusLogService {

    @Override
    public List<OrderStatusLog> listByOrderId(Long orderId) {
        return list(new LambdaQueryWrapper<OrderStatusLog>()
                .eq(OrderStatusLog::getOrderId, orderId)
                .orderByAsc(OrderStatusLog::getCreatedAt));
    }
}
