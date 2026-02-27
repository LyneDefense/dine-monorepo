package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.OrderCreateRequest;
import com.dine.backend.dto.request.OrderItemRequest;
import com.dine.backend.dto.response.OrderListVO;
import com.dine.backend.dto.response.OrderVO;
import com.dine.backend.entity.*;
import com.dine.backend.entity.enums.OrderStatusEnum;
import com.dine.backend.entity.enums.OrderTypeEnum;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.OrderMapper;
import com.dine.backend.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final EntityConverter converter;
    private final OrderItemService orderItemService;
    private final OrderStatusLogService orderStatusLogService;
    private final DiningTableService diningTableService;
    private final MenuItemService menuItemService;
    private final RestaurantSettingsService settingsService;
    private final RestaurantService restaurantService;
    private final ComboService comboService;

    private void validateRestaurantExists(Long restaurantId) {
        Restaurant restaurant = restaurantService.getById(restaurantId);
        if (restaurant == null) {
            throw BusinessException.notFound("Restaurant not found: " + restaurantId);
        }
    }

    private void validateMenuItemAvailable(Long restaurantId, Long itemId) {
        MenuItem item = menuItemService.getById(itemId);
        if (item == null) {
            throw BusinessException.notFound("Menu item not found: " + itemId);
        }
        if (!item.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.badRequest("Menu item does not belong to this restaurant");
        }
        if (!menuItemService.isAvailable(restaurantId, itemId)) {
            throw BusinessException.badRequest("Menu item '" + item.getName() + "' is not available");
        }
    }

    private void validateComboAvailable(Long restaurantId, Long comboId) {
        if (comboId == null) return;
        Combo combo = comboService.getById(comboId);
        if (combo == null) {
            throw BusinessException.notFound("Combo not found: " + comboId);
        }
        if (!combo.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.badRequest("Combo does not belong to this restaurant");
        }
        if (!comboService.isAvailable(restaurantId, comboId)) {
            throw BusinessException.badRequest("Combo '" + combo.getName() + "' is not available");
        }
    }

    @Override
    public List<Order> listByRestaurantId(Long restaurantId) {
        return list(new LambdaQueryWrapper<Order>()
                .eq(Order::getRestaurantId, restaurantId)
                .orderByDesc(Order::getCreatedAt));
    }

    @Override
    public Order getByOrderNumber(String orderNumber) {
        return getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNumber, orderNumber));
    }

    @Override
    public IPage<OrderListVO> getOrders(Long restaurantId, OrderTypeEnum orderType, OrderStatusEnum status,
                                         LocalDate date, String keyword, Integer page, Integer size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getRestaurantId, restaurantId)
                .eq(orderType != null, Order::getType, orderType)
                .eq(status != null, Order::getStatus, status)
                .ge(date != null, Order::getCreatedAt, date != null ? date.atStartOfDay() : null)
                .lt(date != null, Order::getCreatedAt, date != null ? date.plusDays(1).atStartOfDay() : null)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(Order::getOrderNumber, keyword)
                        .or().like(Order::getCustomerName, keyword)
                        .or().like(Order::getCustomerPhone, keyword))
                .orderByDesc(Order::getCreatedAt);

        IPage<Order> orderPage = page(new Page<>(page, size), wrapper);

        return orderPage.convert(order -> {
            Integer itemCount = orderItemService.countByOrderId(order.getId());
            return converter.toOrderListVO(order, null, itemCount);
        });
    }

    @Override
    public OrderVO getOrderById(Long restaurantId, Long id) {
        Order order = getById(id);
        if (order == null || !order.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Order not found");
        }
        return buildOrderVO(order);
    }

    @Override
    public OrderVO getOrderByNumber(Long restaurantId, String orderNumber) {
        Order order = getByOrderNumber(orderNumber);
        if (order == null || !order.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Order not found");
        }
        return buildOrderVO(order);
    }

    private OrderVO buildOrderVO(Order order) {
        List<OrderItem> items = orderItemService.listByOrderId(order.getId());
        List<OrderStatusLog> logs = orderStatusLogService.listByOrderId(order.getId());
        return converter.toOrderVO(order, items, logs, null);
    }

    @Override
    @Transactional
    public OrderVO createOrder(Long restaurantId, OrderCreateRequest request) {
        // 验证餐厅存在
        validateRestaurantExists(restaurantId);

        // 验证订单项不为空
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw BusinessException.badRequest("Order must have at least one item");
        }

        // 验证每个订单项的菜品/套餐可用
        for (OrderItemRequest itemReq : request.getItems()) {
            if (itemReq.getMenuItemId() != null) {
                validateMenuItemAvailable(restaurantId, itemReq.getMenuItemId());
            }
            if (itemReq.getComboId() != null) {
                validateComboAvailable(restaurantId, itemReq.getComboId());
            }
            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw BusinessException.badRequest("Item quantity must be positive");
            }
        }

        // Create order
        Order order = new Order();
        order.setRestaurantId(restaurantId);
        order.setOrderNumber(generateOrderNumber());
        order.setType(request.getOrderType());
        order.setStatus(OrderStatusEnum.PENDING);
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setPartySize(request.getPartySize());
        order.setCustomerNotes(request.getSpecialInstructions());

        // Handle scheduled time
        if (request.getScheduledTime() != null) {
            if (request.getOrderType() == OrderTypeEnum.DINE_IN) {
                order.setReservationDate(request.getScheduledTime().toLocalDate());
                order.setReservationTime(request.getScheduledTime().toLocalTime());
            } else {
                order.setPickupTime(request.getScheduledTime());
            }
        }

        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            BigDecimal unitPrice = itemReq.getUnitPrice();
            if (unitPrice == null) {
                MenuItem menuItem = menuItemService.getById(itemReq.getMenuItemId());
                if (menuItem != null) {
                    unitPrice = menuItem.getPrice();
                } else {
                    unitPrice = BigDecimal.ZERO;
                }
            }
            subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }

        order.setSubtotal(subtotal);

        // Get tax rate from settings
        RestaurantSettings settings = settingsService.getByRestaurantId(restaurantId);
        BigDecimal taxRate = settings != null && settings.getTaxRate() != null
                ? settings.getTaxRate() : BigDecimal.ZERO;
        BigDecimal tax = subtotal.multiply(taxRate);
        order.setTax(tax);
        order.setTotal(subtotal.add(tax));

        save(order);

        log.info("Order created: orderNumber={}, restaurantId={}, type={}, total={}",
                order.getOrderNumber(), restaurantId, order.getType(), order.getTotal());

        // Create order items
        for (OrderItemRequest itemReq : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setItemId(itemReq.getMenuItemId());
            orderItem.setComboId(itemReq.getComboId());
            orderItem.setQuantity(itemReq.getQuantity());

            BigDecimal unitPrice = itemReq.getUnitPrice();
            if (unitPrice == null) {
                MenuItem menuItem = menuItemService.getById(itemReq.getMenuItemId());
                if (menuItem != null) {
                    unitPrice = menuItem.getPrice();
                } else {
                    unitPrice = BigDecimal.ZERO;
                }
            }
            orderItem.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            orderItem.setItemNote(itemReq.getSpecialInstructions());

            orderItemService.save(orderItem);
        }

        // Log status
        logStatusChange(order.getId(), null, OrderStatusEnum.PENDING, "Order created");

        // Auto-confirm if enabled
        if (settings != null && Boolean.TRUE.equals(settings.getAutoConfirmEnabled())) {
            return confirmOrder(restaurantId, order.getId());
        }

        return getOrderById(restaurantId, order.getId());
    }

    @Override
    @Transactional
    public OrderVO updateStatus(Long restaurantId, Long id, OrderStatusEnum status, String reason, Long changedBy) {
        Order order = getById(id);
        if (order == null || !order.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Order not found");
        }

        OrderStatusEnum fromStatus = order.getStatus();
        order.setStatus(status);

        if (status == OrderStatusEnum.CANCELLED) {
            order.setCancelReason(reason);
        }

        updateById(order);
        logStatusChange(id, fromStatus, status, reason);

        log.info("Order status updated: orderId={}, {} -> {}", id, fromStatus, status);

        return getOrderById(restaurantId, id);
    }

    @Override
    @Transactional
    public OrderVO confirmOrder(Long restaurantId, Long id) {
        Order order = getById(id);
        if (order == null || !order.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Order not found");
        }

        if (order.getStatus() != OrderStatusEnum.PENDING) {
            throw BusinessException.badRequest("Order cannot be confirmed");
        }

        OrderStatusEnum fromStatus = order.getStatus();
        order.setStatus(OrderStatusEnum.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());
        updateById(order);

        logStatusChange(id, fromStatus, OrderStatusEnum.CONFIRMED, "Order confirmed");

        log.info("Order confirmed: orderId={}, orderNumber={}", id, order.getOrderNumber());

        return getOrderById(restaurantId, id);
    }

    @Override
    @Transactional
    public OrderVO cancelOrder(Long restaurantId, Long id, String reason) {
        Order order = getById(id);
        if (order == null || !order.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Order not found");
        }

        if (order.getStatus() == OrderStatusEnum.COMPLETED || order.getStatus() == OrderStatusEnum.CANCELLED) {
            throw BusinessException.badRequest("Order cannot be cancelled");
        }

        OrderStatusEnum fromStatus = order.getStatus();
        order.setStatus(OrderStatusEnum.CANCELLED);
        order.setCancelReason(reason);
        updateById(order);

        logStatusChange(id, fromStatus, OrderStatusEnum.CANCELLED, reason);

        log.warn("Order cancelled: orderId={}, orderNumber={}, reason={}", id, order.getOrderNumber(), reason);

        return getOrderById(restaurantId, id);
    }

    @Override
    @Transactional
    public OrderVO completeOrder(Long restaurantId, Long id) {
        Order order = getById(id);
        if (order == null || !order.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Order not found");
        }

        if (order.getStatus() != OrderStatusEnum.CONFIRMED) {
            throw BusinessException.badRequest("Order cannot be completed");
        }

        OrderStatusEnum fromStatus = order.getStatus();
        order.setStatus(OrderStatusEnum.COMPLETED);
        updateById(order);

        logStatusChange(id, fromStatus, OrderStatusEnum.COMPLETED, "Order completed");

        log.info("Order completed: orderId={}, orderNumber={}", id, order.getOrderNumber());

        return getOrderById(restaurantId, id);
    }

    @Override
    public Integer getPendingCount(Long restaurantId) {
        return Math.toIntExact(count(new LambdaQueryWrapper<Order>()
                .eq(Order::getRestaurantId, restaurantId)
                .eq(Order::getStatus, OrderStatusEnum.PENDING)));
    }

    private String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return date + "-" + random;
    }

    private void logStatusChange(Long orderId, OrderStatusEnum fromStatus, OrderStatusEnum toStatus, String reason) {
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setReason(reason);
        orderStatusLogService.save(log);
    }
}
