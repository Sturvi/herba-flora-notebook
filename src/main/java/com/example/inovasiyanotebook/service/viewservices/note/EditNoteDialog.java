package com.example.inovasiyanotebook.service.viewservices.note;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.interfaces.Noteable;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.repository.NoteRepository;
import com.example.inovasiyanotebook.service.entityservices.iml.CategoryService;
import com.example.inovasiyanotebook.service.entityservices.iml.ClientService;
import com.example.inovasiyanotebook.service.entityservices.iml.NoteService;
import com.example.inovasiyanotebook.service.entityservices.iml.ProductService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@UIScope
public class EditNoteDialog {

    private final CategoryService categoryService;
    private final NoteService noteService;
    private final ClientService clientService;
    private final ProductService productService;
    private final DesignTools designTools;
    private final NavigationTools navigationTools;
    private Dialog dialog;

    @Transactional
    public void createNewNoteDialog(Note note, User user) {
        dialog = new Dialog();
        dialog.setWidth("75%");
        dialog.setMaxWidth("600px");

        createCommonComponents(note, user).forEach(dialog::add);

        dialog.open();
    }

    private List<Component> createCommonComponents(Note note, User user) {
        List<Component> components = new ArrayList<>();

        var contentArea = designTools.createTextArea("Not", "^(?=.*[^\\n])[\\s\\S]+$", "Not boş ola bilməz.", note.getText());
        var clientComboBox = designTools.creatComboBox("Müştəri:", clientService.getAll(), Client::getName, note.getClient());
        var categoryComboBox = designTools.creatComboBox("Kateqoriya", categoryService.getAllSortingByParent(), Category::getFullName, note.getCategory());
        var productComboBox = designTools.creatComboBox("Məhsul", productService.getAll(), Product::getName, note.getProduct());
        setupComboBoxListeners(clientComboBox, categoryComboBox, productComboBox);

        components.add(contentArea);
        components.add(new HorizontalLayout(clientComboBox, categoryComboBox));

        Button saveButton = new Button("Yenilə", event -> saveNote(note, user, contentArea, clientComboBox, categoryComboBox, productComboBox));
        Button cancelButton = new Button("Bağla", event -> dialog.close());

        var productAndButtonsLayout = new HorizontalLayout(productComboBox, saveButton, cancelButton);
        productAndButtonsLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        components.add(productAndButtonsLayout);

        return components;
    }

    private void setupComboBoxListeners(ComboBox<Client> clientComboBox, ComboBox<Category> categoryComboBox, ComboBox<Product> productComboBox) {
        productComboBox.addValueChangeListener(selectedProduct -> {
            if (selectedProduct.getValue() != null) {
                categoryComboBox.setValue(null);
                clientComboBox.setValue(null);
            }
        });

        clientComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                productComboBox.setValue(null);
            }
        });
        categoryComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                productComboBox.setValue(null);
            }
        });
    }

    private void saveNote(Note note, User user, TextArea contentArea, ComboBox<Client> clientComboBox, ComboBox<Category> categoryComboBox, ComboBox<Product> productComboBox) {
        if (checkValidation(contentArea, clientComboBox, categoryComboBox, productComboBox)) {
            note.setText(contentArea.getValue());
            note.setCategory(categoryComboBox.getValue());
            note.setClient(clientComboBox.getValue());
            note.setProduct(productComboBox.getValue());
            note.setUpdatedBy(user);
            noteService.update(note);
            navigationTools.reloadPage();
            dialog.close();
        }
    }

    private boolean checkValidation (TextArea textArea, ComboBox<Client> clientComboBox, ComboBox<Category> categoryComboBox, ComboBox<Product> productComboBox) {
        if (!textArea.getValue().matches(textArea.getPattern())) {
            textArea.setInvalid(true);
            return false;
        }

        if (clientComboBox.getValue() == null && categoryComboBox.getValue() == null && productComboBox.getValue() == null) {
            categoryComboBox.setInvalid(true);
            clientComboBox.setInvalid(true);
            productComboBox.setInvalid(true);
            Notification.show("Kateqotiya və ya müstəri və ya məhsul seçilməlidir. üçüdə boş ola bilməz", 3000, Notification.Position.MIDDLE);
            return false;
        }

        if ((productComboBox.getValue() != null && categoryComboBox.getValue() != null)  ||
                (productComboBox.getValue() != null && clientComboBox.getValue() != null)) {
            Notification.show("Məhsul seçildiyi zaman, Müştəri və kateqoriya boş qalmalıdır", 3000, Notification.Position.MIDDLE);
            return false;
        }

        return true;
    }
}
