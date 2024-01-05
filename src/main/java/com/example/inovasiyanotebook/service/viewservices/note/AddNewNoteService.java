package com.example.inovasiyanotebook.service.viewservices.note;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.interfaces.Noteable;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.CategoryService;
import com.example.inovasiyanotebook.service.entityservices.iml.ClientService;
import com.example.inovasiyanotebook.service.entityservices.iml.NoteService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@UIScope
public class AddNewNoteService {
private final DesignTools designTools;
    private final NoteService noteService;
    private final NavigationTools navigationTools;
    private final ClientService clientService;
    private final CategoryService categoryService;

    private Dialog dialog;

    @Transactional
    public <T extends Noteable> void createNewNoteDialog(T entity, User user) {
        dialog = new Dialog();
        dialog.setWidth("75%");
        dialog.setMaxWidth("600px");

        createCommonComponents(entity, user).forEach(dialog::add);

        dialog.open();
    }

    private <T extends Noteable> List<Component> createCommonComponents(T entity, User user) {
        TextArea textArea = designTools.createTextArea("Not", "^(?=.*[^\\n])[\\s\\S]+$", "Not boş ola bilməz.");

        var clientsComboBox = designTools.creatComboBox("Müştəri:", List.of(), Client::getName);
        clientsComboBox.setVisible(false);
        clientsComboBox.setWidthFull();
        var categoriesComboBox = designTools.creatComboBox("Kateqoriya:", List.of(), Category::getFullName);
        categoriesComboBox.setVisible(false);
        categoriesComboBox.setWidthFull();

        Button addButton = new Button("Əlavə et");

        if (!(entity instanceof Product)) {
            var clients = clientService.getAll();
            var categories = categoryService.getAllSortingByParent();

            clientsComboBox.setItems(clients);
            clientsComboBox.setVisible(true);

            categoriesComboBox.setItems(categories);
            categoriesComboBox.setVisible(true);

            if (entity instanceof Client) {
                clientsComboBox.setValue((Client) entity);
            } else if (entity instanceof  Category) {
                categoriesComboBox.setValue((Category) entity);
            }

            addButton.addClickListener(click -> processNewNote(
                    textArea,
                    clientsComboBox.getValue(),
                    categoriesComboBox.getValue(),
                    null,
                    user));
        } else {
            addButton.addClickListener(click -> processNewNote(textArea, null, null, (Product) entity, user));
        }


        Button cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(event -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, cancelButton);
        buttonLayout.setWidthFull();

        HorizontalLayout parentsLayout = new HorizontalLayout(clientsComboBox, categoriesComboBox);

        return List.of(textArea, parentsLayout, buttonLayout);
    }

    private void processNewNote(TextArea textArea, Client client, Category category, Product product, User user) {
        if (textArea.getValue().trim().isEmpty()) {
            textArea.setInvalid(true);
            return;
        }

        if (client == null && category == null && product == null) {
            Notification.show("Kateqotiya və ya Müstəri seçilməlidir. Hər ikisi boş ola bilməz", 3000, Notification.Position.MIDDLE);
            return;
        }

        Note note = Note.builder()
                .text(textArea.getValue())
                .client(client)
                .category(category)
                .product(product)
                .addedBy(user)
                .build();

        noteService.create(note);
        dialog.close();

        navigationTools.reloadPage();
    }
}

