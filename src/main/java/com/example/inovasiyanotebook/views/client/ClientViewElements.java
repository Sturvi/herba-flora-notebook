package com.example.inovasiyanotebook.views.client;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.client.ClientMapper;
import com.example.inovasiyanotebook.model.user.RoleEnum;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.ClientService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientViewElements {
    private final ClientService clientService;
    private final ClientMapper clientMapper;


    public Button newClientButton(User user) {
        Button newClientButton = new Button("Yeni şirkət");

        if (user.getRole() != RoleEnum.ADMIN) {
            newClientButton.setVisible(false);
            return newClientButton;
        }

        newClientButton.addClickListener(event -> {
            Dialog addClientDialog = new Dialog();
            addClientDialog.setWidth("75%");

            TextField nameField = new TextField("Müştəri adı");
            nameField.setPattern(".*\\S.*");
            nameField.setErrorMessage("Ad boş ola bilməz");
            nameField.setRequiredIndicatorVisible(true);
            nameField.setSizeFull();
            TextField phoneNumberField = new TextField();
            phoneNumberField.setLabel("Telefon nömrəsi");
            phoneNumberField.setSizeFull();
            TextField emailField = new TextField();
            emailField.setLabel("Email");
            emailField.setSizeFull();
            TextField voenField = new TextField();
            voenField.setLabel("Vöen");
            voenField.setSizeFull();

            VerticalLayout layout = new VerticalLayout();
            layout.add(nameField, phoneNumberField, emailField, voenField);

            Button addButton = new Button("Əlavə et");
            addButton.addClickListener(click -> {
                String name = nameField.getValue();
                String phoneNumber = phoneNumberField.getValue();
                String email = emailField.getValue();
                String voen = voenField.getValue();

                if (name.isEmpty()) {
                    nameField.setInvalid(true);
                    return;
                }

                Client newClient = clientMapper.creatNewClient(name, email, phoneNumber, voen);

                clientService.save(newClient);

                addClientDialog.close();
            });

            Button cancelButton = new Button("Ləğv et", event1 -> addClientDialog.close());

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setWidth("50%");
            horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            horizontalLayout.add(addButton, cancelButton);


            layout.add(horizontalLayout);

            addClientDialog.add(layout);
            addClientDialog.open();
        });

        return newClientButton;
    }

}