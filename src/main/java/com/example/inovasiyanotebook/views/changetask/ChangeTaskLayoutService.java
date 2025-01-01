package com.example.inovasiyanotebook.views.changetask;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@UIScope
public class ChangeTaskLayoutService {
    private final DesignTools designTools;
    private final ProductService productService;
    private final ChangeTaskGridsService changeTaskGridsService;

    public HorizontalLayout getLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.setPadding(false);


        VerticalLayout firstColumn = new VerticalLayout();
        VerticalLayout secondColumn = new VerticalLayout();
        secondColumn.setHeightFull();

        initFirstColum(firstColumn);
        secondColumn.add(changeTaskGridsService.getProductGrid());
        //initSecondColumn(secondColumn);

        layout.add(firstColumn, secondColumn);

        return layout;
    }

/*private void initSecondColumn(VerticalLayout secondColumn) {
    // Создаем Grid для отображения списка продуктов
    Grid<Product> productGrid = new Grid<>(Product.class);
    productGrid.setHeightFull();

    // Устанавливаем данные для грида из сервиса
    productGrid.setItems(productService.getAll());

    // Устанавливаем режим множественного выбора
    productGrid.setSelectionMode(Grid.SelectionMode.MULTI);

    // Оставляем только колонку с именем продукта
    productGrid.setColumns("name");

    // Добавляем слушатель для обработки выбора элементов
    productGrid.addSelectionListener(selection -> {
        selectedProducts.clear();
        selectedProducts.addAll(selection.getAllSelectedItems());
    });

    // Создаем кнопку подтверждения выбора
    Button confirmButton = new Button("Confirm Selection", e -> {
        Notification.show("Selected products: " + selectedProducts);
    });

    // Добавляем Grid и кнопку на вторую колонку
    secondColumn.add(productGrid, confirmButton);
}*/


    private void initFirstColum(VerticalLayout firstColumn) {
        var nameField = designTools.createTextField("Deyisiklik adi", null, null);
        var descriptionField = designTools.createTextArea("Deyisiklik adi", null, null);
        descriptionField.setClassName("change-task-text-area");
        firstColumn.add(nameField, descriptionField, changeTaskGridsService.getCategoryGrid());
    }


}
