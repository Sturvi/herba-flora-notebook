package com.example.inovasiyanotebook.service.viewservices.note;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.Product;
import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.interfaces.Noteable;
import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.NoteService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.openedordersbyproduct.CategoriesOpenedOrdersCardLayout;
import com.vaadin.flow.component.HasComponents;
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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UIScope
public class NoteGridService {
    private final PermissionsCheck permissionsCheck;
    private final AddNewNoteService addNewNoteService;
    private final NavigationTools navigationTools;
    private final NoteService noteService;
    private final EditNoteDialog editNoteDialog;
    private final DesignTools designTools;
    private final ObjectProvider<NoteCard> noteProvider;

    private boolean allDataLoaded = false;


    public VerticalLayout getVerticalGridWithHeader(Noteable entity, User user) {
        allDataLoaded = false;
        HorizontalLayout productNameLine = new HorizontalLayout(new H2("Notlar"));
        if (permissionsCheck.isContributorOrHigher()) {
            Button button = new Button(new Icon(VaadinIcon.PLUS));
            button.addClickListener(e -> addNewNoteService.createNewNoteDialog(entity, user));
            button.setClassName("small-button");
            productNameLine.add(button);
        }


        Scroller scroller = getScrollerWithNotes(entity, false, false);


        VerticalLayout notesColumn = new VerticalLayout(productNameLine, scroller);
        notesColumn.setHeightFull();
        notesColumn.setWidthFull();
        notesColumn.setPadding(false);
        notesColumn.setMargin(false);
        notesColumn.setSpacing(false);
        notesColumn.getStyle().set("margin-left", "-10px");

        notesColumn.setAlignItems(FlexComponent.Alignment.START);

        return notesColumn;
    }

    public HorizontalLayout getHorizontalGridWithHeader(Noteable entity) {
        allDataLoaded = false;


        Scroller scroller = getScrollerWithNotes(entity, true, true);


        HorizontalLayout notesColumn = new HorizontalLayout(scroller);
        notesColumn.setHeightFull();
        notesColumn.setWidthFull();
        notesColumn.setPadding(false);
        notesColumn.setMargin(false);
        notesColumn.setSpacing(false);
        notesColumn.getStyle().set("margin-left", "10px");

        //notesColumn.setAlignItems(FlexComponent.Alignment.START);

        return notesColumn;
    }

    private Scroller getScrollerWithNotes(Noteable entity, boolean isHorizontal, boolean onlyNotesText) {
        var container = isHorizontal ? new HorizontalLayout() : new VerticalLayout();
        loadNotes(entity, container, 0, onlyNotesText); // начальная загрузка первых 10 заметок


        Scroller scroller = new Scroller();
        scroller.setSizeFull();
        scroller.setScrollDirection(isHorizontal ? Scroller.ScrollDirection.HORIZONTAL : Scroller.ScrollDirection.VERTICAL);
        scroller.addClassName("no-padding-margin");


        // Добавление слушателя прокрутки
        scroller.getElement().addEventListener("scroll", e -> {
            JsonObject json = e.getEventData();
            double clientHeight = json.getNumber("element.clientHeight");
            double scrollTop = json.getNumber("element.scrollTop");
            double scrollHeight = json.getNumber("element.scrollHeight");

            if (scrollTop + clientHeight >= scrollHeight) {
                // Загрузка следующих 10 заметок
                loadNotes(entity, container, (int) container.getChildren().count(), onlyNotesText);
            }
        }).addEventData("element.clientHeight").addEventData("element.scrollTop").addEventData("element.scrollHeight");


        scroller.setContent(container);
        scroller.setClassName("note-bar-padding");
        scroller.setWidthFull();
        scroller.setHeightFull();
        return scroller;
    }

    private void loadNotes(Noteable entity, HasComponents container, int currentElementCount, boolean onlyNotesText) {
        int currentPade = (int) Math.ceil((double) currentElementCount / 10);


        if (!allDataLoaded) {
            Page<Note> notesPage = null;

            if (entity instanceof Client) {
                notesPage = noteService.getAllByClientWithPagination((Client) entity, currentPade);
            } else if (entity instanceof Category) {
                notesPage = noteService.getAllByCategoryWithPagination((Category) entity, currentPade);
            } else if (entity instanceof Product) {
                notesPage = noteService.getAllByProductWithPagination((Product) entity, currentPade);
            } else if (entity instanceof Order) {
                notesPage = noteService.getAllByOrderWithPagination((Order) entity, currentPade);
            }

            assert notesPage != null;
            if (notesPage.getTotalPages() <= currentPade + 1) {
                allDataLoaded = true; // Установка флага, если это последняя страница
            }

            for (Note note : notesPage.getContent()) {
                NoteCard noteCard = noteProvider.getObject();
                noteCard.setNote(note);
                noteCard.setOnlyTextVisible(onlyNotesText);
                container.add(noteCard);
            }
        }
    }


}
