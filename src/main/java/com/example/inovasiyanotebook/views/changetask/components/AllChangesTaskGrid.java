package com.example.inovasiyanotebook.views.changetask.components;

import com.example.inovasiyanotebook.model.changetask.ChangeTask;
import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskService;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@UIScope
@RequiredArgsConstructor
@Slf4j
public class AllChangesTaskGrid {
    private final ChangeTaskService changeTaskService;
    private final NavigationTools navigationTools;

    @Getter
    private Grid<ChangeTask> grid;

    private List<ChangeTask> allTasks;

    @PostConstruct
    public void init() {
        log.info("Initializing AllChangesTaskGrid...");
        grid = new Grid<>();
        grid.setClassName("all-change-tasks-grid");

        allTasks = getSortedTasks();
        grid.setItems(allTasks);

        configureColumns();

        grid.addItemClickListener(event -> {
            log.debug("Navigating to task with ID: {}", event.getItem().getId());
            navigationTools.navigateTo(ViewsEnum.CHANGE_TASK, event.getItem().getId().toString());
        });
        log.info("AllChangesTaskGrid initialized successfully.");
    }

    private List<ChangeTask> getSortedTasks() {
        log.debug("Fetching and sorting tasks...");
        var changeTasks = changeTaskService.getAllWithItems();

        List<ChangeTask> sortedTasks = changeTasks.stream()
            .sorted(Comparator.comparing((ChangeTask task) ->
                task.getItems().stream().allMatch(item -> item.getStatus() == ChangeItemStatus.DONE))
                .thenComparing(task -> task.getItems().stream().allMatch(item -> item.getStatus() == ChangeItemStatus.DONE)
                    ? task.getItems().stream()
                        .map(item -> item.getCompletedAt().toLocalDate())
                        .max(LocalDate::compareTo)
                        .orElse(LocalDate.MIN)
                    : task.getCreatedAt().toLocalDate(), Comparator.reverseOrder()))
            .collect(Collectors.toList());

        log.info("Tasks sorted successfully.");
        return sortedTasks;
    }

    private void configureColumns() {
        log.debug("Configuring grid columns...");
        grid.addColumn(ChangeTask::getTaskType).setHeader("Dəyişiklik");
        grid.addColumn(changeTask -> changeTask.getItems().size()).setHeader("Ümumi məhsul");

        grid.addColumn(changeTask ->
            changeTask.getItems().stream()
                      .filter(item -> item.getStatus() == ChangeItemStatus.DONE)
                      .count()
        ).setHeader("Tamamlanıb");

        grid.addColumn(changeTask ->
            changeTask.getItems().stream()
                      .filter(item -> item.getStatus() == ChangeItemStatus.PENDING)
                      .count()
        ).setHeader("Gözləyir");

        grid.addColumn(changeTask ->
            changeTask.getItems().stream().allMatch(item -> item.getStatus() == ChangeItemStatus.DONE)
            ? "Bitib"
            : "Davam edir"
        ).setHeader("Vəzifənin vəziyyəti");

        grid.addColumn(changeTask -> changeTask.getCreatedAt().toLocalDate())
            .setHeader("Başlanğıc tarixi");

        grid.addColumn(changeTask ->
            changeTask.getItems().stream().allMatch(item -> item.getStatus() == ChangeItemStatus.DONE)
            ? changeTask.getItems().stream()
                        .map(item -> item.getCompletedAt().toLocalDate())
                        .max(LocalDate::compareTo)
                        .orElse(null)
            : null
        ).setHeader("Bitmə tarixi");

        log.info("Grid columns configured successfully.");
    }

    public void filterTasksByName(String query) {
        log.debug("Filtering tasks by query: {}", query);
        if (query == null || query.isBlank()) {
            grid.setItems(allTasks);
            log.debug("Query is blank. Resetting grid items.");
        } else {
            List<ChangeTask> filteredTasks = allTasks.stream()
                .filter(task -> task.getTaskType().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
            grid.setItems(filteredTasks);
            log.debug("Filtered tasks count: {}", filteredTasks.size());
        }
    }
}
