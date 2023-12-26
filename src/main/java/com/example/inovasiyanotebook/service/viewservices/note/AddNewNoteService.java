package com.example.inovasiyanotebook.service.viewservices.note;

import com.example.inovasiyanotebook.model.AbstractEntity;
import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.NoteService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
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

    private Dialog dialog;

    @Transactional
    public <T extends AbstractEntity> void createNewNoteDialog(T entity, User user) {
        dialog = new Dialog();
        dialog.setWidth("75%");
        dialog.setMaxWidth("600px");

        if (entity instanceof Client || entity instanceof Category || entity instanceof Product) {
            createCommonComponents(entity, user).forEach(dialog::add);
        }

        dialog.open();
    }

    private <T extends AbstractEntity> List<Component> createCommonComponents(T entity, User user) {
        TextArea textArea = designTools.createTextArea("Not", "^.+$", "Not boş ola bilməz.");

        Button addButton = new Button("Əlavə et");
        addButton.addClickListener(click -> processNewNote(textArea, entity, user));

        Button cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(event -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, cancelButton);
        buttonLayout.setWidthFull();

        return List.of(textArea, buttonLayout);
    }

    private <T extends AbstractEntity> void processNewNote(TextArea textArea, T entity, User user) {
        if (textArea.getValue().trim().isEmpty()) {
            textArea.setInvalid(true);
            return;
        }

        Note note = createNoteFromEntity(textArea.getValue(), entity, user);

        noteService.create(note);
        dialog.close();

        navigationTools.reloadPage();
    }

    private <T extends AbstractEntity> Note createNoteFromEntity(String text, T entity, User user) {
        Note.NoteBuilder noteBuilder = Note.builder().text(text).addedBy(user);

        if (entity instanceof Client) {
            noteBuilder.client((Client) entity);
        } else if (entity instanceof Category) {
            noteBuilder.category((Category) entity);
        } else if (entity instanceof Product) {
            noteBuilder.product((Product) entity);
        }

        return noteBuilder.build();
    }
}
