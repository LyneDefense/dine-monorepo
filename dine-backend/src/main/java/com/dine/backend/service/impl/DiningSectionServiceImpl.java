package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.DiningSectionRequest;
import com.dine.backend.dto.response.DiningSectionVO;
import com.dine.backend.entity.DiningSection;
import com.dine.backend.entity.DiningTable;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.entity.Restaurant;
import com.dine.backend.mapper.DiningSectionMapper;
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
public class DiningSectionServiceImpl extends ServiceImpl<DiningSectionMapper, DiningSection> implements DiningSectionService {

    private final EntityConverter converter;
    private final DiningTableService diningTableService;
    private final RestaurantService restaurantService;

    private void validateRestaurantExists(Long restaurantId) {
        Restaurant restaurant = restaurantService.getById(restaurantId);
        if (restaurant == null) {
            throw BusinessException.notFound("Restaurant not found: " + restaurantId);
        }
    }

    private void validateSectionNameUnique(Long restaurantId, String name, Long excludeId) {
        LambdaQueryWrapper<DiningSection> wrapper = new LambdaQueryWrapper<DiningSection>()
                .eq(DiningSection::getRestaurantId, restaurantId)
                .eq(DiningSection::getName, name);
        if (excludeId != null) {
            wrapper.ne(DiningSection::getId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw BusinessException.badRequest("Section with name '" + name + "' already exists");
        }
    }

    @Override
    public List<DiningSection> listByRestaurantId(Long restaurantId) {
        return list(new LambdaQueryWrapper<DiningSection>()
                .eq(DiningSection::getRestaurantId, restaurantId));
    }

    @Override
    public List<DiningSectionVO> getSections(Long restaurantId) {
        validateRestaurantExists(restaurantId);
        List<DiningSection> sections = listByRestaurantId(restaurantId);
        return sections.stream().map(section -> {
            List<DiningTable> tables = diningTableService.listBySectionId(section.getId());
            return converter.toDiningSectionVO(section, tables);
        }).collect(Collectors.toList());
    }

    @Override
    public DiningSectionVO getSectionById(Long restaurantId, Long id) {
        validateRestaurantExists(restaurantId);
        DiningSection section = getById(id);
        if (section == null || !section.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Section not found");
        }
        List<DiningTable> tables = diningTableService.listBySectionId(id);
        return converter.toDiningSectionVO(section, tables);
    }

    @Override
    @Transactional
    public DiningSectionVO createSection(Long restaurantId, DiningSectionRequest request) {
        validateRestaurantExists(restaurantId);
        validateSectionNameUnique(restaurantId, request.getName(), null);

        DiningSection section = new DiningSection();
        section.setRestaurantId(restaurantId);
        section.setName(request.getName());
        section.setNote(request.getDescription());
        section.setSmoking(false);
        save(section);

        log.info("Section created: id={}, name={}, restaurantId={}", section.getId(), section.getName(), restaurantId);

        return converter.toDiningSectionVO(section);
    }

    @Override
    @Transactional
    public DiningSectionVO updateSection(Long restaurantId, Long id, DiningSectionRequest request) {
        validateRestaurantExists(restaurantId);
        DiningSection section = getById(id);
        if (section == null || !section.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Section not found");
        }

        // 检查名称唯一性（排除自身）
        if (request.getName() != null && !request.getName().equals(section.getName())) {
            validateSectionNameUnique(restaurantId, request.getName(), id);
        }

        section.setName(request.getName());
        section.setNote(request.getDescription());
        updateById(section);

        List<DiningTable> tables = diningTableService.listBySectionId(id);
        return converter.toDiningSectionVO(section, tables);
    }

    @Override
    @Transactional
    public void deleteSection(Long restaurantId, Long id) {
        validateRestaurantExists(restaurantId);
        DiningSection section = getById(id);
        if (section == null || !section.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Section not found");
        }

        // 检查区域下是否有餐桌
        List<DiningTable> tables = diningTableService.listBySectionId(id);
        if (!tables.isEmpty()) {
            throw BusinessException.badRequest("Cannot delete section with " + tables.size() + " tables. Please delete tables first.");
        }

        removeById(id);

        log.info("Section deleted: id={}, name={}", id, section.getName());
    }
}
