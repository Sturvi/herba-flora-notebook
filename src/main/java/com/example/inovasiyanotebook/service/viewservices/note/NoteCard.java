package com.example.inovasiyanotebook.service.viewservices.note;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.interfaces.ParentEntity;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.NoteService;
import com.example.inovasiyanotebook.views.DesignTools;
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
    private PermissionsCheck permissionsCheck;
    private EditNoteDialog editNoteDialog;
    private DesignTools designTools;
    private Note note;

    public NoteCard(Note note, NavigationTools navigationTools, NoteService noteService, User user, PermissionsCheck permissionsCheck, EditNoteDialog editNoteDialog, DesignTools designTools) {
        this.navigationTools = navigationTools;
        this.note = note;
        this.permissionsCheck = permissionsCheck;
        this.editNoteDialog = editNoteDialog;
        this.designTools = designTools;
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setWidthFull();

        HorizontalLayout header = new HorizontalLayout();
        header.setJustifyContentMode(JustifyContentMode.END);

        if (permissionsCheck.needEditor(user)) {
            Icon editIcon = new Icon(VaadinIcon.EDIT);
            editIcon.setClassName("small-button");
            editIcon.setColor("gray");
            header.add(editIcon);
            editIcon.addClickListener(clickEvent -> {
                editNoteDialog.createNewNoteDialog(note, user);
            });
        }

        if (permissionsCheck.needEditor(user)) {
            Icon deleteIcon = new Icon(VaadinIcon.TRASH);
            deleteIcon.setClassName("small-button");
            deleteIcon.setColor("gray");
            header.add(deleteIcon);
            deleteIcon.addClickListener(clickEvent -> {
                designTools.showConfirmationDialog(() -> {
                    noteService.delete(note);
                    navigationTools.reloadPage();
                });
            });
        }

        Icon pinIcon = new Icon(VaadinIcon.PIN);
        pinIcon.setClassName("small-button");
        pinIcon.setColor(note.isPinned() ? "#FF6666" : "gray");
        header.add(pinIcon);
        if (permissionsCheck.isContributorOrHigher(user)) {
            pinIcon.addClickListener(click -> {
                note.setPinned(!note.isPinned());
                note.setUpdatedBy(user);
                pinIcon.setColor(note.isPinned() ? "#FF6666" : "gray");
                noteService.update(note);
            });
        }

        VerticalLayout informationLayout = new VerticalLayout();
        informationLayout.setSpacing(false);

        informationLayout.add(getParentLayout(note, navigationTools));

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
        text.setClassName("pretext-component");
        addClassName("note-card");
        add(text);
    }

    private static HorizontalLayout getParentLayout(Note note, NavigationTools navigationTools) {
        H5 h5 = new H5("Səviyyə: ");
        h5.addClassName("smaller-text");
        var parentLayout = new HorizontalLayout(h5);


        var parentEntities = note.getParents();
        for (ParentEntity parentEntity : parentEntities) {
            H5 parentName = new H5(parentEntity.getName());
            parentName.addClassName("smaller-text");
            parentName.addClickListener(event -> navigationTools.navigateTo(parentEntity.getViewEnum(), parentEntity.getId().toString()));
            parentLayout.add(parentName);
        }

        parentLayout.addClassName("smaller-text");
        return parentLayout;
    }



}
