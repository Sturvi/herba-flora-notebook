package com.example.inovasiyanotebook.service.viewservices.product;

import com.example.inovasiyanotebook.model.Product;
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

@Service
@RequiredArgsConstructor
@Slf4j
@UIScope
public class ProductsGridService {
    private final ProductService productService;
    private final PermissionsCheck permissionsCheck;
    private final NavigationTools navigationTools;
    private final AddNewProductViewService addNewProductViewService;


    public Component getProductGrid (Client client, User user) {
        List<Product> productList = productService.getAllByClient(client);

        return createNewProductsGrid(productList, user, client);
    }

    private Component createNewProductsGrid(List<Product> productList, User user, Client client) {
        HorizontalLayout productNameLine = new HorizontalLayout(new H2("Məhsullar"));

        if (permissionsCheck.needEditor(user.getRole())) {
            Button button = new Button(new Icon(VaadinIcon.PLUS));
            button.addClickListener(e -> addNewProductViewService.creatNewProductDialog(client));
            button.setClassName("small-button");

            productNameLine.add(button);
        }


        Grid<Product> productGrid = new Grid<>();
        productGrid.addColumn(Product::getName)
                .setHeader("Məhsul")
                .setSortable(true)
                .setFlexGrow(4)
                .setKey("name"); // Добавить это
        productGrid.addColumn(Product::getCategory)
                .setHeader("Kateqoriya")
                .setSortable(true)
                .setFlexGrow(3)
                .setKey("category"); // и это

        GridListDataView<Product> dataView = productGrid.setItems(productList);

        productGrid.addItemClickListener(event -> {
            String categoryId = event.getItem().getId().toString();
            navigationTools.navigateTo(ViewsEnum.PRODUCT, categoryId);
        });


        // Создание объекта TextField для фильтрации
        TextField textField = new TextField();
        textField.setPlaceholder("Search...");
        textField.setWidthFull();
        textField.addValueChangeListener(event -> {
            // Обновление всего списка при изменении текста в поле поиска
            dataView.refreshAll();
        });

        // Анонимный класс фильтрации
        dataView.addFilter(product -> {
            String searchTerm = textField.getValue().trim().toLowerCase();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesName = matchesTerm(product.getName(), searchTerm);
            boolean matchesCategory = matchesTerm(product.getCategory().getName(), searchTerm);

            return matchesName || matchesCategory;
        });

        productNameLine.add(textField);
        productNameLine.setWidthFull();
        productNameLine.setAlignItems(FlexComponent.Alignment.CENTER);

        return new VerticalLayout(productNameLine, productGrid);
    }

    // Соответсвует ли искомое значение полю продукта
    private boolean matchesTerm(String value, String searchTerm) {
        return value != null && value.trim().toLowerCase().contains(searchTerm);
    }


}
