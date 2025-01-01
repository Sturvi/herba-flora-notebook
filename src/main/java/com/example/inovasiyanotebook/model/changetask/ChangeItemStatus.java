package com.example.inovasiyanotebook.model.changetask;

public enum ChangeItemStatus {
    PENDING("Ожидает выполнения"),
    DONE("Выполнено"),
    FAILED("Не удалось");

    private final String description;

    ChangeItemStatus(String description) {
        this.description = description;
    }
}
