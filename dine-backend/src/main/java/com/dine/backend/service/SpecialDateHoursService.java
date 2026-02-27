package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.SpecialDateHoursRequest;
import com.dine.backend.dto.response.SpecialDateHoursVO;
import com.dine.backend.entity.SpecialDateHours;

import java.time.LocalDate;
import java.util.List;

public interface SpecialDateHoursService extends IService<SpecialDateHours> {

    List<SpecialDateHours> listByRestaurantId(Long restaurantId);

    List<SpecialDateHours> listByRestaurantIdAndDate(Long restaurantId, LocalDate date);

    List<SpecialDateHoursVO> getSpecialDateHours(Long restaurantId);

    SpecialDateHoursVO createSpecialDateHours(Long restaurantId, SpecialDateHoursRequest request);

    SpecialDateHoursVO updateSpecialDateHours(Long restaurantId, Long id, SpecialDateHoursRequest request);

    void deleteSpecialDateHours(Long restaurantId, Long id);
}
