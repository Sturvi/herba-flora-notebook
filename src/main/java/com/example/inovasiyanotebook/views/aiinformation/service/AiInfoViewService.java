package com.example.inovasiyanotebook.views.aiinformation.service;

import com.example.inovasiyanotebook.views.aiinformation.AiInfoView;
import com.example.inovasiyanotebook.views.aiinformation.components.AiInfoGrid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiInfoViewService {
    private final AiInfoGrid aiInfoGrid;

    public VerticalLayout getVerticalLayout() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        TextField searchField = new TextField();
        searchField.setWidth("100%");
        searchField.setPlaceholder("Axtarış...");
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(event -> {
            aiInfoGrid.filterGrid(searchField.getValue());
        });
        verticalLayout.add(searchField);


        verticalLayout.add(aiInfoGrid.getGrid());


        return verticalLayout;
    }
}
