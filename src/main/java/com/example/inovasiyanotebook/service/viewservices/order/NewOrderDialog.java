package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.model.order.PrintedType;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderPositionService;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderService;
import com.example.inovasiyanotebook.service.entityservices.iml.PrintedTypeService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
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


    public void openNewDialog() {
        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();

        var orderNoField = designTools.createTextField("Sifariş nömrəsi",
                "\\d+",
                "Yalnız rəqəmlərdən ibarət ola bilər");

        var orderReceivedDateTime = new DateTimePicker("Şifarişin şöbəyə göndərilən tarix");
        orderReceivedDateTime.setValue(LocalDateTime.now());

        var orderCommentField = designTools.createTextArea("Not", "^.*$", "");
        orderCommentField.setMinHeight("100px");

        dialogLayout.add(new HorizontalLayout(orderNoField, orderReceivedDateTime), orderCommentField);

        var products = productService.getAll();
        var printedTypes = printedTypeService.getAll();
        List<OrderPositionLineComponents> positionLineComponents = new ArrayList<>();
        VerticalLayout positionsLayout = new VerticalLayout();
        Button lastButton = designTools.getNewIconButton(
                VaadinIcon.PLUS.create(),
                () -> addNewPositionLine(products, positionLineComponents, positionsLayout, printedTypes));

        positionLineComponents.add(new OrderPositionLineComponents(designTools, 1, products, printedTypes, lastButton));

        positionsLayout.add(positionLineComponents.get(0).getLine());


        dialogLayout.add(positionsLayout);
        dialogLayout.setHeightFull();
        dialogLayout.setSpacing(false);

        Button saveButton = new Button("Əlavə et");
        saveButton.addClickListener(buttonClickEvent -> saveOrder(
                orderNoField,
                orderReceivedDateTime,
                orderCommentField,
                positionLineComponents,
                positionsLayout,
                dialog));
        Button cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(buttonClickEvent -> dialog.close());

        dialogLayout.add(new HorizontalLayout(saveButton, cancelButton));

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void saveOrder(TextField orderNoField,
                           DateTimePicker orderReceivedDateTime,
                           TextArea orderCommentField,
                           List<OrderPositionLineComponents> orderPositionComponents,
                           VerticalLayout positionsLayout,
                           Dialog dialog) {

        if (!checkValidation(orderNoField, orderReceivedDateTime, orderCommentField, orderPositionComponents)) {
            positionsLayout.removeAll();
            int count = 1;

            for (int i = 0; i < orderPositionComponents.size(); i++) {
                orderPositionComponents.get(i).setLineCount(i + 1);
                orderPositionComponents.get(i).nextButtonVisible(false);

                positionsLayout.add(orderPositionComponents.get(i).getLine());
                if (i == orderPositionComponents.size() - 1) {
                    orderPositionComponents.get(i).nextButtonVisible(true);
                }
            }

            return;
        }

        var order = Order.builder()
                .orderNo(Integer.parseInt(orderNoField.getValue()))
                .orderReceivedDateTime(orderReceivedDateTime.getValue())
                .comment(orderCommentField.getValue())
                .status(OrderStatusEnum.OPEN)
                .build();

        orderService.create(order);

        List<OrderPosition> orderPositions = orderPositionComponents.stream()
                .map(orderPositionLineComponents -> orderPositionLineComponents.toEntity(order))
                .toList();

        orderPositionService.saveAll(orderPositions);
        dialog.close();
        navigationTools.reloadPage();
    }

    private boolean checkValidation(TextField orderNoField,
                                    DateTimePicker orderReceivedDateTime,
                                    TextArea orderCommentField,
                                    List<OrderPositionLineComponents> orderPositionComponents) {
        boolean result = true;

        if (orderNoField.getValue() == null || !orderNoField.getValue().matches(orderNoField.getPattern())) {
            orderNoField.setInvalid(true);
            result = false;
        }

        if (orderReceivedDateTime.getValue() == null) {
            orderNoField.setErrorMessage("Tarix seçilməlidir");
            orderNoField.setInvalid(true);
            result = false;
        }

        Iterator<OrderPositionLineComponents> iterator = orderPositionComponents.iterator();
        while (iterator.hasNext()) {
            OrderPositionLineComponents components = iterator.next();
            try {
                if (!components.isValid()) {
                    result = false;
                }
            } catch (EmptyOrderPositionException e) {
                iterator.remove();
            }
        }

        return result;
    }

    private void addNewPositionLine(List<Product> products,
                                    List<OrderPositionLineComponents> positionLineComponents,
                                    VerticalLayout positionsLayout,
                                    List<PrintedType> printedTypes) {
        positionLineComponents.get(positionLineComponents.size() - 1).nextButtonVisible(false);

        Button addNextPositionButton = designTools.getNewIconButton(
                VaadinIcon.PLUS.create(), () ->
                        addNewPositionLine(products, positionLineComponents, positionsLayout, printedTypes));

        var newOrderPositionLineComponents = new OrderPositionLineComponents(
                designTools,
                positionLineComponents.size() + 1,
                products, printedTypes,
                addNextPositionButton);

        positionLineComponents.add(newOrderPositionLineComponents);

        positionsLayout.add(newOrderPositionLineComponents.getLine());
    }


}
