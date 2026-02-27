package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.DiningTableRequest;
import com.dine.backend.dto.response.DiningTableVO;
import com.dine.backend.entity.DiningSection;
import com.dine.backend.entity.DiningTable;
import com.dine.backend.entity.Restaurant;
import com.dine.backend.entity.enums.TableStatusEnum;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.DiningTableMapper;
import com.dine.backend.service.DiningSectionService;
import com.dine.backend.service.DiningTableService;
import com.dine.backend.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiningTableServiceImpl extends ServiceImpl<DiningTableMapper, DiningTable> implements DiningTableService {

    private final EntityConverter converter;
    private final RestaurantService restaurantService;
    private final DiningSectionService diningSectionService;

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
        table.setMergeable(true);
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
}
