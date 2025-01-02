package com.example.inovasiyanotebook.views.changetask;

import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskService;
import com.example.inovasiyanotebook.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@PageTitle("Deyisikler") //todo грамматика
@Route(value = "change-task", layout = MainLayout.class)
@RequiredArgsConstructor
@UIScope
@PermitAll
public class ChangeTaskView extends HorizontalLayout implements HasUrlParameter<Long> {
    private final ChangeTaskLayoutService changeTaskLayoutService;
    private final ChangeTaskService changeTaskService;
    private final AllChangesLayoutService allChangesLayoutService;


    @PostConstruct
    public void init() {
        setHeightFull();
        setWidthFull();

    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
        removeAll();
        if (parameter != null) {
            var changeTaskOpt = changeTaskService.getByIdWithItems(parameter);

            if (changeTaskOpt.isPresent()) {
                changeTaskLayoutService.setChangeTaskData(changeTaskOpt.get(), true);
                add(changeTaskLayoutService.getLayout());
            }
        } else {
            add(allChangesLayoutService.getComponent());
        }
    }
}
