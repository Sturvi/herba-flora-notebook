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
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.inovasiyanotebook.model.order.OrderStatusEnum.*;

@Slf4j
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class OrderComponents {

    private final DesignTools designTools;
    private final ProductService productService;
    private final OrderService orderService;
    private final PrintedTypeService printedTypeService;
    private final OrderPositionService orderPositionService;

    private TextField orderNoField;
    private DateTimePicker receivedDateTimePicker;
    private DateTimePicker completedDataTime;
    private TextArea orderCommentField;
    private ComboBox<OrderStatusEnum> statusField;
    private Order order;
    private VerticalLayout positionsLayout;
    private VerticalLayout orderLayout;
    private LinkedList<OrderPositionComponents> orderPositionComponents;
    private List<OrderPositionComponents> positionsForRemoved;
    private List<Product> products;
    private List<PrintedType> printedTypes;



    /**
     * Initializes the class instance.
     * This method is annotated with @PostConstruct, and it is automatically called after the class is constructed.
     * It initializes the fields and components used in the class.
     */
    @PostConstruct
    public void init() {
        this.orderNoField = designTools.createTextField("Sifariş nömrəsi",
                "\\d+",
                "Yalnız rəqəmlərdən ibarət ola bilər");

        this.receivedDateTimePicker = new DateTimePicker("Şifarişin şöbəyə göndərilən tarix");
        receivedDateTimePicker.setValue(LocalDateTime.now());

        this.completedDataTime = new DateTimePicker("Şifarişin bitmə tarixi");

        this.orderCommentField = designTools.createTextArea("Not", "^.*$", "");
        orderCommentField.setMinHeight("100px");

        this.statusField = designTools.creatComboBox("Status", List.of(values()), OrderStatusEnum::getName);
        statusField.setValue(OPEN);


        products = productService.getAll();
        printedTypes = printedTypeService.getAll();

        orderPositionComponents = new LinkedList<>();
        orderPositionComponents.add(new OrderPositionComponents(1, designTools.getNewIconButton(VaadinIcon.PLUS.create(), this::addNewPositionLine)));

        this.positionsLayout = new VerticalLayout();
        positionsLayout.add(orderPositionComponents.getFirst().getLine());

        positionsForRemoved = new ArrayList<>();
    }

    /**
     * Sets the order data and updates the corresponding UI components.
     *
     * @param order The order object to set.
     */
    public void setOrder(Order order) {
        this.order = order;
        positionsLayout.removeAll();

        orderNoField.setValue(String.valueOf(order.getOrderNo()));
        receivedDateTimePicker.setValue(order.getOrderReceivedDateTime());
        this.completedDataTime = new DateTimePicker("Şifarişin bitmə tarixi");
        completedDataTime.setValue(order.getOrderCompletedDateTime());
        statusField.setValue(order.getStatus());
        orderCommentField.setValue(order.getComment());

        this.orderPositionComponents.clear();
        int i = 1;
        for (OrderPosition orderPosition : order.getOrderPositions()) {
            Button button = designTools.getNewIconButton(VaadinIcon.PLUS.create(), this::addNewPositionLine);
            button.setVisible(false);
            OrderPositionComponents orderPositionComponent = new OrderPositionComponents(orderPosition, i, button);
            i++;
            orderPositionComponents.add(orderPositionComponent);
            positionsLayout.add(orderPositionComponent.getLine());
        }

        orderPositionComponents.getLast().nextButtonVisible(true);
    }

    /**
     * Sets the read-only mode for the form.
     *
     * @param isReadOnly true to make the form read-only, false otherwise
     */
    public void readOnly(boolean isReadOnly) {
        orderNoField.setReadOnly(isReadOnly);
        receivedDateTimePicker.setReadOnly(isReadOnly);
        completedDataTime.setReadOnly(isReadOnly);
        orderCommentField.setReadOnly(isReadOnly);
        statusField.setReadOnly(isReadOnly);
        orderPositionComponents.forEach(components -> components.setReadOnly(isReadOnly));
    }

    /**
     * Adds a new position line to the order.
     * If the list of order position components is not empty,
     * hides the next button of the last order position component.
     * Creates a new button with an icon for adding a new position line,
     * and sets its click listener to this method for recursively adding another line.
     * Creates a new instance of {@link OrderPositionComponents} with an incremented line number
     * and the newly created button.
     * Adds the new order position components to the list of order position components.
     * Adds the line component of the new order position components to the positions layout.
     */
    private void addNewPositionLine() {
        if (!orderPositionComponents.isEmpty()) {
            orderPositionComponents.getLast().nextButtonVisible(false);
        }

        Button button = designTools.getNewIconButton(VaadinIcon.PLUS.create(), this::addNewPositionLine);

        var newOrderPositionLineComponents = new OrderPositionComponents(orderPositionComponents.size() + 1, button);

        orderPositionComponents.add(newOrderPositionLineComponents);

        positionsLayout.add(newOrderPositionLineComponents.getLine());
    }


    public boolean save() {
        if (order == null) {
            order = new Order();
        }

        if (checkValidation()) {
            order.setOrderNo(Integer.parseInt(orderNoField.getValue()));
            order.setOrderReceivedDateTime(receivedDateTimePicker.getValue());
            order.setComment(orderCommentField.getValue());
            if (statusField.getValue() == null || completedDataTime.getValue() == null) {
                order.setStatus(OPEN);
            } else if (completedDataTime.getValue() != null) {
                order.setStatus(COMPLETE);
                order.setOrderCompletedDateTime(completedDataTime.getValue());
            }

            if (order.getId() == null) {
                orderService.create(order);
            } else {
                orderService.update(order);
            }

            orderPositionComponents.forEach(OrderPositionComponents::save);

            orderPositionService.deleteAll(
                    positionsForRemoved
                            .stream()
                            .map(OrderPositionComponents::getEntity)
                            .toList());

            return true;
        }

        return false;
    }

    private boolean checkValidation() {
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
                statusField.getValue() == COMPLETE) {
            completedDataTime.setErrorMessage("Bitmiş sifarişlərin bitmə tarixi boş ola bilməz");
            completedDataTime.setInvalid(true);
            result = false;
        }
        positionsLayout.removeAll();

        Iterator<OrderPositionComponents> iterator = orderPositionComponents.iterator();
        while (iterator.hasNext()) {
            OrderPositionComponents components = iterator.next();
            try {
                if (!components.isValid()) {
                    result = false;
                }
            } catch (EmptyOrderPositionException e) {
                if (components.getEntity().getId() != null) {
                    positionsForRemoved.add(components);
                }
                iterator.remove();
            }
        }

        for (int i = 0; i < orderPositionComponents.size(); i++) {
            orderPositionComponents.get(i).setCurrentLineNumber(i + 1);
            positionsLayout.add(orderPositionComponents.get(i).getLine());
        }

        if (completedDataTime != null &&
                completedDataTime.getValue() != null &&
                statusField.getValue() == COMPLETE) {
            orderPositionComponents.forEach(position -> position.setStatus(COMPLETE));
        }

        if (orderPositionComponents.isEmpty()) {
            orderPositionComponents.add(new OrderPositionComponents(1, designTools.getNewIconButton(VaadinIcon.PLUS.create(), this::addNewPositionLine)));
            positionsLayout.add(orderPositionComponents.getFirst().getLine());
            return false;
        }

        orderPositionComponents.getLast().nextButtonVisible(true);

        return result;
    }

    public VerticalLayout getLayout() {
        if (orderLayout != null) {
            return orderLayout;
        }

        orderLayout = new VerticalLayout();
        HorizontalLayout firstLine = new HorizontalLayout(orderNoField, receivedDateTimePicker, completedDataTime, statusField);
        firstLine.setWidthFull();
        orderLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        orderLayout.add(firstLine);

        orderCommentField.setWidthFull();
        orderLayout.add(orderCommentField);

        orderLayout.add(positionsLayout);

        return orderLayout;
    }

    private class OrderPositionComponents {
        private HtmlContainer currentLine;
        private ComboBox<Product> productComboBox;
        private ComboBox<PrintedType> printedTypeComboBox;
        private TextField orderCount;
        private TextField note;
        private ComboBox<OrderStatusEnum> statusComboBox;
        private DateTimePicker positionCompleteDateTimeField;
        private Button nextButton;
        private Button deletePositionButton;

        private HorizontalLayout lineLayout;
        @Getter
        private OrderPosition entity;


        private OrderPositionComponents(Integer currentLineNo, Button nextButton) {
            initializeCommonComponents(currentLineNo);
            this.nextButton = nextButton;
            this.entity = new OrderPosition();
        }

        private OrderPositionComponents(OrderPosition entity, Integer currentLineNo, Button nextButton) {
            initializeCommonComponents(currentLineNo);
            this.nextButton = nextButton;
            this.entity = entity;
            this.productComboBox.setValue(entity.getProduct());
            this.printedTypeComboBox.setValue(entity.getPrintedType());
            this.orderCount.setValue(entity.getCount());
            this.note.setValue(entity.getComment());
            this.statusComboBox.setValue(entity.getStatus());
            this.positionCompleteDateTimeField.setValue(entity.getPositionCompletedDateTime());
        }

        private void initializeCommonComponents(Integer currentLineNo) {
            this.currentLine = new H3(currentLineNo.toString());
            this.productComboBox = designTools.creatComboBox("Məhsul", products, Product::getName);
            this.printedTypeComboBox = designTools.creatComboBox("Çap növü", printedTypes, PrintedType::getName);
            this.orderCount = designTools.createTextField("Say", "^\\d+(\\s+.*|)$", "Ya təkcə rəqəmlər ve ya rəqəmlərdən sonra boşluq buraxılaraq yazılar");
            this.note = designTools.createTextField("Not", ".*", null);
            this.statusComboBox = designTools.creatComboBox("Status", List.of(values()), OrderStatusEnum::getName);
            this.deletePositionButton = designTools.getNewIconButton(VaadinIcon.TRASH.create(), this::deleteLine);
            this.statusComboBox.setValue(OPEN);
            this.positionCompleteDateTimeField = new DateTimePicker();
            this.positionCompleteDateTimeField.setLabel("Bitmə tarixi");

            this.orderCount.setMaxWidth("80px");
            this.statusComboBox.setMaxWidth("140px");
            this.positionCompleteDateTimeField.setMaxWidth("230px");
        }

        private void deleteLine() {
            positionsForRemoved.add(this);

            positionsLayout.remove(this.getLine());
            orderPositionComponents.remove(this);

            for (int i = 0; i < orderPositionComponents.size(); i++) {
                orderPositionComponents.get(i).setCurrentLineNumber(i + 1);
            }

            if (orderPositionComponents.isEmpty()) {
                addNewPositionLine();
            } else {
                orderPositionComponents.getLast().nextButtonVisible(true);
            }
        }


        public HorizontalLayout getLine() {
            return lineLayout != null ? lineLayout : createNewLineLayout();
        }

        private HorizontalLayout createNewLineLayout() {
            lineLayout = new HorizontalLayout();
            lineLayout.add(currentLine, productComboBox, printedTypeComboBox, orderCount, note, positionCompleteDateTimeField, statusComboBox, deletePositionButton, nextButton);

            lineLayout.setWidthFull();
            lineLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

            return lineLayout;
        }

        public void setCurrentLineNumber(Integer i) {
            currentLine.setText(i.toString());
        }

        public boolean isValid() throws EmptyOrderPositionException {

            if (productComboBox.getValue() == null &&
                    printedTypeComboBox.getValue() == null &&
                    (orderCount.getValue() == null || orderCount.getValue().isEmpty()) &&
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

        public void save() {
            if (!isValid()) {
                throw new RuntimeException("Invalid order position");
            }

            if (entity == null) {
                entity = new OrderPosition();
            }

            entity.setOrder(order);
            entity.setProduct(productComboBox.getValue());
            entity.setPrintedType(printedTypeComboBox.getValue());
            entity.setCount(orderCount.getValue());
            entity.setComment(note.getValue());
            entity.setPositionCompletedDateTime(positionCompleteDateTimeField.getValue());

            orderPositionService.setOrderPositionStatus(entity, statusComboBox);

            if (statusComboBox == null || statusComboBox.getValue() == null) {
                entity.setStatus(OPEN);
            } else {
                entity.setStatus(statusComboBox.getValue());

                if (statusComboBox.getValue() == COMPLETE && entity.getPositionCompletedDateTime() == null) {
                    entity.setPositionCompletedDateTime(LocalDateTime.now());
                }
            }

            if (entity.getId() == null) {
                orderPositionService.create(entity);
            } else {
                orderPositionService.update(entity);
            }
        }

        public void setStatus (OrderStatusEnum status) {
            statusComboBox.setValue(status);
        }

        public void nextButtonVisible(boolean isVisible) {
            nextButton.setVisible(isVisible);
        }

        public void setReadOnly(boolean isReadOnly) {
            productComboBox.setReadOnly(isReadOnly);
            printedTypeComboBox.setReadOnly(isReadOnly);
            orderCount.setReadOnly(isReadOnly);
            note.setReadOnly(isReadOnly);
            statusComboBox.setReadOnly(isReadOnly);
            deletePositionButton.setVisible(false);
            nextButtonVisible(false);
            positionCompleteDateTimeField.setReadOnly(isReadOnly);
        }
    }
}
