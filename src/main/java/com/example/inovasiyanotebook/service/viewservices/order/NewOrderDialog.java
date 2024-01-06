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

    private List<Product> products;
    private List<PrintedType> printedTypes;

    public void openNewDialog() {
        openNewDialog(null);
    }

    public void openNewDialog(Order order) {
        Dialog dialog = new Dialog();
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(false);
        dialogLayout.setPadding(false);

        OrderComponents orderComponents = order == null ? new OrderComponents(designTools) : new OrderComponents(designTools, order);

        dialogLayout.add(orderComponents.getLayout());

        if (products == null) {
            products = productService.getAll();
        }
        if (printedTypes == null) {
            printedTypes = printedTypeService.getAll();
        }

        VerticalLayout positionsLayout = new VerticalLayout();
        LinkedList<OrderPositionLineComponents> positionLineComponents = new LinkedList<>();
        Runnable runnable = () -> addNewPositionLine(products, positionLineComponents, positionsLayout, printedTypes);

        if (order == null) {
            positionLineComponents.add(new OrderPositionLineComponents(
                    designTools,
                    1,
                    products,
                    printedTypes,
                    runnable));
            positionsLayout.add(positionLineComponents.get(0).getLine());
        } else {
            OrderPositionLineComponents.addNewLinesComponentsToList(
                    designTools,
                    order,
                    positionLineComponents,
                    products,
                    printedTypes,
                    runnable
            );
            positionLineComponents.forEach(components ->positionsLayout.add(components.getLine()));
        }
        positionLineComponents.getLast().nextButtonVisible(true);

        dialogLayout.add(positionsLayout);
        dialogLayout.setHeightFull();
        dialogLayout.setSpacing(false);

        Button saveButton = new Button("Əlavə et");
        saveButton.addClickListener(buttonClickEvent -> saveOrder(
                orderComponents,
                positionLineComponents,
                positionsLayout,
                dialog));
        Button cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(buttonClickEvent -> dialog.close());

        dialogLayout.add(new HorizontalLayout(saveButton, cancelButton));

        dialog.add(dialogLayout);
        dialog.open();
    }


    private void saveOrder(OrderComponents orderComponents,
                           List<OrderPositionLineComponents> orderPositionComponents,
                           VerticalLayout positionsLayout,
                           Dialog dialog) {

        var orderOpt = orderComponents.getEntity();

        if (!checkValidation(orderPositionComponents) || orderOpt.isEmpty()) {
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

        Order order = orderOpt.get();
        orderService.create(orderOpt.get());

        List<OrderPosition> orderPositions = orderPositionComponents.stream()
                .map(orderPositionLineComponents -> orderPositionLineComponents.toEntity(order))
                .toList();

        orderPositionService.saveAll(orderPositions);
        dialog.close();
        navigationTools.reloadPage();
    }

    private boolean checkValidation(List<OrderPositionLineComponents> orderPositionComponents) {
        boolean result = true;

        Iterator<OrderPositionLineComponents> iterator = orderPositionComponents.iterator();
        OrderPositionLineComponents lastLine = null;
        while (iterator.hasNext()) {
            OrderPositionLineComponents components = iterator.next();
            lastLine = components;
            try {
                if (!components.isValid()) {
                    result = false;
                }
            } catch (EmptyOrderPositionException e) {
                    iterator.remove();
            }
        }

        if (orderPositionComponents.isEmpty()) {
            orderPositionComponents.add(lastLine);
        }
        return result;
    }

    private void addNewPositionLine(List<Product> products,
                                    LinkedList<OrderPositionLineComponents> positionLineComponents,
                                    VerticalLayout positionsLayout,
                                    List<PrintedType> printedTypes) {
        positionLineComponents.getLast().nextButtonVisible(false);

        Runnable runnable = () ->
                        addNewPositionLine(products, positionLineComponents, positionsLayout, printedTypes);

        var newOrderPositionLineComponents = new OrderPositionLineComponents(
                designTools,
                positionLineComponents.size() + 1,
                products,
                printedTypes,
                runnable);

        newOrderPositionLineComponents.nextButtonVisible(true);
        positionLineComponents.add(newOrderPositionLineComponents);

        positionsLayout.add(newOrderPositionLineComponents.getLine());
    }


}
