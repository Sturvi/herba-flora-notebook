package com.example.inovasiyanotebook.views.openedordersbyproduct;

import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskItemService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Scope("prototype")
public class ChangeTasksLayout extends VerticalLayout {
    private final ChangeTaskItemService changeTaskItemService;

    private List<ChangeTaskItem> changeTaskItems;

    @PostConstruct
    private void init() {
        setHeightFull();
        setWidthFull();
        setPadding(false);
        setMargin(false);
        setSpacing(false);
        getStyle().set("margin-left", "10px");

        changeTaskItems = new ArrayList<>();
    }

    public void setChangeTaskItems (List<ChangeTaskItem> changeTaskItems) {
        this.changeTaskItems = changeTaskItems;
        constructLayout();
    }

    private void constructLayout() {
        this.removeAll();

        boolean labelAdded = false;
        HorizontalLayout changesComboBoxes = new HorizontalLayout();

        for (ChangeTaskItem taskItem : changeTaskItems) {
            if (taskItem.getStatus() == ChangeItemStatus.PENDING) {
                if (!labelAdded) {
                    add(new H4("Dəyişikliklər"));
                    labelAdded = true;
                    add(changesComboBoxes);
                }
                ComboBox<ChangeItemStatus> statusComboBox = new ComboBox<>();
                statusComboBox.setItems(ChangeItemStatus.values());
                statusComboBox.setItemLabelGenerator(ChangeItemStatus::getDescription);
                statusComboBox.setValue(taskItem.getStatus());
                statusComboBox.setLabel(taskItem.getTask().getTaskType());
                statusComboBox.addValueChangeListener(event -> {
                    taskItem.setStatus(event.getValue());
                    changeTaskItemService.create(taskItem);

                    // Run update in a separate thread to avoid blocking the UI thread
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000); // Wait for 5 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Update UI after 5 seconds
                        getUI().ifPresent(ui ->
                                ui.access(this::constructLayout)
                        );
                    }).start();
                });

                changesComboBoxes.add(statusComboBox);
            }
        }

    }

    public VerticalLayout getLayout () {
        return this;
    }
}
