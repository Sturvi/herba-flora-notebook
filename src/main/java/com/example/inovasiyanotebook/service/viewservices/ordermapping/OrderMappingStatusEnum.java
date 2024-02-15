package com.example.inovasiyanotebook.service.viewservices.ordermapping;

/**
 * Enum для статусов сопоставления заказов.
 */
public enum OrderMappingStatusEnum {
    ALREADY_MAPPED("Eyniləşdirilmişlər"),
    TO_BE_MAPPED("Eyniləşdirilməmiş"),
    ALL("Hamısı");

    private final String displayName;

    OrderMappingStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
