package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.DiningTableRequest;
import com.dine.backend.dto.response.DiningTableVO;
import com.dine.backend.dto.response.TableAvailabilityVO;
import com.dine.backend.entity.*;
import com.dine.backend.entity.enums.OrderStatusEnum;
import com.dine.backend.entity.enums.OrderTypeEnum;
import com.dine.backend.entity.enums.TableStatusEnum;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.DiningTableMapper;
import com.dine.backend.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DiningTableServiceImpl extends ServiceImpl<DiningTableMapper, DiningTable> implements DiningTableService {

    private final EntityConverter converter;
    private final RestaurantService restaurantService;
    private final DiningSectionService diningSectionService;
    private final OrderService orderService;
    private final OrderTypeConfigService orderTypeConfigService;

    public DiningTableServiceImpl(
            EntityConverter converter,
            RestaurantService restaurantService,
            @Lazy DiningSectionService diningSectionService,
            @Lazy OrderService orderService,
            @Lazy OrderTypeConfigService orderTypeConfigService) {
        this.converter = converter;
        this.restaurantService = restaurantService;
        this.diningSectionService = diningSectionService;
        this.orderService = orderService;
        this.orderTypeConfigService = orderTypeConfigService;
    }

    private void validateRestaurantExists(Long restaurantId) {
        Restaurant restaurant = restaurantService.getById(restaurantId);
        if (restaurant == null) {
            throw BusinessException.notFound("Restaurant not found: " + restaurantId);
        }
    }

    private void validateSectionExists(Long restaurantId, Long sectionId) {
        DiningSection section = diningSectionService.getById(sectionId);
        if (section == null) {
            throw BusinessException.notFound("Section not found: " + sectionId);
        }
        if (!section.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.badRequest("Section does not belong to this restaurant");
        }
    }

    private void validateTableRequest(DiningTableRequest request) {
        if (request.getCapacity() != null && request.getCapacity() <= 0) {
            throw BusinessException.badRequest("Table capacity must be positive");
        }
    }

    private void validateTableNumberUnique(Long sectionId, String tableNumber, Long excludeId) {
        LambdaQueryWrapper<DiningTable> wrapper = new LambdaQueryWrapper<DiningTable>()
                .eq(DiningTable::getSectionId, sectionId)
                .eq(DiningTable::getTableNumber, tableNumber);
        if (excludeId != null) {
            wrapper.ne(DiningTable::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw BusinessException.badRequest("Table with number '" + tableNumber + "' already exists in this section");
        }
    }

    @Override
    public List<DiningTable> listByRestaurantId(Long restaurantId) {
        return list(new LambdaQueryWrapper<DiningTable>()
                .eq(DiningTable::getRestaurantId, restaurantId));
    }

    @Override
    public List<DiningTable> listBySectionId(Long sectionId) {
        return list(new LambdaQueryWrapper<DiningTable>()
                .eq(DiningTable::getSectionId, sectionId));
    }

    @Override
    public List<DiningTableVO> getTables(Long restaurantId, Long sectionId) {
        validateRestaurantExists(restaurantId);
        if (sectionId != null) {
            validateSectionExists(restaurantId, sectionId);
        }

        LambdaQueryWrapper<DiningTable> wrapper = new LambdaQueryWrapper<DiningTable>()
                .eq(DiningTable::getRestaurantId, restaurantId);
        if (sectionId != null) {
            wrapper.eq(DiningTable::getSectionId, sectionId);
        }
        List<DiningTable> tables = list(wrapper);
        return tables.stream().map(converter::toDiningTableVO).collect(Collectors.toList());
    }

    @Override
    public DiningTableVO getTableById(Long restaurantId, Long id) {
        validateRestaurantExists(restaurantId);
        DiningTable table = getById(id);
        if (table == null || !table.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Table not found");
        }
        return converter.toDiningTableVO(table);
    }

    @Override
    @Transactional
    public DiningTableVO createTable(Long restaurantId, Long sectionId, DiningTableRequest request) {
        validateRestaurantExists(restaurantId);
        validateSectionExists(restaurantId, sectionId);
        validateTableRequest(request);
        validateTableNumberUnique(sectionId, request.getName(), null);

        DiningTable table = new DiningTable();
        table.setRestaurantId(restaurantId);
        table.setSectionId(sectionId);
        table.setTableNumber(request.getName());
        table.setCapacity(request.getCapacity());
        table.setStatus(request.getStatus() != null ? request.getStatus() : TableStatusEnum.AVAILABLE);
        save(table);

        log.info("Table created: id={}, number={}, sectionId={}", table.getId(), table.getTableNumber(), sectionId);

        return converter.toDiningTableVO(table);
    }

    @Override
    @Transactional
    public DiningTableVO updateTable(Long restaurantId, Long id, DiningTableRequest request) {
        validateRestaurantExists(restaurantId);
        validateTableRequest(request);

        DiningTable table = getById(id);
        if (table == null || !table.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Table not found");
        }

        // 检查桌号唯一性（排除自身）
        if (request.getName() != null && !request.getName().equals(table.getTableNumber())) {
            validateTableNumberUnique(table.getSectionId(), request.getName(), id);
        }

        table.setTableNumber(request.getName());
        table.setCapacity(request.getCapacity());
        if (request.getStatus() != null) {
            table.setStatus(request.getStatus());
        }
        updateById(table);
        return converter.toDiningTableVO(table);
    }

    @Override
    @Transactional
    public void deleteTable(Long restaurantId, Long id) {
        validateRestaurantExists(restaurantId);
        DiningTable table = getById(id);
        if (table == null || !table.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Table not found");
        }
        removeById(id);

        log.info("Table deleted: id={}, number={}", id, table.getTableNumber());
    }

    @Override
    @Transactional
    public void updateStatus(Long restaurantId, Long id, TableStatusEnum status) {
        validateRestaurantExists(restaurantId);
        DiningTable table = getById(id);
        if (table == null || !table.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Table not found");
        }
        table.setStatus(status);
        updateById(table);

        log.info("Table status updated: id={}, status={}", id, status);
    }

    @Override
    @Transactional
    public void deleteBySectionId(Long sectionId) {
        remove(new LambdaQueryWrapper<DiningTable>()
                .eq(DiningTable::getSectionId, sectionId));
    }

    @Override
    public TableAvailabilityVO checkAvailability(Long restaurantId, LocalDate date, LocalTime time, Integer partySize) {
        validateRestaurantExists(restaurantId);

        // 获取用餐时长设置（默认120分钟）
        int diningDurationMinutes = 120;
        try {
            OrderTypeConfig dineInConfig = orderTypeConfigService.getOne(
                    new LambdaQueryWrapper<OrderTypeConfig>()
                            .eq(OrderTypeConfig::getRestaurantId, restaurantId)
                            .eq(OrderTypeConfig::getOrderType, OrderTypeEnum.DINE_IN));
            if (dineInConfig != null && dineInConfig.getDefaultDiningDurationMinutes() != null) {
                diningDurationMinutes = dineInConfig.getDefaultDiningDurationMinutes();
            }
        } catch (Exception e) {
            log.warn("Failed to get dining duration config, using default: {}", e.getMessage());
        }

        // 计算时间范围
        // 如果请求时间是 18:00，用餐时长 2 小时
        // 则冲突的预约是：预约时间在 16:00-20:00 之间的（即 18:00 前后各2小时）
        LocalTime rangeStart = time.minusMinutes(diningDurationMinutes);
        LocalTime rangeEnd = time.plusMinutes(diningDurationMinutes);

        // 查询该时段有预约的订单，获取占用的桌子ID列表
        List<Order> conflictingOrders = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getRestaurantId, restaurantId)
                .eq(Order::getType, OrderTypeEnum.DINE_IN)
                .eq(Order::getReservationDate, date)
                .isNotNull(Order::getTableId)
                .notIn(Order::getStatus, OrderStatusEnum.CANCELLED, OrderStatusEnum.COMPLETED)
                .and(wrapper -> wrapper
                        .ge(Order::getReservationTime, rangeStart)
                        .lt(Order::getReservationTime, rangeEnd)));

        Set<Long> occupiedTableIds = conflictingOrders.stream()
                .map(Order::getTableId)
                .collect(Collectors.toSet());

        // 查询所有可用的餐桌（容量足够，状态为 AVAILABLE，且不在占用列表中）
        LambdaQueryWrapper<DiningTable> tableWrapper = new LambdaQueryWrapper<DiningTable>()
                .eq(DiningTable::getRestaurantId, restaurantId)
                .ge(DiningTable::getCapacity, partySize)
                .eq(DiningTable::getStatus, TableStatusEnum.AVAILABLE);

        if (!occupiedTableIds.isEmpty()) {
            tableWrapper.notIn(DiningTable::getId, occupiedTableIds);
        }

        List<DiningTable> availableTables = list(tableWrapper);

        // 获取分区信息用于返回
        List<DiningSection> sections = diningSectionService.list(new LambdaQueryWrapper<DiningSection>()
                .eq(DiningSection::getRestaurantId, restaurantId));
        var sectionMap = sections.stream()
                .collect(Collectors.toMap(DiningSection::getId, DiningSection::getName));

        // 构建响应
        List<TableAvailabilityVO.AvailableTable> result = new ArrayList<>();
        for (DiningTable table : availableTables) {
            TableAvailabilityVO.AvailableTable availableTable = new TableAvailabilityVO.AvailableTable();
            availableTable.setTableId(table.getId());
            availableTable.setTableNumber(table.getTableNumber());
            availableTable.setCapacity(table.getCapacity());
            availableTable.setSectionName(sectionMap.get(table.getSectionId()));
            result.add(availableTable);
        }

        log.info("Availability check: restaurantId={}, date={}, time={}, partySize={}, availableTables={}",
                restaurantId, date, time, partySize, result.size());

        return TableAvailabilityVO.available(result);
    }
}
