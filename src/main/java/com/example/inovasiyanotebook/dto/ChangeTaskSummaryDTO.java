package com.example.inovasiyanotebook.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Строка грида задач на изменение: агрегаты по позициям считаются в БД.
 */
public record ChangeTaskSummaryDTO(Long id,
                                   String taskType,
                                   LocalDateTime createdAt,
                                   Long total,
                                   Long done,
                                   Long pending,
                                   LocalDateTime lastCompletedAt) {

    public boolean isFinished() {
        return pending != null && pending == 0;
    }

    public LocalDate finishedDate() {
        return isFinished() && lastCompletedAt != null ? lastCompletedAt.toLocalDate() : null;
    }

    public LocalDate startedDate() {
        return createdAt != null ? createdAt.toLocalDate() : null;
    }
}
