package com.example.inovasiyanotebook.views.pricemapping;

import com.example.inovasiyanotebook.model.ProductPriceMapping;
import com.example.inovasiyanotebook.repository.specification.ProductPriceMappingSpecifications.PriceMappingGridFilter;
import com.example.inovasiyanotebook.service.PrototypeComponentsFactory;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductPriceMappingService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
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
    private final DesignTools designTools;
    private final PrototypeComponentsFactory prototypeComponentsFactory;

    @Getter
    private TextField searchField;
    @Getter
    private ComboBox<PriceMappingStatusEnum> statusComboBox;
    private GridLazyDataView<ProductPriceMapping> dataView;
    private PriceMappingStatusEnum status = PriceMappingStatusEnum.TO_BE_MAPPED;

    @PostConstruct
    private void init() {
        setWidthFull();
        setHeightFull();
        setSelectionMode(SelectionMode.SINGLE);

        searchField = createSearchField();
        statusComboBox = createStatusComboBox();

        loadItems();

        addColumn(ProductPriceMapping::getIncomingOrderPositionName)
                .setHeader("Price Listdeki Pozisiya")
                .setSortable(true)
                .setSortProperty("incomingOrderPositionName")
                .setKey("positionName")
                .setFlexGrow(5);

        addColumn(this::getProductColumnValue)
                .setHeader("Məhsul")
                .setSortable(true)
                .setSortProperty("product.name")
                .setKey("product")
                .setFlexGrow(5);

        addComponentColumn(productPriceMapping -> designTools.getNewIconButton(VaadinIcon.EDIT.create(), () -> {
                    var dialog = prototypeComponentsFactory.getPricePositionMapperDialog();
                    dialog.setProductPriceMapping(productPriceMapping);
                    dialog.setOnSaveCallback(this::reloadGrid);
                    dialog.open();
                }))
                .setFlexGrow(1);

        addComponentColumn(productPriceMapping -> {
            var icon = productPriceMapping.isIgnored() ? VaadinIcon.EYE.create() : VaadinIcon.EYE_SLASH.create();
            var button = designTools.getNewIconButton(icon, () -> toggleIgnored(productPriceMapping));
            button.setTooltipText(productPriceMapping.isIgnored() ? "Yenidən nəzərə al" : "Nəzərə alma");
            return button;
        })
                .setFlexGrow(1);

    }

    private TextField createSearchField() {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Axtarış...");
        searchField.setWidthFull();
        searchField.addValueChangeListener(event -> reloadGrid());
        return searchField;
    }

    private ComboBox<PriceMappingStatusEnum> createStatusComboBox () {
        ComboBox<PriceMappingStatusEnum> comboBox = new ComboBox<>();
        comboBox.setItems(PriceMappingStatusEnum.values());
        comboBox.setItemLabelGenerator(PriceMappingStatusEnum::getDisplayName);
        comboBox.addValueChangeListener(event -> {
            status = event.getValue();
            reloadGrid();
        });
        comboBox.setValue(PriceMappingStatusEnum.TO_BE_MAPPED);
        return comboBox;
    }

    private void toggleIgnored(ProductPriceMapping productPriceMapping) {
        productPriceMapping.setIgnored(!productPriceMapping.isIgnored());
        productPriceMappingService.update(productPriceMapping);
        reloadGrid();
    }

    private String getProductColumnValue(ProductPriceMapping productPriceMapping) {
        if (productPriceMapping.isIgnored()) {
            return "Nəzərə alınmır";
        }
        return productPriceMapping.getProduct() != null ? productPriceMapping.getProduct().getName() : "";
    }

    private PriceMappingGridFilter currentFilter() {
        return new PriceMappingGridFilter(status, searchField.getValue());
    }

    /**
     * Перечитывает текущую страницу из базы (после редактирования, смены фильтра или загрузки прайса).
     */
    public void reloadGrid() {
        if (dataView != null) {
            dataView.refreshAll();
        }
    }

    /**
     * Ленивая загрузка: страницы, фильтр по статусу и поиск выполняются в БД.
     * Повторный вызов (например, после загрузки прайса с новыми позициями) просто перечитывает данные.
     */
    public void loadItems() {
        if (dataView == null) {
            dataView = setItems(
                    query -> productPriceMappingService.fetchForGrid(currentFilter(), query).stream(),
                    query -> productPriceMappingService.countForGrid(currentFilter()));
        } else {
            dataView.refreshAll();
        }
    }
}
