package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@UIScope
public class OrderInformation {
    private final DesignTools designTools;
    private final OrderService orderService;
    private final NewOrderDialog newOrderDialog;
    private final PermissionsCheck permissionsCheck;

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public VerticalLayout getInformationLayout (Order order, User user) {
        VerticalLayout verticalLayout = new VerticalLayout();

        H1 orderNumber = new H1(order.getOrderNo().toString());
        HorizontalLayout headerLine = new HorizontalLayout(orderNumber);
        headerLine.setAlignItems(FlexComponent.Alignment.CENTER);

        if (permissionsCheck.needEditor(user)) {
            Button editButton =
                    designTools.getNewIconButton(VaadinIcon.EDIT.create(), () -> newOrderDialog.openNewDialog(order));
            headerLine.add(editButton);
        }
        verticalLayout.add(headerLine);

        H5 orderStatus = new H5("Status: " + order.getStatus().getName());
        H5 receivedDateTime = new H5(
                "Gəlmə tarixi: " + order.getOrderReceivedDateTime().format(FORMATTER));
        verticalLayout.add(orderStatus, receivedDateTime);

        if (order.getOrderCompletedDateTime() != null) {
            H5 completedDateTime = new H5(
                    "Bitmə tarixi: " + order.getOrderCompletedDateTime().format(FORMATTER));
            verticalLayout.add(completedDateTime);
        }

        if (order.getComment() != null && !order.getComment().isEmpty()) {
            H5 commentHeader = new H5("Not:");
            Pre comment = new Pre(order.getComment());
            comment.setClassName("small-pretext-component");
            VerticalLayout commentLayout = new VerticalLayout(commentHeader, comment);
            commentLayout.setSpacing(false);
            commentLayout.setMargin(false);
            verticalLayout.add(commentLayout);
        }

        Scroller scroller = new Scroller();
        scroller.setSizeFull();
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        //scroller.addClassName("no-padding-margin");

        var ordersComponents = getOrdersComponents(order.getOrderPositions());

        scroller.setContent(ordersComponents);
        verticalLayout.add(scroller);

        return verticalLayout;
    }

    private VerticalLayout getOrdersComponents (List<OrderPosition> orderPositions) {

        VerticalLayout ordersComponents = new VerticalLayout();
        for (OrderPosition orderPosition : orderPositions) {
            StringBuilder componentText = new StringBuilder();

            componentText.append("Məhsul: ").append( orderPosition.getProduct().getName()).append("\t");
            componentText.append("Çap növü: ").append(orderPosition.getPrintedType().getName()).append("\n");
            componentText.append("Say: ").append(orderPosition.getCount()).append("\t\t");
            if (orderPosition.getStatus() != null) {
                componentText.append("Status: ").append(orderPosition.getStatus().getName()).append("\n");
            }
            if (!orderPosition.getComment().isEmpty()) {
                componentText.append("Not: ").append(orderPosition.getComment()).append("\n");
            }


            Pre text = new Pre(componentText.toString());
            text.setClassName("pretext-component");

            VerticalLayout orderPositionLayout = new VerticalLayout(text);
            orderPositionLayout.addClassName("order-card");

            ordersComponents.add(orderPositionLayout);
        }

        return ordersComponents;

    }
}
