package com.example.inovasiyanotebook.views.changetask.components;

import com.example.inovasiyanotebook.dto.ChangeTaskSummaryDTO;
import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskService;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@UIScope
@RequiredArgsConstructor
@Slf4j
public class AllChangesTaskGrid {
    private final ChangeTaskService changeTaskService;
    private final NavigationTools navigationTools;

    public Grid<ChangeTaskSummaryDTO> getGrid() {
        init();
        return grid;
    }

    private Grid<ChangeTaskSummaryDTO> grid;
    private GridLazyDataView<ChangeTaskSummaryDTO> dataView;
    private String filter = "";

    /**
     * Грид ленивый: строки, агрегаты по позициям, порядок и фильтр по названию считаются в БД.
     */
    @PostConstruct
    public void init() {
        log.info("Initializing AllChangesTaskGrid...");
        grid = new Grid<>();
        grid.setClassName("all-change-tasks-grid");

        dataView = grid.setItems(
                query -> changeTaskService.fetchSummaries(filter, query).stream(),
                query -> changeTaskService.countSummaries(filter));

        configureColumns();

        grid.addItemClickListener(event -> {
            log.debug("Navigating to task with ID: {}", event.getItem().id());
            navigationTools.navigateTo(ViewsEnum.CHANGE_TASK, event.getItem().id().toString());
        });
        log.info("AllChangesTaskGrid initialized successfully.");
    }

    private void configureColumns() {
        log.debug("Configuring grid columns...");
        grid.addColumn(ChangeTaskSummaryDTO::taskType).setHeader("Dəyişiklik");
        grid.addColumn(ChangeTaskSummaryDTO::total).setHeader("Ümumi məhsul");
        grid.addColumn(ChangeTaskSummaryDTO::done).setHeader("Tamamlanıb");
        grid.addColumn(ChangeTaskSummaryDTO::pending).setHeader("Gözləyir");
        grid.addColumn(task -> task.isFinished() ? "Bitib" : "Davam edir").setHeader("Dəyişikliyin durumu");
        grid.addColumn(ChangeTaskSummaryDTO::startedDate).setHeader("Başlanğıc tarixi");
        grid.addColumn(ChangeTaskSummaryDTO::finishedDate).setHeader("Bitmə tarixi");
        log.info("Grid columns configured successfully.");
    }

    public void filterTasksByName(String query) {
        log.debug("Filtering tasks by query: {}", query);
        filter = query == null ? "" : query;
        dataView.refreshAll();
    }
}
