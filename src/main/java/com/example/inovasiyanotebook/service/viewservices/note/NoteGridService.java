package com.example.inovasiyanotebook.service.viewservices.note;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.NoteService;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UIScope
public class NoteGridService {
    private final PermissionsCheck permissionsCheck;
    private final AddNewNoteService addNewNoteService;
    private final NavigationTools navigationTools;
    private final NoteService noteService;

    public Component getNoteGrid (Client client, User user) {
        HorizontalLayout productNameLine = new HorizontalLayout(new H2("Notlar"));
        if (permissionsCheck.needEditor(user.getRole())) {
            Button button = new Button(new Icon(VaadinIcon.PLUS));
            button.addClickListener(e -> addNewNoteService.createNewProductDialog(client, user));
            button.setClassName("small-button");
            productNameLine.add(button);
        }

        Scroller scroller = new Scroller();
        scroller.setSizeFull();
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);

        VerticalLayout container = new VerticalLayout();


        var notes = noteService.getAllByClient(client);

        for (Note note : notes) {
            NoteCard noteCard = new NoteCard(note, navigationTools);
            noteCard.addClassName("note-card");
            container.add(noteCard);
        }

        scroller.setContent(container);


        VerticalLayout notesColumn = new VerticalLayout(productNameLine, scroller);
        notesColumn.setHeightFull();
        notesColumn.setWidthFull();
        notesColumn.setPadding(false);
        notesColumn.setMargin(false);
        return notesColumn;
    }
}
