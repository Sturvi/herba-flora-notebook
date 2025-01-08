package com.example.inovasiyanotebook.dto;

import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ChangeTaskItemDTO {
    private Long id;
    private Long taskId;
    private Long productId;
    private ChangeItemStatus status;
    private LocalDateTime completedAt;
    private String productName; // Список названий продуктов

    // Геттеры и сеттеры
}
