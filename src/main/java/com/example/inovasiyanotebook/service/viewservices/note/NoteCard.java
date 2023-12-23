package com.example.inovasiyanotebook.service.viewservices.note;

import com.example.inovasiyanotebook.model.Note;
import com.example.inovasiyanotebook.model.interfaces.ParentEntity;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.format.DateTimeFormatter;

public class NoteCard extends VerticalLayout {
    private NavigationTools navigationTools;

    private Note note;


    public NoteCard(Note note, NavigationTools navigationTools) {
        this.navigationTools = navigationTools;
        this.note = note;
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setWidthFull();

        VerticalLayout informationLayout = new VerticalLayout();

        ParentEntity parentEntity = note.getParent();
        H5 parentName = new H5("Parent: " + parentEntity.getName());
        parentName.addClickListener(event -> navigationTools.navigateTo(parentEntity.getViewEnum(), parentEntity.getId().toString()));
        informationLayout.add(parentName);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        H5 createdAtInformation = new H5("Yaradılıb: "
                + note.getCreatedAt().format(formatter)
                + ". (" + note.getAddedBy().getFullName() + ")");
        informationLayout.add(createdAtInformation);


        if (note.getUpdatedBy() != null) {
            H5 updatedInformation = new H5("Son düzəliş: "
                    + note.getUpdatedAt().format(formatter)
                    + ". (" + note.getUpdatedBy().getFullName() + ")");
            informationLayout.add(updatedInformation);
        }

        add(informationLayout);

        H5 text = new H5(note.getText());
        text.setWidthFull();

        addClassName("note-card");

        add(text);
    }
}
