package com.example.inovasiyanotebook.service.viewservices.ordermapping;

import com.example.inovasiyanotebook.model.ProductMapping;
import com.example.inovasiyanotebook.repository.specification.ProductMappingSpecifications.ProductMappingGridFilter;
import com.example.inovasiyanotebook.service.PrototypeComponentsFactory;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.service.viewservices.product.AddNewProductViewService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
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

    private Grid<ProductMapping> productMappingGrid;
    private VerticalLayout gridLayout;
    private GridLazyDataView<ProductMapping> dataView;
    private OrderMappingStatusEnum status;
    private String searchTerm;

    @PostConstruct
    private void init () {
        gridLayout = new VerticalLayout();
        gridLayout.setWidthFull();
        gridLayout.setHeightFull();

        productMappingGrid = new Grid<>();
        productMappingGrid.setHeightFull();
        productMappingGrid.setWidthFull();

        status = OrderMappingStatusEnum.ALREADY_MAPPED;
        searchTerm = "";

        initializeOrderMappingGrid(productMappingGrid);

        gridLayout.add(productMappingGrid);
    }

    public VerticalLayout getOrderMappingGridLayout () {
        return gridLayout;
    }

    public void setFilter (OrderMappingStatusEnum status) {
        this.status = status;
        dataView.refreshAll();
    }

    public void setSearchTerm (String searchTerm){
        this.searchTerm = searchTerm.toLowerCase().trim();
        dataView.refreshAll();
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

    /**
     * Ленивая загрузка: страницы, фильтр по статусу сопоставления и поиск выполняются в БД.
     * По умолчанию несопоставленные строки идут первыми.
     */
    private void setProductMappings(Grid<ProductMapping> productMappingGrid) {
        dataView = productMappingGrid.setItems(
                query -> productMappingService.fetchForGrid(new ProductMappingGridFilter(status, searchTerm), query).stream(),
                query -> productMappingService.countForGrid(new ProductMappingGridFilter(status, searchTerm)));
    }

    private void createCommentColumn(Grid<ProductMapping> productMappingGrid) {
        productMappingGrid.addColumn(ProductMapping::getComment)
                .setHeader("Not")
                .setSortable(true)
                .setSortProperty("comment")
                .setFlexGrow(3)
                .setKey("comment");
    }

    private void createPrintedTypeColumn(Grid<ProductMapping> productMappingGrid) {
        productMappingGrid.addColumn(ProductMapping::getPrintedType)
                .setHeader("Çap Növü")
                .setSortable(true)
                .setSortProperty("printedType.name")
                .setFlexGrow(2)
                .setKey("printedType");
    }

    private void createProductColumn(Grid<ProductMapping> productMappingGrid) {
        productMappingGrid.addColumn(ProductMapping::getProduct)
                .setHeader("Məhsul")
                .setSortable(true)
                .setSortProperty("product.name")
                .setFlexGrow(3)
                .setKey("productName");
    }

    private void createIncomingOrderPositionNameColumn(Grid<ProductMapping> productMappingGrid) {
        productMappingGrid.addColumn(ProductMapping::getIncomingOrderPositionName)
                .setHeader("1C-də adı")
                .setSortable(true)
                .setSortProperty("incomingOrderPositionName")
                .setFlexGrow(10)
                .setKey("incomingOrderPositionName");
    }


}
