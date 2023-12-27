package com.example.inovasiyanotebook.service.viewservices.note;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.interfaces.ParentEntity;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.NoteService;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.format.DateTimeFormatter;

public class NoteCard extends VerticalLayout {
    private NavigationTools navigationTools;
    private NoteService noteService;
    private Note note;

    public NoteCard(Note note, NavigationTools navigationTools, NoteService noteService, User user) {
        this.navigationTools = navigationTools;
        this.note = note;
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setWidthFull();

        HorizontalLayout header = new HorizontalLayout();
        header.setJustifyContentMode(JustifyContentMode.END);

        Icon pinIcon = new Icon(VaadinIcon.PIN);
        pinIcon.setClassName("small-button");
        pinIcon.setColor(note.isPinned() ? "#FF6666" : "gray");
        header.add(pinIcon);
        pinIcon.addClickListener(click -> {
            note.setPinned(!note.isPinned());
            note.setUpdatedBy(user);
            pinIcon.setColor(note.isPinned() ? "#FF6666" : "gray");
            noteService.update(note);
        });

        VerticalLayout informationLayout = new VerticalLayout();
        informationLayout.setSpacing(false);

        ParentEntity parentEntity = note.getParent();
        H5 parentName = new H5("Səviyyə: " + parentEntity.getName());
        parentName.addClassName("smaller-text");
        parentName.addClickListener(event -> navigationTools.navigateTo(parentEntity.getViewEnum(), parentEntity.getId().toString()));
        informationLayout.add(parentName);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        H5 createdAtInformation = new H5("Yaradılıb: "
                + note.getCreatedAt().format(formatter)
                + ". (" + note.getAddedBy().getFullName() + ")");
        createdAtInformation.addClassName("smaller-text");
        informationLayout.add(createdAtInformation);

        if (note.getUpdatedBy() != null) {
            H5 updatedInformation = new H5("Son düzəliş: "
                    + note.getUpdatedAt().format(formatter)
                    + ". (" + note.getUpdatedBy().getFullName() + ")");
            updatedInformation.addClassName("smaller-text");
            informationLayout.add(updatedInformation);
        }

        add(new HorizontalLayout(informationLayout, header));

        Pre text = new Pre(note.getText());
        text.setWidthFull();
        text.setClassName("pre-text-component");
        addClassName("note-card");
        add(text);
    }
}
