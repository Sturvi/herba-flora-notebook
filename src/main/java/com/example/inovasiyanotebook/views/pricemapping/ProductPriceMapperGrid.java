package com.example.inovasiyanotebook.views.pricemapping;

import com.example.inovasiyanotebook.model.ProductPriceMapping;
import com.example.inovasiyanotebook.service.PrototypeComponentsFactory;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductPriceMappingService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
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
    private GridListDataView<ProductPriceMapping> dataView;
    private PriceMappingStatusEnum status = PriceMappingStatusEnum.TO_BE_MAPPED;

    @PostConstruct
    private void init() {
        setWidthFull();
        setHeightFull();
        setSelectionMode(SelectionMode.SINGLE);


        loadItems();


        searchField = createSearchField();
        statusComboBox = createStatusComboBox();

        addColumn(ProductPriceMapping::getIncomingOrderPositionName)
                .setHeader("Price Listdeki Pozisiya")
                .setSortable(true)
                .setKey("positionName")
                .setFlexGrow(5);

        addColumn(this::getProductColumnValue)
                .setHeader("Məhsul")
                .setSortable(true)
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
        searchField.addValueChangeListener(event -> {
            dataView.refreshAll();
        });
        return searchField;
    }

    private ComboBox<PriceMappingStatusEnum> createStatusComboBox () {
        ComboBox<PriceMappingStatusEnum> comboBox = new ComboBox<>();
        comboBox.setItems(PriceMappingStatusEnum.values());
        comboBox.setItemLabelGenerator(PriceMappingStatusEnum::getDisplayName);
        comboBox.addValueChangeListener(event -> {
            status = event.getValue();
            dataView.refreshAll();
        });
        comboBox.setValue(PriceMappingStatusEnum.TO_BE_MAPPED);
        return comboBox;
    }

    private boolean chekStatus(ProductPriceMapping productPriceMapping) {
        return status == null || status.matches(productPriceMapping);
    }

    private void toggleIgnored(ProductPriceMapping productPriceMapping) {
        productPriceMapping.setIgnored(!productPriceMapping.isIgnored());
        productPriceMappingService.update(productPriceMapping);
        dataView.refreshAll();
    }

    private String getProductColumnValue(ProductPriceMapping productPriceMapping) {
        if (productPriceMapping.isIgnored()) {
            return "Nəzərə alınmır";
        }
        return productPriceMapping.getProduct() != null ? productPriceMapping.getProduct().getName() : "";
    }

    public void reloadGrid() {
        dataView.refreshAll();
    }

    /**
     * Перечитывает сопоставления из базы (например, после загрузки прайса с новыми позициями)
     */
    public void loadItems() {
        dataView = setItems(productPriceMappingService.getAll());
        dataView.addFilter(productPriceMapping -> {
            String searchTerm = searchField.getValue().toLowerCase();
            return (productPriceMapping.getIncomingOrderPositionName().toLowerCase().contains(searchTerm)
                    || (productPriceMapping.getProduct() != null && productPriceMapping.getProduct().getName().toLowerCase().contains(searchTerm)))
                    && chekStatus(productPriceMapping);
        });
    }
}
