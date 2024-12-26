package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.order.OrderStatusEnum;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderService;
import com.example.inovasiyanotebook.service.viewservices.note.NoteDialog;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@UIScope
public class OrdersGrid {
    private final OrderService orderService;
    private final NewOrderDialog newOrderDialog;
    private final DesignTools designTools;
    private final NavigationTools navigationTools;
    private final PermissionsCheck permissionsCheck;
    private final PrintedTypeGrid printedTypeGrid;
    private final NoteDialog noteDialog;
    private final UploadComponentCreator uploadComponentCreator;

    private final AtomicBoolean buttonClicked = new AtomicBoolean(false);
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    public VerticalLayout getAllOrdersGrid(User user) {
        var orders = orderService.getAll();
        return createGridComponent(orders, user, newOrderDialog::openNewDialog, true);
    }

    public VerticalLayout getOrderGrid(User user, Product product) {
        var orders = orderService.getAllByProduct(product);
        return createGridComponent(orders.stream().toList(), user, null, false);
    }

    private VerticalLayout createGridComponent(List<Order> orders, User user, Runnable addButtonAction, boolean hasTitle) {

        VerticalLayout layout = new VerticalLayout();
        layout.setHeightFull();
        layout.setWidthFull();

        TextField searchField = getSearchField();

        ComboBox<StatusWrapper> statusComboBox = getStatusWrapperComboBox();


        displayOrdersGridHeader(addButtonAction, hasTitle, statusComboBox, searchField, layout);

        Grid<Order> orderGrid = new Grid<>();
        orderGrid.setHeightFull();
        orderGrid.setWidthFull();

        addGridColumns(user, orderGrid);

        orderGrid.addItemClickListener(orderLine -> {
            if (!buttonClicked.get()) {
                navigationTools.navigateTo(ViewsEnum.ORDER, orderLine.getItem().getId().toString());
            }
            buttonClicked.set(false);
        });

        GridListDataView<Order> dataView = orderGrid.setItems(orders);

        statusComboBox.addValueChangeListener(event -> dataView.refreshAll());
        searchField.addValueChangeListener(event -> dataView.refreshAll());

        applySearchAndStatusFilters(dataView, statusComboBox, searchField);

        layout.add(orderGrid);

        return layout;
    }

    private void applySearchAndStatusFilters(GridListDataView<Order> dataView, ComboBox<StatusWrapper> statusComboBox, TextField searchField) {
        dataView.addFilter(order -> {
            if (statusComboBox.getValue() != StatusWrapper.ALL && statusComboBox.getValue().status != order.getStatus()) {
                return false;
            }

            String searchTerm = searchField.getValue().trim().toLowerCase();
            if (searchTerm.isEmpty()) return true;
            boolean matchesName = matchesTerm(order.getOrderNo().toString(), searchTerm);
            boolean matchesProducts = matchesTerm(order.getProductsString(), searchTerm);
            boolean matchesStatus = matchesTerm(order.getStatus().getName(), searchTerm);
            return matchesName || matchesProducts || matchesStatus;
        });
    }

