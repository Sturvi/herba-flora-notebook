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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The AddNewProductViewService class is responsible for creating a dialog for adding a new product to a client's inventory.
 */
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
     * This method creates a dialog box for adding a new product to the client's inventory. The dialog is
     * initialized with the necessary components and design tools.
     *
     * @param client The client for whom the product is being added.
     * @throws TransactionException if there is an error in the transaction.
     */
    public void creatNewProductDialog(Client client) {
        addClientDialog = new Dialog();
        addClientDialog.setWidth("75%");

        List<Category> categories = categoryService.getAllSortingByParent();

        var desktopView = createComponents(List.of(client), categories);
        var mobileView = createComponents(List.of(client), categories);

        designTools.creatDialog(addClientDialog, desktopView, mobileView);
    }

    public void creatNewProductDialog () {
        addClientDialog = new Dialog();
        addClientDialog.setWidth("75%");

        List<Category> categories = categoryService.getAllSortingByParent();
        List<Client> clients = clientService.getAll();

        var desktopView = createComponents(clients, categories);
        var mobileView = createComponents(clients, categories);

        designTools.creatDialog(addClientDialog, desktopView, mobileView);
    }

    /**
     * Creates a new product dialog for the given category.
     *
     * @param category the category of the product
     */
    @Transactional
    public void creatNewProductDialog(Category category) {
        addClientDialog = new Dialog();
        addClientDialog.setWidth("75%");

        List<Client> allClients = clientService.getAll();

        var desktopView = createComponents(allClients, List.of(category));
        var mobileView = createComponents(allClients, List.of(category));

        designTools.creatDialog(addClientDialog, desktopView, mobileView);
    }

    /**
     * Creates a list of components for creating products.
     *
     * @param clients    The list of clients.
     * @param categories The list of categories.
     * @return The list of components.
     */
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
        TextField productShelfLife = designTools.createTextField("Saxlama müddəti:", null, null);
        componentList.add(productShelfLife);

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
        addButton.addClickListener(click -> processNewProduct(productName, productTs, productBarcode, productWeight, productClient, productCategory, productShelfLife));

        Button cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(event -> addClientDialog.close());
        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, cancelButton);
        buttonLayout.setWidthFull();
        componentList.add(buttonLayout);

        return componentList;
    }

    /**
     * Process a new product by validating the input fields and creating the product.
     *
     * @param productName       The text field for the product name.
     * @param productTs         The text field for the product TS.
     * @param productBarcode    The text field for the product barcode.
     * @param productWeight     The text field for the product weight.
     * @param clientComboBox    The combo box for selecting the client.
     * @param categoryComboBox  The combo box for selecting the category.
     */
    private void processNewProduct(TextField productName,
                                   TextField productTs,
                                   TextField productBarcode,
                                   TextField productWeight,
                                   ComboBox<Client> clientComboBox,
                                   ComboBox<Category> categoryComboBox,
                                   TextField productShelfLife) {

        if (productName.getValue().trim().isEmpty()) {
            productName.setInvalid(true);
            return;
        }

        Client client;
        Category category;

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
                .shelfLife(productShelfLife.getValue())
                .build();

        productService.create(product);
        addClientDialog.close();
        navigationTools.reloadPage();
    }


    /**
     * Checks if the given ComboBox has a selected value. If not, throws an EmptyComboBoxException.
     *
     * @param comboBox the ComboBox to be checked.
     * @param <T>      the type of values in the ComboBox.
     * @return the selected value of the ComboBox.
     * @throws EmptyComboBoxException if the ComboBox does not have a selected value.
     */
    private <T> T checkComboBox(ComboBox<T> comboBox) {
        if (comboBox.getValue() == null) {
            comboBox.setInvalid(true);
            throw new EmptyComboBoxException();
        } else {
            return comboBox.getValue();
        }
    }

    /**
     * The {@code EmptyComboBoxException} is a subclass of {@code RuntimeException} that is thrown when a combo box is empty.
     * This exception is used to handle situations where a combo box should have a selected item, but no item is selected.
     */
    private static class EmptyComboBoxException extends RuntimeException {
    }
}
