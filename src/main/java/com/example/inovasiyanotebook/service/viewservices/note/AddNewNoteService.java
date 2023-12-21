package com.example.inovasiyanotebook.service.viewservices.note;

import com.example.inovasiyanotebook.model.Note;
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
    public void createNewProductDialog(Client client, User user) {
        dialog = new Dialog();
        dialog.setWidth("75%");
        dialog.setMaxWidth("600px");

        createCommonComponents(client, user).forEach(dialog::add);

        dialog.open();
    }


    private List<Component> createCommonComponents (Client client, User user) {
        TextArea textArea = designTools.createTextArea("Not", "^.+$", "Not boş ola bilməz.");

        var addButton = new Button("Əlavə et");
        addButton.addClickListener(click -> processNewNote(textArea, client, user));

        var cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(event -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(addButton, cancelButton);
        buttonLayout.setWidthFull();

        return List.of(textArea, buttonLayout);
    }

    private void processNewNote(TextArea textArea, Client client, User user) {
        if (textArea.getValue().trim().isEmpty()) {
            textArea.setInvalid(true);
            return;
        }

        Note note = Note.builder()
                .text(textArea.getValue())
                .client(client)
                .addedBy(user)
                .build();

        noteService.create(note);
        dialog.close();

        navigationTools.reloadPage();
    }
}
