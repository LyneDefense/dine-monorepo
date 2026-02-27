package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.ComboRequest;
import com.dine.backend.dto.response.ComboVO;
import com.dine.backend.entity.Combo;

import java.util.List;

public interface ComboService extends IService<Combo> {

    List<Combo> listByRestaurantId(Long restaurantId);

    List<ComboVO> getCombos(Long restaurantId);

    ComboVO getComboById(Long restaurantId, Long id);

    ComboVO createCombo(Long restaurantId, ComboRequest request);

    ComboVO updateCombo(Long restaurantId, Long id, ComboRequest request);

    void deleteCombo(Long restaurantId, Long id);

    void updateAvailability(Long restaurantId, Long id, Boolean available);

    boolean isAvailable(Long restaurantId, Long comboId);
}
