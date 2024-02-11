package com.example.inovasiyanotebook.views.ordermapping;

import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.service.viewservices.ordermapping.ProductMappingGridService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Sifariş məhsulları")
@Route(value = "productmapping", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class ProductMappingView extends VerticalLayout {
    private final DesignTools designTools;
    private final UserService userService;
    private final NavigationTools navigationTools;
    private final ProductMappingGridService productMappingGridService;
    private final User user;

    public ProductMappingView(DesignTools designTools, UserService userService, NavigationTools navigationTools, ProductMappingGridService productMappingGridService) {
        this.designTools = designTools;
        this.userService = userService;
        this.navigationTools = navigationTools;
        this.productMappingGridService = productMappingGridService;
        this.user = userService.findByUsername(navigationTools.getCurrentUsername());

        setHeightFull();
        setWidthFull();

        var productMappingPageHeaderLine = designTools.getAllCommonViewHeader(user, "1C eyniləşdirmə", null);
        var productMappingGrid = productMappingGridService.getOrderMappingGridLayout(user);

        add(productMappingPageHeaderLine, productMappingGrid);
    }
}
