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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import elemental.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    private boolean allDataLoaded = false;

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
        scroller.addClassName("no-padding-margin");

        VerticalLayout container = new VerticalLayout();
        loadNotes(client, container, 0); // начальная загрузка первых 10 заметок

        // Добавление слушателя прокрутки
        scroller.getElement().addEventListener("scroll", e -> {
            JsonObject json = e.getEventData();
            double clientHeight = json.getNumber("element.clientHeight");
            double scrollTop = json.getNumber("element.scrollTop");
            double scrollHeight = json.getNumber("element.scrollHeight");

            if (scrollTop + clientHeight >= scrollHeight) {
                // Загрузка следующих 10 заметок
                loadNotes(client, container, (int) container.getChildren().count());
            }
        }).addEventData("element.clientHeight").addEventData("element.scrollTop").addEventData("element.scrollHeight");


        scroller.setContent(container);


        VerticalLayout notesColumn = new VerticalLayout(productNameLine, scroller);
        notesColumn.setHeightFull();
        notesColumn.setWidthFull();
        notesColumn.setPadding(false);
        notesColumn.setMargin(false);
        notesColumn.setSpacing(false);

        notesColumn.setAlignItems(FlexComponent.Alignment.START);

        return notesColumn;
    }

    private void loadNotes(Client client, VerticalLayout container, int currentElementCount) {
        int currentPade = (int) Math.ceil((double) currentElementCount / 10);


        if (!allDataLoaded) {
            Page<Note> notesPage = noteService.getAllByClientWithPagination(client, currentPade);

            if (notesPage.getTotalPages() <= currentPade + 1) {
                allDataLoaded = true; // Установка флага, если это последняя страница
            }

            for (Note note : notesPage.getContent()) {
                NoteCard noteCard = new NoteCard(note, navigationTools);
                container.add(noteCard);
            }
        }
    }
}
