package com.example.inovasiyanotebook.service.viewservices.product;

import com.example.inovasiyanotebook.model.AbstractEntity;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.interfaces.NamedEntity;
import com.example.inovasiyanotebook.service.entityservices.iml.CategoryService;
import com.example.inovasiyanotebook.service.entityservices.iml.ClientService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@UIScope
@Slf4j
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

        var desktopView = createComponents(List.of(client), categories);
        var mobileView = createComponents(List.of(client), categories);

        designTools.creatDialog(addClientDialog, desktopView, mobileView);
    }

    @Transactional
    public void creatNewProductDialog(Category category) {
        addClientDialog = new Dialog();
        addClientDialog.setWidth("75%");

        List<Client> allClients = clientService.getAll();

        var desktopView = createComponents(allClients, List.of(category));
        var mobileView = createComponents(allClients, List.of(category));

        designTools.creatDialog(addClientDialog, desktopView, mobileView);
    }

    private List<Component> createComponents (List<Client> clients, List<Category> categories) {
        List<Component> componentList = new ArrayList<>();

        TextField productName = designTools.createTextField("Adı:", "^.+$", "Məhsul adı boş ola bilməz.");
        componentList.add(productName);
        TextField productTs = designTools.createTextField("TŞ:", null, null);
        componentList.add(productTs);
        TextField productBarcode = designTools.createTextField("Barkod:", "^[0-9]+$", "Barkod yalnız rəqəmlərdən ibarət olmalıdır");
        componentList.add(productBarcode);
        TextField productWeight = designTools.createTextField("Çəkisi:", null, null);
        componentList.add(productWeight);

        ComboBox<Client> productClient = designTools.creatComboBox("Müştəri:", clients, Client::getName);
        if (clients.size() == 1) {
            productClient.setValue(clients.get(0));
        } else {
            componentList.add(productClient);
        }

        ComboBox<Category> productCategory = designTools.creatComboBox("Kateqoriya:", categories, Category::getFullName);
        if (categories.size() == 1) {
            productCategory.setValue(categories.get(0));
        } else {
            componentList.add(productCategory);
        }

        Button addButton = new Button("Əlavə et");
        addButton.addClickListener(click -> processNewProduct(productName, productTs, productBarcode, productWeight, productClient, productCategory));

        Button cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(event -> addClientDialog.close());
        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, cancelButton);
        buttonLayout.setWidthFull();
        componentList.add(buttonLayout);

        return componentList;
    }

    private void processNewProduct(TextField productName,
                                   TextField productTs,
                                   TextField productBarcode,
                                   TextField productWeight,
                                   ComboBox<Client> clientComboBox,
                                   ComboBox<Category> categoryComboBox) {

        if (productName.getValue().trim().isEmpty()) {
            productName.setInvalid(true);
            return;
        }

        Client client = null;
        Category category = null;

        try {
            client = checkComboBox(clientComboBox);
            category = checkComboBox(categoryComboBox);
        } catch (EmptyComboBoxException e) {
            log.debug("Empty ComboBox.");
            return;
        }

        Product product = Product.builder()
                .name(productName.getValue().trim())
                .ts(productTs.getValue().trim())
                .barcode(productBarcode.getValue().trim())
                .weight(productWeight.getValue().trim())
                .client(client)
                .category(category)
                .build();

        productService.create(product);
        addClientDialog.close();
        navigationTools.reloadPage();
    }


    private <T> T checkComboBox(ComboBox<T> comboBox) {
        if (comboBox.getValue() == null) {
            comboBox.setInvalid(true);
            throw new EmptyComboBoxException();
        } else {
            return comboBox.getValue();
        }
    }

    private class EmptyComboBoxException extends RuntimeException {
    }
}
