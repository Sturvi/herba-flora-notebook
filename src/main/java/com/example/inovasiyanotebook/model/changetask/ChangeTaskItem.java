package com.example.inovasiyanotebook.model.changetask;

import com.example.inovasiyanotebook.model.AbstractEntity;
import com.example.inovasiyanotebook.model.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "change_task_items")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ChangeTaskItem extends AbstractEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private ChangeTask task;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChangeItemStatus status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}