    private static TextField getSearchField() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Axtarış...");
        searchField.setWidthFull();
        return searchField;
    }

    private static ComboBox<StatusWrapper> getStatusWrapperComboBox() {
        ComboBox<StatusWrapper> statusComboBox = new ComboBox<>();
        statusComboBox.setItems(StatusWrapper.getAllStatuses());
        statusComboBox.setItemLabelGenerator(StatusWrapper::getLabel);
        statusComboBox.setValue(StatusWrapper.OPEN);
        statusComboBox.setPlaceholder("Status");
        return statusComboBox;
    }

    private void addGridColumns(User user, Grid<Order> orderGrid) {
        addOrderNoColumn(orderGrid);
        addProductsNameColumn(orderGrid);
        addOrderReceivedDateTimeColum(orderGrid);
        addOrderCompletedDateTimeColumn(orderGrid);
        addStatusColumn(orderGrid);
        addButtonsColumn(user, orderGrid);
    }

    private void displayOrdersGridHeader(Runnable addButtonAction, boolean hasTitle, ComboBox<StatusWrapper> statusComboBox, TextField searchField, VerticalLayout layout) {
        if (hasTitle) {
            var ordersPageHeaderLine = designTools.getAllCommonViewHeader("Sifarişlər", addButtonAction);

            Button printedTypeButton;
            if (permissionsCheck.isEditorOrHigher()) {
                ordersPageHeaderLine.add(uploadComponentCreator.getUpload());

                printedTypeButton = new Button("Çap növləri");
                printedTypeButton.addClickListener(buttonClickEvent -> {
                    printedTypeGrid.openDialog();
                });
                ordersPageHeaderLine.add(printedTypeButton);
            }

            ordersPageHeaderLine.add(statusComboBox);
            ordersPageHeaderLine.add(searchField);
            ordersPageHeaderLine.setWidthFull();
            layout.add(ordersPageHeaderLine);
        } else {
            statusComboBox.setValue(StatusWrapper.ALL);
            layout.add(new HorizontalLayout(searchField));
        }
    }

    private void addButtonsColumn(User user, Grid<Order> orderGrid) {
        orderGrid.addComponentColumn(order -> {
                    HorizontalLayout componentsColumn = new HorizontalLayout();
                    Button previewButton = designTools.getNewIconButton(VaadinIcon.PRESENTATION.create(), () -> {
                        buttonClicked.set(true);
                        newOrderDialog.openReadOnlyDialog(order, user);
                    });
                    componentsColumn.add(previewButton);

                    Button notesDialogButton = designTools.getNewIconButton(VaadinIcon.NOTEBOOK.create(), () -> {
                        buttonClicked.set(true);
                        noteDialog.openDialog(order, user);
                    });
                    componentsColumn.add(notesDialogButton);

                    if (permissionsCheck.needEditor()) {
                        Button editButton = designTools.getNewIconButton(VaadinIcon.EDIT.create(), () -> {
                            buttonClicked.set(true);
                            newOrderDialog.openNewDialog(order);
                        });

                        componentsColumn.add(editButton);
                    }

                    return componentsColumn;
                }
        ).setFlexGrow(2);
    }

    private static void addStatusColumn(Grid<Order> orderGrid) {
        orderGrid.addColumn(order -> order.getStatus().getName())
                .setHeader("Status")
                .setFlexGrow(2)
                .setSortable(true)
                .setKey("status");
    }

    private void addOrderCompletedDateTimeColumn(Grid<Order> orderGrid) {
        orderGrid.addColumn(order ->
                        order.getOrderCompletedDateTime() != null ?
                                order.getOrderCompletedDateTime().format(DATE_FORMATTER) : "")
                .setHeader("Sifariş bitdi")
                .setSortable(true)
                .setComparator(order ->
                        order.getOrderCompletedDateTime() != null ? order.getOrderCompletedDateTime() : LocalDateTime.MIN)
                .setFlexGrow(2)
                .setKey("complete_date");
    }

    private void addOrderReceivedDateTimeColum(Grid<Order> orderGrid) {
        Grid.Column<Order> orderReceivedDateTimeColumn = orderGrid.addColumn(order ->
                        order.getOrderReceivedDate() != null ?
                                order.getOrderReceivedDate().format(DATE_FORMATTER) : "")
                .setHeader("Sifariş gəldi")
                .setComparator(order ->
                        order.getOrderReceivedDate() != null ? order.getOrderReceivedDate() : LocalDate.MIN)
                .setFlexGrow(2)
                .setKey("incoming_date");

        orderGrid.sort(GridSortOrder.desc(orderReceivedDateTimeColumn).build());
    }


    private static void addProductsNameColumn(Grid<Order> orderGrid) {
        orderGrid.addColumn(Order::getProductsString)
                .setHeader("Məhsullar")
                .setSortable(false)
                .setFlexGrow(10)
                .setKey("products");
    }

    private static void addOrderNoColumn(Grid<Order> orderGrid) {
        orderGrid.addColumn(Order::getOrderNo)
                .setHeader("Sifariş nömrəsi")
                .setSortable(true)
                .setFlexGrow(2)
                .setKey("number");
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value != null && value.trim().toLowerCase().contains(searchTerm);
    }

    public enum StatusWrapper {
        OPEN(OrderStatusEnum.OPEN, "Açıq"),
        WAITING(OrderStatusEnum.WAITING, "Gözləmədə"),
        COMPLETE(OrderStatusEnum.COMPLETE, "Bitdi"),
        CANCELED(OrderStatusEnum.CANCELED, "Ləğv edildi"),
        ALL(null, "Hamısı"); // Дополнительный элемент для "всех статусов"

        @Getter
        private final OrderStatusEnum status;
        @Getter
        private final String label;

        StatusWrapper(OrderStatusEnum status, String label) {
            this.status = status;
            this.label = label;
        }

        public static List<StatusWrapper> getAllStatuses() {
            return Arrays.asList(StatusWrapper.values());
        }
    }


}
