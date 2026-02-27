package com.dine.backend.dto.request;

import com.dine.backend.entity.enums.RestaurantStatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class RestaurantUpdateRequest {

    private String name;

    private String description;

    private String address;

    private String phone;

    private String timezone;

    private String logo;

    private List<String> images;

    private RestaurantStatusEnum status;
}
