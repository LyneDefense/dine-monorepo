package com.dine.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.OrderCreateRequest;
import com.dine.backend.dto.response.OrderListVO;
import com.dine.backend.dto.response.OrderVO;
import com.dine.backend.entity.Order;
import com.dine.backend.entity.enums.OrderStatusEnum;
import com.dine.backend.entity.enums.OrderTypeEnum;

import java.time.LocalDate;
import java.util.List;

public interface OrderService extends IService<Order> {

    List<Order> listByRestaurantId(Long restaurantId);

    Order getByOrderNumber(String orderNumber);

    IPage<OrderListVO> getOrders(Long restaurantId, OrderTypeEnum orderType, OrderStatusEnum status,
                                  LocalDate date, String keyword, Integer page, Integer size);

    OrderVO getOrderById(Long restaurantId, Long id);

    OrderVO getOrderByNumber(Long restaurantId, String orderNumber);

    OrderVO createOrder(Long restaurantId, OrderCreateRequest request);

    OrderVO updateStatus(Long restaurantId, Long id, OrderStatusEnum status, String reason, Long changedBy);

    OrderVO confirmOrder(Long restaurantId, Long id);

    OrderVO cancelOrder(Long restaurantId, Long id, String reason);

    OrderVO completeOrder(Long restaurantId, Long id);

    Integer getPendingCount(Long restaurantId);
}
