package com.dine.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dine.backend.converter.EntityConverter;
import com.dine.backend.dto.request.SpecialDateHoursRequest;
import com.dine.backend.dto.response.SpecialDateHoursVO;
import com.dine.backend.entity.SpecialDateHours;
import com.dine.backend.exception.BusinessException;
import com.dine.backend.mapper.SpecialDateHoursMapper;
import com.dine.backend.service.SpecialDateHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialDateHoursServiceImpl extends ServiceImpl<SpecialDateHoursMapper, SpecialDateHours> implements SpecialDateHoursService {

    private final EntityConverter converter;

    @Override
    public List<SpecialDateHours> listByRestaurantId(Long restaurantId) {
        return list(new LambdaQueryWrapper<SpecialDateHours>()
                .eq(SpecialDateHours::getRestaurantId, restaurantId)
                .orderByAsc(SpecialDateHours::getDate));
    }

    @Override
    public List<SpecialDateHours> listByRestaurantIdAndDate(Long restaurantId, LocalDate date) {
        return list(new LambdaQueryWrapper<SpecialDateHours>()
                .eq(SpecialDateHours::getRestaurantId, restaurantId)
                .eq(SpecialDateHours::getDate, date));
    }

    @Override
    public List<SpecialDateHoursVO> getSpecialDateHours(Long restaurantId) {
        List<SpecialDateHours> hours = listByRestaurantId(restaurantId);
        return converter.toSpecialDateHoursVOList(hours);
    }

    @Override
    @Transactional
    public SpecialDateHoursVO createSpecialDateHours(Long restaurantId, SpecialDateHoursRequest request) {
        SpecialDateHours hours = new SpecialDateHours();
        BeanUtils.copyProperties(request, hours);
        hours.setRestaurantId(restaurantId);
        save(hours);
        return converter.toSpecialDateHoursVO(hours);
    }

    @Override
    @Transactional
    public SpecialDateHoursVO updateSpecialDateHours(Long restaurantId, Long id, SpecialDateHoursRequest request) {
        SpecialDateHours hours = getById(id);
        if (hours == null || !hours.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Special date hours not found");
        }

        if (request.getDate() != null) hours.setDate(request.getDate());
        if (request.getIsClosed() != null) hours.setIsClosed(request.getIsClosed());
        if (request.getOpenTime() != null) hours.setOpenTime(request.getOpenTime());
        if (request.getCloseTime() != null) hours.setCloseTime(request.getCloseTime());
        if (request.getNote() != null) hours.setNote(request.getNote());

        updateById(hours);
        return converter.toSpecialDateHoursVO(hours);
    }

    @Override
    @Transactional
    public void deleteSpecialDateHours(Long restaurantId, Long id) {
        SpecialDateHours hours = getById(id);
        if (hours == null || !hours.getRestaurantId().equals(restaurantId)) {
            throw BusinessException.notFound("Special date hours not found");
        }
        removeById(id);
    }
}
