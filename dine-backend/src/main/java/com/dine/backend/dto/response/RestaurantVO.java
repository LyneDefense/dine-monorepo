package com.dine.backend.dto.response;

import com.dine.backend.common.LongToString;
import com.dine.backend.entity.enums.RestaurantStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RestaurantVO {

    @LongToString
    private Long id;

    private String name;

    private String description;

    private String address;

    private String phone;

    private String timezone;

    private String logo;

    private List<String> images;

    private RestaurantStatusEnum status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
