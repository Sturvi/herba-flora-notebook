package com.example.inovasiyanotebook.views.changetask;

import com.example.inovasiyanotebook.model.changetask.ChangeTask;
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

@Service
@UIScope
@RequiredArgsConstructor
@Slf4j
public class AllChangesTaskGrid {
    private final ChangeTaskService changeTaskService;
    private final NavigationTools navigationTools;

    @Getter
    private Grid<ChangeTask> grid;


    @PostConstruct
    public void init() {
        grid = new Grid<>();
        grid.setClassName("all-changes-task-grid");

        var changeTasks = changeTaskService.getAllWithItems();
        grid.setItems(changeTasks);

        grid.addColumn(ChangeTask::getTaskType).setHeader("Deyisiklik"); //todo грамматика
        grid.addColumn(changeTask -> changeTask.getItems().size()).setHeader("Toplam mehsul"); //todo грамматика

        grid.addItemClickListener(event -> navigationTools.navigateTo(ViewsEnum.CHANGE_TASK, event.getItem().getId().toString()));
    }

}
