package com.example.inovasiyanotebook.service.viewservices.ordermapping;

import com.example.inovasiyanotebook.model.ProductMapping;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.PrototypeComponentsFactory;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.service.viewservices.product.AddNewProductViewService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UIScope
public class ProductMappingGridService {
    private final ProductMappingService productMappingService;
    private final PrototypeComponentsFactory prototypeComponentsFactory;
    private final DesignTools designTools;
    private final AddNewProductViewService addNewProductViewService;

    public VerticalLayout getOrderMappingGridLayout (User user) {

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setHeightFull();

        Grid<ProductMapping> productMappingGrid = new Grid<>();
        productMappingGrid.setHeightFull();
        productMappingGrid.setWidthFull();

        initializeOrderMappingGrid(productMappingGrid);

        layout.add(productMappingGrid);

        return layout;
    }

    private void initializeOrderMappingGrid(Grid<ProductMapping> productMappingGrid) {
        createIncomingOrderPositionNameColumn(productMappingGrid);
        createProductColumn(productMappingGrid);
        createPrintedTypeColumn(productMappingGrid);
        createCommentColumn(productMappingGrid);
        createEditButtonColumn(productMappingGrid);

        setProductMappings(productMappingGrid);
    }

    private void createEditButtonColumn(Grid<ProductMapping> productMappingGrid) {
        productMappingGrid.addComponentColumn(productMapping -> designTools.getNewIconButton(VaadinIcon.EDIT.create(), () -> {
            var dialog = prototypeComponentsFactory.getProductMappingDialogComponent();
            dialog.setProductMappingAndOpenDialog(productMapping);
        }))
                .setFlexGrow(1);
    }

    private void setProductMappings(Grid<ProductMapping> productMappingGrid) {
        var allProductMappings = productMappingService.getAll();
        productMappingGrid.setItems(allProductMappings);
    }

    private void createCommentColumn(Grid<ProductMapping> productMappingGrid) {
        productMappingGrid.addColumn(ProductMapping::getComment)
                .setHeader("Not")
                .setSortable(true)
                .setFlexGrow(3)
                .setKey("comment");
    }

    private void createPrintedTypeColumn(Grid<ProductMapping> productMappingGrid) {
        productMappingGrid.addColumn(ProductMapping::getPrintedType)
                .setHeader("Çap Növü")
                .setSortable(true)
                .setFlexGrow(2)
                .setKey("printedType");
    }

    private void createProductColumn(Grid<ProductMapping> productMappingGrid) {
        productMappingGrid.addColumn(ProductMapping::getProduct)
                .setHeader("Məhsul")
                .setSortable(true)
                .setFlexGrow(3)
                .setKey("productName");
    }

    private void createIncomingOrderPositionNameColumn(Grid<ProductMapping> productMappingGrid) {
        productMappingGrid.addColumn(ProductMapping::getIncomingOrderPositionName)
                .setHeader("1C-də adı")
                .setSortable(true)
                .setFlexGrow(10)
                .setKey("incomingOrderPositionName");
    }

}
