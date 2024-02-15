package com.example.inovasiyanotebook.service.viewservices.ordermapping;

import com.example.inovasiyanotebook.model.ProductMapping;
import com.example.inovasiyanotebook.model.interfaces.NamedEntity;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.PrototypeComponentsFactory;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductMappingService;
import com.example.inovasiyanotebook.service.viewservices.product.AddNewProductViewService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

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
    private GridListDataView<ProductMapping> dataView;
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

    public VerticalLayout getOrderMappingGridLayout (User user) {
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

    private void gridFilters() {
        dataView.addFilter(productMapping -> {
            if (isSearchTermNonMatching(productMapping)) {
                return false;
            }
            return filterByStatus(productMapping);
        });
    }

    private boolean isSearchTermNonMatching(ProductMapping productMapping) {
        if (searchTerm.isEmpty()) return false;

        boolean matches1CNames = matchesTerm(productMapping.getIncomingOrderPositionName(), searchTerm);
        boolean matchesProducts = matchesTermOptional(productMapping.getProduct(), searchTerm);
        boolean matchesPrintedTypes = matchesTermOptional(productMapping.getPrintedType(), searchTerm);

        return !(matches1CNames || matchesProducts || matchesPrintedTypes);
    }

    private boolean matchesTermOptional(NamedEntity entity, String term) {
        return entity != null && matchesTerm(entity.getName(), term);
    }

    private boolean filterByStatus(ProductMapping productMapping) {
        switch (status) {
            case TO_BE_MAPPED -> {
                return productMapping.getProduct() == null || productMapping.getPrintedType() == null;
            }
            case ALREADY_MAPPED -> {
                return productMapping.getProduct() != null && productMapping.getPrintedType() != null;
            }
            case ALL -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }


    private boolean matchesTerm(String value, String searchTerm) {
        return value != null && value.trim().toLowerCase().contains(searchTerm);
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
        List<ProductMapping> allProductMappings = productMappingService.getAll();

        // Создаем компаратор, который сортирует null значения в начало
        Comparator<ProductMapping> byProductNullFirst = Comparator.comparing(
                productMapping -> productMapping.getProduct() == null ? "" : productMapping.getProduct().toString(),
                Comparator.nullsFirst(String::compareTo)
        );

        // Применяем компаратор к списку
        allProductMappings.sort(byProductNullFirst);

        dataView = productMappingGrid.setItems(allProductMappings);
        gridFilters();
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
