package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderPosition;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.model.order.PrintedType;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Slf4j
class OrderPositionLineComponents {
    private final DesignTools designTools;

    private final HtmlContainer currentLine;
    private final ComboBox<Product> productComboBox;
    private final ComboBox<PrintedType> printedTypeComboBox;
    private final TextField orderCount;
    private final TextField note;
    private final Button addNextButton;

    private HorizontalLayout lineLayout;

    public OrderPositionLineComponents(DesignTools designTools, Integer currentLineNo, List<Product> products, List<PrintedType> productTypes, Button addNextButton) {
        this.designTools = designTools;
        currentLine = new H3(currentLineNo.toString());

        productComboBox = designTools.creatComboBox("Məhsul", products, Product::getName);
        printedTypeComboBox = designTools.creatComboBox("Çap növü", productTypes, PrintedType::getName);
        orderCount = designTools.createTextField("Say", "^\\d+(\\s+.*|)$", "Ya təkcə rəqəmlər ve ya rəqəmlərdən sonra boşluq buraxılaraq yazılar");
        note = designTools.createTextField("Not", ".*", null);
        this.addNextButton = addNextButton;
    }

    public HorizontalLayout getLine() {
        return lineLayout != null ? lineLayout : createNewLineLayout();
    }

    private HorizontalLayout createNewLineLayout() {
        lineLayout = new HorizontalLayout();
        lineLayout.add(currentLine, productComboBox, printedTypeComboBox, orderCount, note, addNextButton);
        lineLayout.setWidthFull();
        lineLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        return lineLayout;
    }

    public boolean isValid() throws EmptyOrderPositionException {
        log.info("Product Value: " + productComboBox.getValue());
        log.info("Printed Type Value: " + printedTypeComboBox.getValue());
        log.info("Order Count Value: " + orderCount.getValue());
        log.info("Note Value: " + note.getValue());


        if (productComboBox.getValue() == null &&
                printedTypeComboBox.getValue() == null &&
                (orderCount.getValue() == null || orderCount.getValue().isEmpty() )&&
                (note.getValue() == null || note.getValue().isEmpty())) {
            throw new EmptyOrderPositionException();
        }

        boolean result = true;

        if (productComboBox.getValue() == null) {
            result = false;
            productComboBox.setErrorMessage("Məhsul boş ola bilməz");
            productComboBox.setInvalid(true);
        }

        if (printedTypeComboBox.getValue() == null) {
            result = false;
            printedTypeComboBox.setErrorMessage("Çap növü boş ola bilməz");
            printedTypeComboBox.setInvalid(true);
        }

        if (orderCount.getValue() == null || !orderCount.getValue().matches(orderCount.getPattern())) {
            result = false;
            orderCount.setInvalid(true);
        }

        return result;
    }

    public OrderPosition toEntity(Order order) {
        if (!isValid()) {
            throw new RuntimeException("Invalid order position");
        }

        return OrderPosition.builder()
                .order(order)
                .product(productComboBox.getValue())
                .printedType(printedTypeComboBox.getValue())
                .count(orderCount.getValue())
                .comment(note.getValue())
                .status(OrderStatusEnum.OPEN)
                .build();
    }

    public void setLineCount (int count) {
        currentLine.setText(String.valueOf(count));
    }

    public void nextButtonVisible(boolean isVisible) {
        addNextButton.setVisible(isVisible);
    }
}
