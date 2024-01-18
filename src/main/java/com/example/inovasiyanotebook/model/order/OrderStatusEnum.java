package com.example.inovasiyanotebook.model.order;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    OPEN ("Açıq"),
    WAITING ("Gözləmədə"),
    COMPLETE ("Bitdi"),
    CANCELED ("Ləğv edildi");

    private final String name;

    OrderStatusEnum(String name) {
        this.name = name;
    }
}
