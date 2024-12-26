package com.example.inovasiyanotebook.views.order;

import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.service.viewservices.note.NoteGridService;
import com.example.inovasiyanotebook.service.viewservices.order.OrdersGrid;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.OrderService;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.service.viewservices.order.OrderInformation;
import com.example.inovasiyanotebook.service.viewservices.order.PrintedTypeGrid;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

@PageTitle("Sifarişlər")
@Route(value = "order", layout = MainLayout.class)
@PermitAll
public class OrderView extends HorizontalLayout implements HasUrlParameter<String> {
    private final UserService userService;
    private final NavigationTools navigationTools;
    private final DesignTools designTools;
    private final PrintedTypeGrid printedTypeGrid;
    private final OrdersGrid ordersGrid;
    private final OrderService orderService;
    private final OrderInformation orderInformation;
    private final NoteGridService noteGridService;

    private User user;
    private Order order;

    public OrderView(UserService userService, NavigationTools navigationTools, DesignTools designTools, PrintedTypeGrid printedTypeGrid, OrdersGrid ordersGrid, OrderService orderService, OrderInformation orderInformation, NoteGridService noteGridService) {
        this.userService = userService;
        this.navigationTools = navigationTools;
        this.designTools = designTools;
        this.printedTypeGrid = printedTypeGrid;
        this.ordersGrid = ordersGrid;
        this.orderService = orderService;
        this.orderInformation = orderInformation;
        this.noteGridService = noteGridService;
        this.user = userService.findByUsername(navigationTools.getCurrentUsername());

        setHeightFull();
        setWidthFull();

    }


    private void createNewOrder () {
        Dialog dialog = new Dialog();

    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String id) {
        removeAll();

        if (id == null) {
            allProductsPage();
        } else {
            var orderOpt = orderService.getById(Long.parseLong(id));
            orderOpt.ifPresentOrElse(order -> {
                        this.order = order;
                        handleHasProduct();
                    },
                    this::allProductsPage);
        }
    }

    private void handleHasProduct() {
        add(orderInformation.getInformationLayout(order),
                noteGridService.getVerticalGridWithHeader(order, user));


    }

    private void allProductsPage() {

        /*add(new VerticalLayout(ordersGrid.getAllOrdersGrid(user)),
                new VerticalLayout(printedTypeGrid.getPrintedTypeGrid(user)));*/
        add(new VerticalLayout(ordersGrid.getAllOrdersGrid(user)));
    }
}
