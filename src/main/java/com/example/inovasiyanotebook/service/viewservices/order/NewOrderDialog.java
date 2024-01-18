package com.example.inovasiyanotebook.service.viewservices.order;

import com.example.inovasiyanotebook.model.order.Order;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UIScope
public class NewOrderDialog {

    private final NavigationTools navigationTools;
    private final OrderComponentsFactory orderComponentsFactory;

    public void openNewDialog() {
        openNewDialog(null);
    }

    public void openNewDialog(Order order) {
        Dialog dialog = createDialog();
        VerticalLayout dialogLayout = createDialogLayout();

        OrderComponents orderComponents = orderComponentsFactory.getNewBean();

        if (order != null) {
            orderComponents.setOrder(order);
        }

        dialogLayout.add(orderComponents.getLayout());

        Button saveButton = new Button(order == null ? "Əlavə et" : "Yenilə");
        saveButton.addClickListener(buttonClickEvent -> {
            boolean entitySaved = orderComponents.save();
            if (entitySaved) {
                navigationTools.reloadPage();
            }
        });

        Button cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(buttonClickEvent -> dialog.close());

        dialogLayout.add(new HorizontalLayout(saveButton, cancelButton));

        dialog.add(dialogLayout);
        dialog.open();
    }

    public void openReadOnlyDialog(Order order, User user) {
        Dialog dialog = createDialog();
        VerticalLayout dialogLayout = createDialogLayout();

        OrderComponents orderComponents = orderComponentsFactory.getNewBean();

        if (order != null) {
            orderComponents.setOrder(order);
            orderComponents.readOnly(true);
            orderComponents.addNotesButton(user);
        }

        dialogLayout.add(orderComponents.getLayout());

        dialog.add(dialogLayout);
        dialog.open();
    }

    private Dialog createDialog() {
        //dialog.setHeightFull();
        return new Dialog();
    }

    private VerticalLayout createDialogLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setHeightFull();
        return layout;
    }
}
