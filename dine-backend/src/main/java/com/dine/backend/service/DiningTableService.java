package com.dine.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dine.backend.dto.request.DiningTableRequest;
import com.dine.backend.dto.response.DiningTableVO;
import com.dine.backend.dto.response.TableAvailabilityVO;
import com.dine.backend.entity.DiningTable;
import com.dine.backend.entity.enums.TableStatusEnum;

import java.time.LocalDate;
import java.time.LocalTime;
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

    /**
     * 检查指定时间段的餐桌可用性
     * @param restaurantId 餐厅ID
     * @param date 预约日期
     * @param time 预约时间
     * @param partySize 用餐人数
     * @return 可用性信息及可用餐桌列表
     */
    TableAvailabilityVO checkAvailability(Long restaurantId, LocalDate date, LocalTime time, Integer partySize);
}
