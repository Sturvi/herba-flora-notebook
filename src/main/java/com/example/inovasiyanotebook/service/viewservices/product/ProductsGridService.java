package com.example.inovasiyanotebook.service.viewservices.product;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.repository.specification.ProductSpecifications.ProductGridFilter;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * This class provides methods to create a product grid component based on different parameters.
 * Грид ленивый: страницы, поиск и сортировка выполняются в БД.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@UIScope
public class ProductsGridService {
    private final ProductService productService;
    private final PermissionsCheck permissionsCheck;
    private final NavigationTools navigationTools;
    private final AddNewProductViewService addNewProductViewService;

    /**
     * Продукты клиента.
     */
    public Component getProductGrid(Client client, User user) {
        return createProductGridComponent(new ProductGridFilter(client, null, null),
                () -> addNewProductViewService.creatNewProductDialog(client), true);
    }

    /**
     * Продукты категории и её подкатегорий.
     */
    public Component getProductGrid(Category category, User user) {
        return createProductGridComponent(new ProductGridFilter(null, category, null),
                () -> addNewProductViewService.creatNewProductDialog(category), true);
    }

    /**
     * Все продукты.
     */
    public Component getProductGrid(User user) {
        return createProductGridComponent(new ProductGridFilter(null, null, null), null, false);
    }

    /**
     * @param base            базовый фильтр (клиент / категория); строка поиска подставляется из поля
     * @param addButtonAction действие кнопки «добавить», null - без кнопки
     * @param hasTitle        показывать заголовок «Məhsullar»
     */
    private Component createProductGridComponent(ProductGridFilter base, Runnable addButtonAction, boolean hasTitle) {
        HorizontalLayout productNameLine = new HorizontalLayout();

        if (hasTitle) {
            productNameLine.add(new H2("Məhsullar"));
        }

        if (permissionsCheck.needEditor() && addButtonAction != null) {
            Button button = new Button(new Icon(VaadinIcon.PLUS));
            button.addClickListener(e -> addButtonAction.run());
            button.setClassName("small-button");
            productNameLine.add(button);
        }

        TextField textField = new TextField();
        textField.setPlaceholder("Axtarış...");
        textField.setWidthFull();

        Grid<Product> productGrid = new Grid<>();
        productGrid.setHeightFull();
        productGrid.addColumn(Product::getName)
                .setHeader("Məhsul")
                .setSortable(true)
                .setSortProperty("name")
                .setFlexGrow(3)
                .setKey("name");
        productGrid.addColumn(Product::getCategory)
                .setHeader("Kateqoriya")
                .setSortable(true)
                .setSortProperty("category.name")
                .setFlexGrow(1)
                .setKey("category");
        productGrid.addColumn(Product::getClient)
                .setHeader("Müştəri")
                .setSortable(true)
                .setSortProperty("client.name")
                .setFlexGrow(1)
                .setKey("client");

        Supplier<ProductGridFilter> filter = () -> new ProductGridFilter(base.client(), base.category(), textField.getValue());
        GridLazyDataView<Product> dataView = productGrid.setItems(
                query -> productService.fetchForGrid(filter.get(), query).stream(),
                query -> productService.countForGrid(filter.get()));
        textField.addValueChangeListener(event -> dataView.refreshAll());

        productGrid.addItemClickListener(event -> {
            String productId = event.getItem().getId().toString();
            navigationTools.navigateTo(ViewsEnum.PRODUCT, productId);
        });

        productNameLine.add(textField);
        productNameLine.setWidthFull();
        productNameLine.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout verticalLayout = new VerticalLayout(productNameLine, productGrid);
        verticalLayout.setHeightFull();

        return verticalLayout;
    }

}
