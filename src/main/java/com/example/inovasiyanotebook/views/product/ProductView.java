package com.example.inovasiyanotebook.views.product;


import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.service.viewservices.note.NoteGridService;
import com.example.inovasiyanotebook.service.viewservices.product.ProductInfoViewService;
import com.example.inovasiyanotebook.service.viewservices.product.ProductsGridService;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@PageTitle("Məhsul")
@Route(value = "product", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class ProductView extends HorizontalLayout implements HasUrlParameter<String> {
    private final ProductService productService;
    private final ProductInfoViewService infoViewService;
    private final UserService userService;
    private final NavigationTools navigationTools;
    private final NoteGridService noteGridService;
    private final ProductsGridService productsGridService;

    private Product product;
    private User user;

    public ProductView(ProductService productService, ProductInfoViewService infoViewService, UserService userService, NavigationTools navigationTools, NoteGridService noteGridService, ProductsGridService productsGridService) {
        this.productService = productService;
        this.infoViewService = infoViewService;
        this.userService = userService;
        this.navigationTools = navigationTools;
        this.noteGridService = noteGridService;
        this.productsGridService = productsGridService;
        this.user = userService.findByUsername(navigationTools.getCurrentUsername());

        setHeightFull();
        setWidthFull();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String productId) {
        removeAll();

        if (productId == null) {
            allProductsPage();
        } else {
            var productOpt = productService.getById(Long.parseLong(productId));
            productOpt.ifPresentOrElse(product -> {
                        this.product = product;
                        handleHasProduct();
                    },
                    this::allProductsPage);
        }
    }

    private void handleHasProduct() {
        VerticalLayout verticalLayout = new VerticalLayout(
                infoViewService.getProductNameLine(product, user),
                infoViewService.getProductInformation(product, user)
        );
        verticalLayout.setWidthFull();

        VerticalLayout notesLayout = new VerticalLayout(
                noteGridService.getNoteGrid(product, user)
        );
        notesLayout.setWidthFull();

        add(
                verticalLayout,
                notesLayout
        );
    }

    private void allProductsPage() {
        add(
                new VerticalLayout(
                        infoViewService.getAllProductsHeader(user),
                        productsGridService.getProductGrid(user)
                )
        );
    }
}
