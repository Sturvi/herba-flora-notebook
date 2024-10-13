package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderPositionService;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderService;
import com.example.inovasiyanotebook.service.entityservices.iml.PrintedTypeService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.inovasiyanotebook.model.order.OrderStatusEnum.*;

@Service
@RequiredArgsConstructor
@UIScope
public class OrderInformation {
    private final DesignTools designTools;
    private final OrderService orderService;
    private final NewOrderDialog newOrderDialog;
    private final PermissionsCheck permissionsCheck;
    private final OrderPositionService orderPositionService;
    private final ProductService productService;
    private final PrintedTypeService printedTypeService;
    private final NavigationTools navigationTools;

    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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
                "Gəlmə tarixi: " + order.getOrderReceivedDate().format(DATE_FORMATTER));
        verticalLayout.add(orderStatus, receivedDateTime);

        if (order.getOrderCompletedDateTime() != null) {
            H5 completedDateTime = new H5(
                    "Bitmə tarixi: " + order.getOrderCompletedDateTime().format(DATE_TIME_FORMATTER));
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

        var ordersComponents = getOrdersComponents(order.getOrderPositions(), user);

        scroller.setContent(ordersComponents);
        verticalLayout.add(scroller);


        return verticalLayout;
    }

    private VerticalLayout getOrdersComponents (List<OrderPosition> orderPositions, User user) {
        var products = productService.getAll();
        var printedTypes = printedTypeService.getAll();


        VerticalLayout ordersComponents = new VerticalLayout();
        for (OrderPosition orderPosition : orderPositions) {

            TextField productComboBox = new TextField("Məhsul");
            productComboBox.setValue(orderPosition.getProduct().getName());
            productComboBox.setReadOnly(true);


            TextField printedTypeComboBox =new TextField("Çap növü");
            printedTypeComboBox.setValue(orderPosition.getPrintedType().getName());
            printedTypeComboBox.setReadOnly(true);

            TextField orderCountField = new TextField("Say");
            orderCountField.setValue(orderPosition.getCount());
            orderCountField.setReadOnly(true);

            ComboBox<OrderStatusEnum> statusComboBox = designTools.creatComboBox("Status", List.of(values()), OrderStatusEnum::getName);
            statusComboBox.setValue(orderPosition.getStatus());
            if (permissionsCheck.needEditor(user)) {
                statusComboBox.addValueChangeListener(event -> {
                    orderPositionService.setOrderPositionStatus(orderPosition, statusComboBox);

                    orderPositionService.update(orderPosition);
                    navigationTools.reloadPage();
                });
            } else {
                statusComboBox.setReadOnly(true);
            }

            DateTimePicker positionCompleteDateTimeField = new DateTimePicker();
            positionCompleteDateTimeField.setLabel("Bitmə tarixi");
            positionCompleteDateTimeField.setValue(orderPosition.getPositionCompletedDateTime());
            positionCompleteDateTimeField.setReadOnly(true);

            TextField noteField = new TextField("Koment");
            noteField.setValue(orderPosition.getComment());
            noteField.setReadOnly(true);
            noteField.setWidthFull();

            HorizontalLayout firstLine = new HorizontalLayout(productComboBox, orderCountField, printedTypeComboBox);
            HorizontalLayout secondLine = new HorizontalLayout(positionCompleteDateTimeField, statusComboBox);
            HorizontalLayout thirdLine = new HorizontalLayout(noteField);
            firstLine.setHeightFull();
            secondLine.setHeightFull();
            thirdLine.setHeightFull();
/*
            orderCountField.setMaxWidth("80px");
            statusComboBox.setMaxWidth("140px");
            positionCompleteDateTimeField.setMaxWidth("230px");*/

            VerticalLayout orderPositionLayout = new VerticalLayout(firstLine, secondLine);
            if (orderPosition.getComment() != null && !orderPosition.getComment().isEmpty()){
                orderPositionLayout.add(thirdLine);
            }
            orderPositionLayout.addClassName("order-card");

            ordersComponents.add(orderPositionLayout);
        }

        return ordersComponents;

    }

}
