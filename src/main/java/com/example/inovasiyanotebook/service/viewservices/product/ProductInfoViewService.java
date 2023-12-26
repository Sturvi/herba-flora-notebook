package com.example.inovasiyanotebook.service.viewservices.product;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.interfaces.NamedEntity;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.CategoryService;
import com.example.inovasiyanotebook.service.entityservices.iml.ClientService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.function.Consumer;

@Service
@UIScope
@RequiredArgsConstructor
public class ProductInfoViewService {
    private final DesignTools designTools;
    private final ProductService productService;
    private final PermissionsCheck permissionsCheck;
    private final CategoryService categoryService;
    private final ClientService clientService;

    public HorizontalLayout getProductNameLine(Product product, User user) {
        return designTools.getNameLine(product, user, productService, this::updateProductName);
    }

    private void updateProductName(NamedEntity namedEntity, String name) {
        Product product = (Product) namedEntity;

        if (!name.isEmpty()) {
            product.setName(name);
            productService.update(product);
        }
    }

    public Component getProductInformation(Product product, User user) {
        VerticalLayout verticalLayout = new VerticalLayout();

        if (permissionsCheck.needEditor(user)) {
            designTools.addEditableField(product, verticalLayout, "TŞ (və ya ГОСТ):", product.getTs(), "^.*$", "", this::updateTs);
            designTools.addEditableField(product, verticalLayout, "Barkod:", product.getBarcode(), "^\\d*$", "Yalnız rəqəmlərdən ibarət ola bilər.", this::updateBarcode);
            designTools.addEditableField(product, verticalLayout, "Çəkisi:", product.getWeight(), "^.*$", "", this::updateWeight);

            var categories = categoryService.getAllSortingByParent();
            var categoriesComboBox = designTools.creatComboBox("Kateqoriyalar", categories, Category::getFullName);
            categoriesComboBox.setValue(product.getCategory());
            categoriesComboBox.addValueChangeListener(event -> {
                product.setCategory(event.getValue());
                productService.update(product);
            });

            var clients = clientService.getAll();
            clients.sort(Comparator.comparing(Client::getName));
            var clientsComboBox = designTools.creatComboBox("Müştəri", clients, Client::getName);
            clientsComboBox.setValue(product.getClient());
            clientsComboBox.addValueChangeListener(event -> {
                product.setClient(event.getValue());
                productService.update(product);
            });

            verticalLayout.add(new HorizontalLayout(categoriesComboBox, clientsComboBox));
        } else {
            verticalLayout.add(
                    new H4("TŞ (və ya ГОСТ): " + product.getTs()),
                    new H4("Barkod: " + product.getBarcode()),
                    new H4("Çəkisi: " + product.getWeight()),
                    new H4("Kateqoriya: " + product.getCategory().getFullName()),
                    new H4("Müştəri: " + product.getClient().getName())
            );
        }

        return verticalLayout;
    }

    private void updateTs(NamedEntity entity, String newTs) {
        Product product = (Product) entity;
        update(product, newTs, product::setTs);
    }

    private void updateBarcode(NamedEntity entity, String newBarcode) {
        Product product = (Product) entity;
        update(product, newBarcode, product::setBarcode);
    }

    private void updateWeight(NamedEntity entity, String newWeight) {
        Product product = (Product) entity;
        update(product, newWeight, product::setWeight);
    }

    private void update(Product product, String newValue, Consumer<String> setter) {
        setter.accept(newValue);
        productService.update(product);
    }


}
