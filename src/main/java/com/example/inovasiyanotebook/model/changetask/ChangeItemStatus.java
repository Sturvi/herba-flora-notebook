package com.example.inovasiyanotebook.model.changetask;

import lombok.Getter;

public enum ChangeItemStatus {
    PENDING("Edilməyib"),
    DONE("Bitdi");

    @Getter
    private final String description;

    ChangeItemStatus(String description) {
        this.description = description;
    }
}
