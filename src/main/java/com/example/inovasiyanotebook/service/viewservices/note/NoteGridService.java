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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntFunction;

@Service
@RequiredArgsConstructor
@UIScope
public class NoteGridService {
    private static final int PAGE_SIZE = 10;

    private final PermissionsCheck permissionsCheck;
    private final AddNewNoteService addNewNoteService;
    private final NavigationTools navigationTools;
    private final NoteService noteService;
    private final EditNoteDialog editNoteDialog;
    private final DesignTools designTools;
    private final ObjectProvider<NoteCard> noteProvider;


    public VerticalLayout getVerticalGridWithHeader(Noteable entity, User user) {
        HorizontalLayout productNameLine = new HorizontalLayout(new H2("Notlar"));
        if (permissionsCheck.isContributorOrHigher()) {
            Button button = new Button(new Icon(VaadinIcon.PLUS));
            button.addClickListener(e -> addNewNoteService.createNewNoteDialog(entity, user));
            button.setClassName("small-button");
            productNameLine.add(button);
        }


        Scroller scroller = getScrollerWithNotes(pageFromDatabase(entity), false, false);


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
        return getHorizontalGrid(pageFromDatabase(entity));
    }

    /**
     * Горизонтальная лента заметок из заранее загруженного списка (без обращения к БД):
     * используется на странице открытых заказов, где заметки всех продуктов читаются одним запросом.
     */
    public HorizontalLayout getHorizontalGridWithHeader(Noteable entity, List<Note> preloadedNotes) {
        return getHorizontalGrid(pageFromList(preloadedNotes));
    }

    private HorizontalLayout getHorizontalGrid(IntFunction<Page<Note>> pageSource) {
        Scroller scroller = getScrollerWithNotes(pageSource, true, true);


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

    /**
     * Страница заметок сущности из БД (по 10 штук).
     */
    private IntFunction<Page<Note>> pageFromDatabase(Noteable entity) {
        return pageNumber -> {
            if (entity instanceof Client client) {
                return noteService.getAllByClientWithPagination(client, pageNumber);
            } else if (entity instanceof Category category) {
                return noteService.getAllByCategoryWithPagination(category, pageNumber);
            } else if (entity instanceof Product product) {
                return noteService.getAllByProductWithPagination(product, pageNumber);
            } else if (entity instanceof Order order) {
                return noteService.getAllByOrderWithPagination(order, pageNumber);
            }
            throw new IllegalArgumentException("Unsupported noteable entity: " + entity.getClass());
        };
    }

    /**
     * Страница заметок из уже загруженного списка (по 10 штук).
     */
    private static IntFunction<Page<Note>> pageFromList(List<Note> notes) {
        return pageNumber -> {
            int from = Math.min(pageNumber * PAGE_SIZE, notes.size());
            int to = Math.min(from + PAGE_SIZE, notes.size());
            return new PageImpl<>(notes.subList(from, to), PageRequest.of(pageNumber, PAGE_SIZE), notes.size());
        };
    }

    private Scroller getScrollerWithNotes(IntFunction<Page<Note>> pageSource, boolean isHorizontal, boolean onlyNotesText) {
        var container = isHorizontal ? new HorizontalLayout() : new VerticalLayout();
        // Флаг «всё загружено» принадлежит конкретному скроллеру, а не сервису: на одной странице их может быть много
        AtomicBoolean allDataLoaded = new AtomicBoolean(false);
        loadNotes(pageSource, container, 0, onlyNotesText, allDataLoaded); // начальная загрузка первых 10 заметок


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
                loadNotes(pageSource, container, (int) container.getChildren().count(), onlyNotesText, allDataLoaded);
            }
        }).addEventData("element.clientHeight").addEventData("element.scrollTop").addEventData("element.scrollHeight");


        scroller.setContent(container);
        scroller.setClassName("note-bar-padding");
        scroller.setWidthFull();
        scroller.setHeightFull();
        return scroller;
    }

    private void loadNotes(IntFunction<Page<Note>> pageSource, HasComponents container, int currentElementCount,
                           boolean onlyNotesText, AtomicBoolean allDataLoaded) {
        int currentPage = (int) Math.ceil((double) currentElementCount / PAGE_SIZE);


        if (!allDataLoaded.get()) {
            Page<Note> notesPage = pageSource.apply(currentPage);

            if (notesPage.getTotalPages() <= currentPage + 1) {
                allDataLoaded.set(true); // Установка флага, если это последняя страница
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
