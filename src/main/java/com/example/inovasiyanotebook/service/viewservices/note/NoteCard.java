package com.example.inovasiyanotebook.service.viewservices.note;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.interfaces.ParentEntity;
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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class NoteCard extends VerticalLayout {
    private final NavigationTools navigationTools;
    private final NoteService noteService;
    private final PermissionsCheck permissionsCheck;
    private final EditNoteDialog editNoteDialog;
    private final DesignTools designTools;
    private Note note;

    private HorizontalLayout header;
    private VerticalLayout informationLayout;
    private Pre text;

    public void setNote(Note note) {
        this.note = note;
        initContent();
    }

    public void setOnlyTextVisible (boolean visible) {
        header.setVisible(!visible);
        informationLayout.setVisible(!visible);
        if (visible) {
            text.getStyle().set("font-size", "13px");
        } else {
            text.getStyle().set("font-size", "19px");
        }
    }

    @PostConstruct
    private void init() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        //setWidthFull();
        setMinWidth("400px");

        informationLayout = new VerticalLayout();
        informationLayout.setSpacing(false);

        header = new HorizontalLayout();
        header.setJustifyContentMode(JustifyContentMode.END);

        add(new HorizontalLayout(informationLayout, header));

        addClassName("note-card");
    }

    public void initContent() {
        initHeader();
        initInformationLayout();

        text = new Pre(note.getText());
        text.setWidthFull();
        text.setClassName("pretext-component");

        add(text);
    }

    private void initInformationLayout() {
        informationLayout.add(getParentLayout(note));

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
    }

    private void initHeader() {


        if (permissionsCheck.needEditor()) {
            Icon editIcon = new Icon(VaadinIcon.EDIT);
            editIcon.setClassName("small-button");
            editIcon.setColor("gray");
            header.add(editIcon);
            editIcon.addClickListener(clickEvent -> {
                editNoteDialog.createNewNoteDialog(note, navigationTools.getCurrentUser());
            });
        }

        if (permissionsCheck.needEditor()) {
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
        if (permissionsCheck.isContributorOrHigher()) {
            pinIcon.addClickListener(click -> {
                note.setPinned(!note.isPinned());
                note.setUpdatedBy(navigationTools.getCurrentUser());
                pinIcon.setColor(note.isPinned() ? "#FF6666" : "gray");
                noteService.update(note);
            });
        }
    }

    private HorizontalLayout getParentLayout(Note note) {
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
