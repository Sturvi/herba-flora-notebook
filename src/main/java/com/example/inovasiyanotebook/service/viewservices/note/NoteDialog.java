package com.example.inovasiyanotebook.service.viewservices.note;

import com.example.inovasiyanotebook.model.interfaces.Noteable;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@UIScope
public class NoteDialog {

    private final NoteGridService noteGridService;
    private final DesignTools designTools;


    public void openDialog(Noteable noteble, User user) {
        Dialog dialog = new Dialog();
        dialog.setHeightFull();
        dialog.setMinWidth("700px");
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        var notesLayout = noteGridService.getVerticalGridWithHeader(noteble, user);

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidthFull(); // Задаем ширину на всю доступную ширину
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END); // Выравнивание содержимого справа

        Button closeButton = designTools.getNewIconButton(new Icon("lumo", "cross"), dialog::close);
        buttonsLayout.add(closeButton);
        dialog.getHeader().add(buttonsLayout);

        notesLayout.getStyle().set("margin-left", "0px");
        dialog.add(notesLayout);
        dialog.setHeightFull();
        dialog.open();
    }
}
