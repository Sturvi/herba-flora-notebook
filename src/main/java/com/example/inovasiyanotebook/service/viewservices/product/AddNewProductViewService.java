package com.example.inovasiyanotebook.service.viewservices.product;

import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.service.entityservices.iml.CategoryService;
import com.example.inovasiyanotebook.service.entityservices.iml.ClientService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataView;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@UIScope
public class AddNewProductViewService {
    private final ClientService clientService;
    private final DesignTools designTools;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final NavigationTools navigationTools;
    private Dialog addClientDialog;



    /**
     * Creates a new product dialog for the given client.
     *
     * @param client The client to create a new product for
     */
    @Transactional
    public void creatNewProductDialog(Client client) {
        addClientDialog = new Dialog();
        addClientDialog.setWidth("75%");

        List<Category> allParentCategories = categoryService.getAllParentCategories();
        List<Category> categories = new ArrayList<>();
        allParentCategories.forEach(category -> {
            categories.add(category);
            category.getSubCategories().stream()
                    .sorted(Comparator.comparing(Category::getName))
                    .forEach(categories::add);
        });

        var desktopView = createCommonComponents(categories, client);
        var mobileView = createCommonComponents(categories, client);

        designTools.creatDialog(addClientDialog, desktopView, mobileView);
    }


    private List<Component> createCommonComponents(List<Category> categories, Client client) {
        ViewComponents components = new ViewComponents();

        components.productName = designTools.createTextField("Adı:", "^.+$", "Məhsul adı boş ola bilməz.");
        components.productTs = designTools.createTextField("TŞ:", null, null);
        components.productBarcode = designTools.createTextField("Barkod:", "^[0-9]+$", "Barkod yalnız rəqəmlərdən ibarət olmalıdır");
        components.productWeight = designTools.createTextField("Çəkisi:", null, null);
        components.productCategory = designTools.creatComboBox("Məhsul növü:", categories, Category::getFullName);

        components.addButton = new Button("Əlavə et");
        components.addButton.addClickListener(click -> processNewProduct(components.productName, components.productTs, components.productBarcode, components.productWeight, client, components.productCategory));

        components.cancelButton = new Button("Ləğv et");
        components.cancelButton.addClickListener(event -> addClientDialog.close());
        HorizontalLayout buttonLayout = new HorizontalLayout(components.addButton, components.cancelButton);
        buttonLayout.setWidthFull();

        return List.of(components.productName,
                components.productTs,
                components.productBarcode,
                components.productWeight,
                components.productCategory,
                buttonLayout);
    }

    private void processNewProduct(TextField productName, TextField productTs, TextField productBarcode, TextField productWeight, Client client, ComboBox<Category> productCategory) {
        if (productName.getValue().trim().isEmpty()) {
            productName.setInvalid(true);
            return;
        }

        if (productCategory.getValue() == null) {
            productCategory.setInvalid(true);
            return;
        }

        Product product = Product.builder()
                .name(productName.getValue().trim())
                .ts(productTs.getValue().trim())
                .barcode(productBarcode.getValue().trim())
                .weight(productWeight.getValue().trim())
                .client(client)
                .category(productCategory.getValue())
                .build();

        productService.create(product);
        addClientDialog.close();

        navigationTools.reloadPage();
    }


    /**
     * Class for storing view components related to adding new products.
     */
    private static class ViewComponents {
        TextField productName;
        TextField productTs;
        TextField productBarcode;
        TextField productWeight;
        ComboBox<Category> productCategory;
        Button addButton;
        Button cancelButton;
    }
}
