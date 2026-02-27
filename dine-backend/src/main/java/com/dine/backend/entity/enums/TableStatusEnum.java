package com.dine.backend.entity.enums;

public enum TableStatusEnum {
    AVAILABLE,    // 空闲可用
    RESERVED,     // 已预约（客人未到）
    OCCUPIED,     // 正在使用中
    MAINTENANCE   // 维护中
}
