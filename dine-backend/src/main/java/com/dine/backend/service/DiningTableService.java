package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.DiningTableRequest;
import com.dine.backend.dto.response.DiningTableVO;
import com.dine.backend.entity.DiningTable;
import com.dine.backend.entity.enums.TableStatusEnum;

import java.util.List;

public interface DiningTableService extends IService<DiningTable> {

    List<DiningTable> listByRestaurantId(Long restaurantId);

    List<DiningTable> listBySectionId(Long sectionId);

    List<DiningTableVO> getTables(Long restaurantId, Long sectionId);

    DiningTableVO getTableById(Long restaurantId, Long id);

    DiningTableVO createTable(Long restaurantId, Long sectionId, DiningTableRequest request);

    DiningTableVO updateTable(Long restaurantId, Long id, DiningTableRequest request);

    void deleteTable(Long restaurantId, Long id);

    void updateStatus(Long restaurantId, Long id, TableStatusEnum status);

    void deleteBySectionId(Long sectionId);
}
