package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class OrderComponents {
    private TextField orderNoField;
    private DateTimePicker receivedDateTimePicker;
    private DateTimePicker completedDataTime;
    private TextArea orderCommentField;
    private ComboBox<OrderStatusEnum> statusField;

    private Order order;

    public OrderComponents (DesignTools designTools) {
        this.orderNoField = designTools.createTextField("Sifariş nömrəsi",
                "\\d+",
                "Yalnız rəqəmlərdən ibarət ola bilər");

        this.receivedDateTimePicker = new DateTimePicker("Şifarişin şöbəyə göndərilən tarix");
        receivedDateTimePicker.setValue(LocalDateTime.now());

        this.orderCommentField = designTools.createTextArea("Not", "^.*$", "");
        orderCommentField.setMinHeight("100px");
    }

    public OrderComponents (DesignTools designTools, Order order) {
        this.order = order;
        this.orderNoField = designTools.createTextField("Sifariş nömrəsi",
                "\\d+",
                "Yalnız rəqəmlərdən ibarət ola bilər");
        orderNoField.setValue(order.getOrderNo().toString());
        this.receivedDateTimePicker = new DateTimePicker("Şifarişin şöbəyə göndərilən tarix");
        receivedDateTimePicker.setValue(order.getOrderReceivedDateTime());
        this.completedDataTime = new DateTimePicker("Şifarişin bitmə tarixi");
        completedDataTime.setValue(order.getOrderCompletedDateTime());
        this.orderCommentField = designTools.createTextArea("Not", "^.*$", "");
        orderCommentField.setValue(order.getComment());
        orderCommentField.setMinHeight("100px");
        this.statusField = designTools.creatComboBox("Status", List.of(OrderStatusEnum.values()), OrderStatusEnum::getName, order.getStatus());
    }

    public VerticalLayout getLayout(){
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);

        HorizontalLayout firstLine = new HorizontalLayout(orderNoField, receivedDateTimePicker);
        if (completedDataTime != null) {
            firstLine.add(completedDataTime);
        }
        if (statusField != null) {
            firstLine.add(statusField);
        }

        layout.add(firstLine, orderCommentField);
        return layout;
    }

    public Optional<Order> getEntity () {
        if (order == null) {
            order = new Order();
        }

        if (checkValidation()) {
            order.setOrderNo(Integer.parseInt(orderNoField.getValue()));
            order.setOrderReceivedDateTime(receivedDateTimePicker.getValue());
            order.setComment(orderCommentField.getValue());
            if (statusField == null || completedDataTime == null) {
                order.setStatus(OrderStatusEnum.OPEN);
            } else {
                order.setStatus(statusField.getValue());
                order.setOrderCompletedDateTime(completedDataTime.getValue());
            }
            return Optional.of(order);
        } else {
            return Optional.empty();
        }
    }

    private boolean checkValidation () {
        boolean result = true;

        if (orderNoField.getValue() == null || !orderNoField.getValue().matches(orderNoField.getPattern())) {
            orderNoField.setInvalid(true);
            result = false;
        }

        if (receivedDateTimePicker.getValue() == null) {
            receivedDateTimePicker.setErrorMessage("Tarix seçilməlidir");
            receivedDateTimePicker.setInvalid(true);
            result = false;
        }

        if (statusField != null &&
                statusField.getValue() == null) {
            statusField.setErrorMessage("Boş ola bilməz");
            statusField.setInvalid(true);
            result = false;
        }

        //log.info("completedDataTime: " + completedDataTime.getValue());
        if (completedDataTime != null &&
                completedDataTime.getValue() == null &&
                statusField.getValue() == OrderStatusEnum.COMPLETE) {
            completedDataTime.setErrorMessage("Bitmiş sifarişlərin bitmə tarixi boş ola bilməz");
            completedDataTime.setInvalid(true);
            result = false;
        }

        return result;
    }
}
