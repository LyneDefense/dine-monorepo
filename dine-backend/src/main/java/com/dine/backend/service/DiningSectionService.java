package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.DiningSectionRequest;
import com.dine.backend.dto.response.DiningSectionVO;
import com.dine.backend.entity.DiningSection;

import java.util.List;

public interface DiningSectionService extends IService<DiningSection> {

    List<DiningSection> listByRestaurantId(Long restaurantId);

    List<DiningSectionVO> getSections(Long restaurantId);

    DiningSectionVO getSectionById(Long restaurantId, Long id);

    DiningSectionVO createSection(Long restaurantId, DiningSectionRequest request);

    DiningSectionVO updateSection(Long restaurantId, Long id, DiningSectionRequest request);

    void deleteSection(Long restaurantId, Long id);
}
