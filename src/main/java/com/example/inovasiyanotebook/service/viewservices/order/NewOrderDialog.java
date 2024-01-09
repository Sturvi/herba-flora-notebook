package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.PrintedType;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderPositionService;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderService;
import com.example.inovasiyanotebook.service.entityservices.iml.PrintedTypeService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@UIScope
public class NewOrderDialog {

    private final DesignTools designTools;
    private final ProductService productService;
    private final PrintedTypeService printedTypeService;
    private final OrderService orderService;
    private final OrderPositionService orderPositionService;
    private final NavigationTools navigationTools;
    private final OrderComponentsFactory orderComponentsFactory;

    public void openNewDialog() {
        openNewDialog(null);
    }

    public void openNewDialog(Order order) {
        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(false);
        dialogLayout.setPadding(false);

        OrderComponents orderComponents = orderComponentsFactory.getNewBean();

        if (order != null) {
            orderComponents.setOrder(order);
        }

        dialogLayout.add(orderComponents.getLayout());

        dialogLayout.setHeightFull();
        dialogLayout.setSpacing(false);

        Button saveButton = new Button("Əlavə et");
        saveButton.addClickListener(buttonClickEvent -> {
            boolean entitySaved = orderComponents.save();
            if (entitySaved) {
                navigationTools.reloadPage();
            }
        });
        Button cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(buttonClickEvent -> dialog.close());

        dialogLayout.add(new HorizontalLayout(saveButton, cancelButton));

        dialog.add(dialogLayout);
        dialog.open();
    }





}
