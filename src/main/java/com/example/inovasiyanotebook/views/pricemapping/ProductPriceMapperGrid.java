package com.example.inovasiyanotebook.views.pricemapping;

import com.example.inovasiyanotebook.model.ProductMapping;
import com.example.inovasiyanotebook.model.ProductPriceMapping;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductPriceMappingService;
import com.example.inovasiyanotebook.service.viewservices.ordermapping.OrderMappingStatusEnum;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@UIScope
public class ProductPriceMapperGrid extends Grid<ProductPriceMapping> {
    private final ProductPriceMappingService productPriceMappingService;

    @Getter
    private TextField searchField;
    @Getter
    private ComboBox<OrderMappingStatusEnum> statusComboBox;
    private GridListDataView<ProductPriceMapping> dataView;
    private OrderMappingStatusEnum status = OrderMappingStatusEnum.TO_BE_MAPPED;

    @PostConstruct
    private void init() {
        setWidthFull();
        setHeightFull();
        setSelectionMode(SelectionMode.SINGLE);


        dataView = setItems(productPriceMappingService.getAll());
        dataView.addFilter(productPriceMapping -> {
            String searchTerm = searchField.getValue().toLowerCase();
            return (productPriceMapping.getIncomingOrderPositionName().toLowerCase().contains(searchTerm)
                    || (productPriceMapping.getProduct() != null && productPriceMapping.getProduct().getName().toLowerCase().contains(searchTerm)))
                    && chekStatus(productPriceMapping);
        });


        searchField = createSearchField();
        statusComboBox = createStatusComboBox();

        addColumn(ProductPriceMapping::getIncomingOrderPositionName)
                .setHeader("Price Listdeki Pozisiya")
                .setSortable(true)
                .setKey("positionName");

        addColumn(ProductPriceMapping::getProduct)
                .setHeader("Məhsul")
                .setSortable(true)
                .setKey("product");

    }

    private TextField createSearchField() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Axtarış...");
        searchField.setWidthFull();
        searchField.addValueChangeListener(event -> {
            dataView.refreshAll();
        });
        return searchField;
    }

    private ComboBox<OrderMappingStatusEnum> createStatusComboBox () {
        ComboBox<OrderMappingStatusEnum> comboBox = new ComboBox<>();
        comboBox.setItems(OrderMappingStatusEnum.values());
        comboBox.setItemLabelGenerator(OrderMappingStatusEnum::getDisplayName);
        comboBox.addValueChangeListener(event -> {
            status = event.getValue();
            dataView.refreshAll();
        });
        comboBox.setValue(OrderMappingStatusEnum.TO_BE_MAPPED);
        return comboBox;
    }

    private boolean chekStatus(ProductPriceMapping productPriceMapping) {
        switch (status) {
            case TO_BE_MAPPED -> {
                return productPriceMapping.getProduct() == null;
            }
            case ALREADY_MAPPED -> {
                return productPriceMapping.getProduct() != null;
            }
            case ALL -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
