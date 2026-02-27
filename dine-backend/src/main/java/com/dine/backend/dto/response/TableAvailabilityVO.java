package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import lombok.Data;

import java.util.List;

@Data
public class TableAvailabilityVO {

    private Boolean available;
    private List<AvailableTable> availableTables;

    @Data
    public static class AvailableTable {
        @LongToString
        private Long tableId;
        private String tableNumber;
        private Integer capacity;
        private String sectionName;
    }

    public static TableAvailabilityVO available(List<AvailableTable> tables) {
        TableAvailabilityVO vo = new TableAvailabilityVO();
        vo.setAvailable(!tables.isEmpty());
        vo.setAvailableTables(tables);
        return vo;
    }
}
