package com.example.inovasiyanotebook.views.order;

import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.service.viewservices.order.NewOrderDialog;
import com.example.inovasiyanotebook.service.viewservices.order.PrintedTypeGrid;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Sifarişlər")
@Route(value = "order", layout = MainLayout.class)
@PermitAll
public class OrderView extends HorizontalLayout {
    private final UserService userService;
    private final NavigationTools navigationTools;
    private final DesignTools designTools;
    private final NewOrderDialog newOrderDialog;
    private final PrintedTypeGrid printedTypeGrid;

    private User user;

    public OrderView(UserService userService, NavigationTools navigationTools, DesignTools designTools, NewOrderDialog newOrderDialog, PrintedTypeGrid printedTypeGrid) {
        this.userService = userService;
        this.navigationTools = navigationTools;
        this.designTools = designTools;
        this.newOrderDialog = newOrderDialog;
        this.printedTypeGrid = printedTypeGrid;
        this.user = userService.findByUsername(navigationTools.getCurrentUsername());

        setHeightFull();
        setWidthFull();

        var ordersPageHeaderLine = designTools.getAllCommonViewHeader(user, "Sifarişlər", newOrderDialog::openNewDialog);

        add(new VerticalLayout(ordersPageHeaderLine),
                new VerticalLayout(printedTypeGrid.getPrintedTypeGrid(user)));
    }


    private void createNewOrder () {
        Dialog dialog = new Dialog();

    }
}
