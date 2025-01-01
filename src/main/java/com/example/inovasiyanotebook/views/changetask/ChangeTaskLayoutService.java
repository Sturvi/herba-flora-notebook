package com.example.inovasiyanotebook.views.changetask;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.changetask.ChangeItemStatus;
import com.example.inovasiyanotebook.model.changetask.ChangeTask;
import com.example.inovasiyanotebook.model.changetask.ChangeTaskItem;
import com.example.inovasiyanotebook.service.entityservices.iml.ChangeTaskService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@UIScope
public class ChangeTaskLayoutService {
    private final DesignTools designTools;
    private final ChangeTaskGridsService changeTaskGridsService;
    private final ChangeTaskService changeTaskService;

    public HorizontalLayout getLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setPadding(false);


        VerticalLayout firstColumn = new VerticalLayout();
        VerticalLayout secondColumn = new VerticalLayout();
        secondColumn.setHeightFull();

        initFirstColum(firstColumn);
        secondColumn.add(changeTaskGridsService.getProductGrid());
        //initSecondColumn(secondColumn);

        layout.add(firstColumn, secondColumn);

        return layout;
    }


    private void initFirstColum(VerticalLayout firstColumn) {
        var nameField = designTools.createTextField("Deyisiklik adi", null, null); //todo грамматика
        nameField.setWidthFull();
        var descriptionField = designTools.createTextArea("Deyisiklik tesfiri", null, null);

        Button button = new Button("Yadda saxla");
        button.addClickListener(e -> {
            ChangeTask changeTask = new ChangeTask();
            changeTask.setTaskType(nameField.getValue());
            changeTask.setDescription(descriptionField.getValue());

            var changeTaskItems = changeTaskGridsService.getSelectedProducts().stream()
                    .map(product -> {
                        ChangeTaskItem ctItem = new ChangeTaskItem();
                        ctItem.setProduct(product);
                        ctItem.setTask(changeTask);
                        ctItem.setStatus(ChangeItemStatus.PENDING);
                        return ctItem;
                    }).toList();

            changeTask.setItems(changeTaskItems);

            changeTaskService.create(changeTask);
        });

        descriptionField.setClassName("change-task-text-area");

        var nameAndButtonLine = new HorizontalLayout(nameField, button);
        nameAndButtonLine.setWidthFull();
        nameAndButtonLine.setAlignItems(FlexComponent.Alignment.BASELINE);

        firstColumn.add(nameAndButtonLine, descriptionField, changeTaskGridsService.getCategoryGrid());
    }


}
