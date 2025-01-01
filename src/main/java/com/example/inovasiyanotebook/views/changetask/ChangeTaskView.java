package com.example.inovasiyanotebook.views.changetask;

import com.example.inovasiyanotebook.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@PageTitle("Deyisikler")
@Route(value = "change-task", layout = MainLayout.class)
@RequiredArgsConstructor
@UIScope
@PermitAll
public class ChangeTaskView extends HorizontalLayout {
    private final ChangeTaskLayoutService changeTaskLayoutService;


    @PostConstruct
    public void init() {
        setHeightFull();
        add(changeTaskLayoutService.getLayout());

    }
}
