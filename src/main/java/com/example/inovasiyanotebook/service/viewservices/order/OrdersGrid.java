package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
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

    private final AtomicBoolean buttonClicked = new AtomicBoolean(false);


    public VerticalLayout getAllOrdersGrid(User user) {
        var orders = orderService.getAll();
        return createGridComponent(orders, user, newOrderDialog::openNewDialog, true);
    }


    private VerticalLayout createGridComponent(List<Order> orders, User user, Runnable addButtonAction, boolean hasTitle) {

        VerticalLayout layout = new VerticalLayout();
        layout.setHeightFull();
        layout.setWidthFull();

        TextField searchField = new TextField();
        searchField.setPlaceholder("Axtarış...");
        searchField.setWidthFull();

        if (hasTitle) {
            var ordersPageHeaderLine = designTools.getAllCommonViewHeader(user, "Sifarişlər", addButtonAction);
            ordersPageHeaderLine.add(searchField);
            ordersPageHeaderLine.setWidthFull();
            layout.add(ordersPageHeaderLine);
        } else {
            layout.add(searchField);
        }

        Grid<Order> orderGrid = new Grid<>();
        orderGrid.setHeightFull();
        orderGrid.setWidthFull();

        orderGrid.addColumn(Order::getOrderNo)
                .setHeader("Sifariş nömrəsi")
                .setSortable(true)
                .setFlexGrow(2)
                .setKey("number");

        orderGrid.addColumn(Order::getProductsString)
                .setHeader("Məhsullar")
                .setSortable(false)
                .setFlexGrow(10)
                .setKey("products");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        orderGrid.addColumn(order ->
                        order.getOrderReceivedDateTime() != null ?
                                order.getOrderReceivedDateTime().format(formatter) : "")
                .setHeader("Sifarişin gəldi")
                .setSortable(true)
                .setFlexGrow(2)
                .setKey("incoming_date");

        orderGrid.addColumn(order ->
                        order.getOrderCompletedDateTime() != null ?
                                order.getOrderCompletedDateTime().format(formatter) : "")
                .setHeader("Sifarişin bitdi")
                .setSortable(true)
                .setFlexGrow(2)
                .setKey("complete_date");

        orderGrid.addColumn(order -> order.getStatus().getName())
                .setHeader("Status")
                .setFlexGrow(2)
                .setSortable(true)
                .setKey("status");

        orderGrid.addItemClickListener(orderLine -> {
            if (!buttonClicked.get()) {
                navigationTools.navigateTo(ViewsEnum.ORDER, orderLine.getItem().getId().toString());
            }
            buttonClicked.set(false);
        });

        if (permissionsCheck.needEditor(user)) {
            orderGrid.addComponentColumn(order -> new HorizontalLayout(
                    designTools.getNewIconButton(VaadinIcon.EDIT.create(), () -> {
                        buttonClicked.set(true);
                        newOrderDialog.openNewDialog(order);
                    }))
            );
        }

        GridListDataView<Order> dataView = orderGrid.setItems(orders);


        searchField.addValueChangeListener(event -> dataView.refreshAll());

        dataView.addFilter(order -> {
            String searchTerm = searchField.getValue().trim().toLowerCase();
            if (searchTerm.isEmpty()) return true;
            boolean matchesName = matchesTerm(order.getOrderNo().toString(), searchTerm);
            boolean matchesProducts = matchesTerm(order.getProductsString(), searchTerm);
            boolean matchesStatus = matchesTerm(order.getStatus().getName(), searchTerm);
            return matchesName || matchesProducts || matchesStatus;
        });

        layout.add(orderGrid);

        return layout;
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value != null && value.trim().toLowerCase().contains(searchTerm);
    }
}
