package com.example.inovasiyanotebook.views.changetask;

import com.example.inovasiyanotebook.views.DesignTools;
import com.sun.jna.platform.win32.WinGDI;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@UIScope
public class AllChangesLayoutService {
    private final AllChangesTaskGrid allChangesTaskGrid;
    private final DesignTools designTools;

    VerticalLayout layout;

    public Component getComponent() {


        return layout;
    }

    @PostConstruct
    private void init() {
        configurateLayout();

        Button button = new Button("Yeni deyisiklik", new Icon(VaadinIcon.PLUS));
        button.removeClassNames();


        var filterField = designTools.createTextField();
        filterField.setValueChangeMode(ValueChangeMode.EAGER);
        filterField.addValueChangeListener(event -> {

            allChangesTaskGrid.filterTasksByName(filterField.getValue());
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(button, filterField);
        horizontalLayout.setWidthFull();

        layout.add(horizontalLayout, allChangesTaskGrid.getGrid());
    }

    private void configurateLayout() {
        layout = new VerticalLayout();

        layout.setHeightFull();
        layout.setWidthFull();
/*        layout.setSpacing(false);
        layout.setMargin(false);
        layout.setPadding(false);*/
    }
}
