package com.example.inovasiyanotebook.views.changetask;

import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskService;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.changetask.components.AllChangesLayoutService;
import com.example.inovasiyanotebook.views.changetask.components.ChangeTaskLayoutService;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@PageTitle("Dəyişikliklər")
@Route(value = "change-task", layout = MainLayout.class)
@RequiredArgsConstructor
@UIScope
@PermitAll
@Slf4j
public class ChangeTaskView extends HorizontalLayout implements HasUrlParameter<Long> {
    private final ChangeTaskLayoutService changeTaskLayoutService;
    private final ChangeTaskService changeTaskService;
    private final AllChangesLayoutService allChangesLayoutService;

    @PostConstruct
    public void init() {
        log.info("Initializing ChangeTaskView...");
        setHeightFull();
        setWidthFull();
        log.info("ChangeTaskView initialized successfully.");
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        log.debug("Setting parameter for ChangeTaskView: {}", parameter);
        removeAll();
        if (parameter != null) {
            if (parameter == 0) {
                log.debug("Parameter is 0. Adding new layout.");
                add(changeTaskLayoutService.getNewLayout());
            } else {
                log.debug("Parameter is not 0. Fetching change task with ID: {}", parameter);
                var changeTaskOpt = changeTaskService.getByIdWithItems(parameter);

                if (changeTaskOpt.isPresent()) {
                    log.debug("Change task found. Setting task data.");
                    changeTaskLayoutService.setChangeTaskData(changeTaskOpt.get(), true);
                    add(changeTaskLayoutService.getLayout());
                } else {
                    log.warn("Change task with ID {} not found.", parameter);
                }
            }

        } else {
            log.debug("Parameter is null. Adding all changes layout component.");
            removeAll();
            add(allChangesLayoutService.getComponent());
        }
    }
}
