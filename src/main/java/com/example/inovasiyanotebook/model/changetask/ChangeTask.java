package com.example.inovasiyanotebook.model.changetask;

import com.example.inovasiyanotebook.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "change_tasks")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ChangeTask extends AbstractEntity {

    @Column(nullable = false)
    private String taskType; // Например, "Смена ГОСТ"

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChangeTaskItem> items = new ArrayList<>();
}
