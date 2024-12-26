package com.example.inovasiyanotebook.service.viewservices.product;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
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

import java.util.List;

/**
 * This class provides methods to create a product grid component based on different parameters.
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
     * This method is used to create a product grid component.
     *
     * @param client The client object.
     * @param user The user object.
     * @return The product grid component.
     */
    public Component getProductGrid(Client client, User user) {
        List<Product> productList = productService.getAllByClient(client);
        return createProductGridComponent(productList, user, () -> addNewProductViewService.creatNewProductDialog(client));
    }

    /**
     * Retrieves a product grid component for the given category and user.
     *
     * @param category The category of products to display in the grid.
     * @param user The user for whom the grid is being displayed.
     * @return A {@code Component} representing the product grid.
     */
    public Component getProductGrid(Category category, User user) {
        List<Product> productList = productService.getAllByCategory(category);
        return createProductGridComponent(productList, user, () -> addNewProductViewService.creatNewProductDialog(category));
    }

    public Component getProductGrid(User user) {
        List<Product> productList = productService.getAll();
        return createProductGridComponent(productList, null, false);
    }

    private Component createProductGridComponent(List<Product> products, User user, Runnable addButtonAction){
        return createProductGridComponent(products, addButtonAction, true);
    }

    /**
     * Creates a component that displays a grid of products.
     *
     * @param products         the list of products to display in the grid
     * @param user             the user for permission check
     * @param addButtonAction  the action to be performed when the add button is clicked
     * @return a component containing the product grid
     */
    private Component createProductGridComponent(List<Product> products, Runnable addButtonAction, boolean hasTitle) {
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

        Grid<Product> productGrid = new Grid<>();
        productGrid.setHeightFull();
        productGrid.addColumn(Product::getName)
                .setHeader("Məhsul")
                .setSortable(true)
                .setFlexGrow(3)
                .setKey("name");
        productGrid.addColumn(Product::getCategory)
                .setHeader("Kateqoriya")
                .setSortable(true)
                .setFlexGrow(1)
                .setKey("category");
        productGrid.addColumn(Product::getClient)
                .setHeader("Müştəri")
                .setSortable(true)
                .setFlexGrow(1)
                .setKey("client");
        GridListDataView<Product> dataView = productGrid.setItems(products);
        productGrid.addItemClickListener(event -> {
            String productId = event.getItem().getId().toString();
            navigationTools.navigateTo(ViewsEnum.PRODUCT, productId);
        });

        // Создание объекта TextField для фильтрации
        TextField textField = new TextField();
        textField.setPlaceholder("Axtarış...");
        textField.setWidthFull();
        textField.addValueChangeListener(event -> dataView.refreshAll());

        // Анонимный класс фильтрации
        dataView.addFilter(product -> {
            String searchTerm = textField.getValue().trim().toLowerCase();
            if (searchTerm.isEmpty()) return true;
            boolean matchesName = matchesTerm(product.getName(), searchTerm);
            boolean matchesCategory = matchesTerm(product.getCategory().getName(), searchTerm);
            boolean matchesClient = matchesTerm(product.getClient().getName(), searchTerm);
            return matchesName || matchesCategory || matchesClient;
        });

        productNameLine.add(textField);
        productNameLine.setWidthFull();
        productNameLine.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout verticalLayout = new VerticalLayout(productNameLine, productGrid);
        verticalLayout.setHeightFull();

        return verticalLayout;
    }

    /**
     * Determines whether the given search term is present in the value.
     *
     * @param value The string value to search in. Must not be null.
     * @param searchTerm The search term to look for. Must not be null.
     * @return True if the search term is present in the value, false otherwise.
     */
    private boolean matchesTerm(String value, String searchTerm) {
        return value != null && value.trim().toLowerCase().contains(searchTerm);
    }

}