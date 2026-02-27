package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.OperatingHoursRequest;
import com.dine.backend.dto.response.OperatingHoursVO;
import com.dine.backend.entity.OperatingHours;

import java.util.List;

public interface OperatingHoursService extends IService<OperatingHours> {

    List<OperatingHours> listByRestaurantId(Long restaurantId);

    List<OperatingHoursVO> getOperatingHours(Long restaurantId);

    List<OperatingHoursVO> updateOperatingHours(Long restaurantId, List<OperatingHoursRequest> requests);
}
