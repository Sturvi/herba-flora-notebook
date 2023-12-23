package com.example.inovasiyanotebook.service.viewservices.client;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.interfaces.NamedEntity;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.ClientService;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.updateevent.ClientListUpdateCommandEventPublisher;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * This class represents a service for fetching and displaying client information.
 */
@Service
@RequiredArgsConstructor
@UIScope
public class ClientInformation {

    private final ClientService clientService;
    private final DesignTools designTools;
    private final PermissionsCheck permissionsCheck;
    private final ClientListUpdateCommandEventPublisher clientListUpdateCommandEventPublisher;


    /**
     * Retrieves the information for a given client and displays it in a vertical layout.
     *
     * @param client The client object for which the information needs to be retrieved.
     * @return A VerticalLayout object containing the client information.
     */
    public VerticalLayout getInformation(Client client, User user) {
        VerticalLayout verticalLayout = new VerticalLayout();

        if (permissionsCheck.needEditor(user)) {
            designTools.addEditableField(client, verticalLayout, "Telefon nömrəsi:", client.getPhoneNumber(),
                    "^\\+(?:[0-9] ?){6,14}[0-9]$", "Telefon nömrəsi doğru yazılmayıb",this::updatePhoneNumber);

            designTools.addEditableField(client, verticalLayout, "Email:", client.getEmail(),
                    "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", "Email doğru yazılmayıb",this::updateEmail);

            designTools.addEditableField(client, verticalLayout, "VÖEN:", client.getVoen(),
                    "^\\d+$", "VÖEN yalnız rəgəmlərdən ibarət ola bilər", this::updateVoen);
        } else {
            verticalLayout.add(
                    new H4("Telefon nömrəsi: " + client.getPhoneNumber()),
                    new H4("Email: " + client.getEmail()),
                    new H4("VÖEN: " + client.getVoen())
            );
        }

        return verticalLayout;
    }

    public HorizontalLayout getClientName(Client client, User user) {
        HorizontalLayout horizontalLayout = designTools.getNameLine(
                client,
                user,
                clientService,
                this::updateClientName
        );

/*        horizontalLayout.setSpacing(false);
        horizontalLayout.setMargin(false);
        horizontalLayout.setPadding(false);*/

        return horizontalLayout;
    }

    private void updateClientName(NamedEntity abstractEntity, TextField titleEditor, Component title) {
        Client client = (Client) abstractEntity;

        String newName = titleEditor.getValue().trim();
        if (!newName.isEmpty()) {
            client.setName(newName);
            clientService.create(client);
            ((H1) title).setText(newName);
        }
        title.setVisible(true);
        titleEditor.setVisible(false);
        clientListUpdateCommandEventPublisher.updateClientList();
    }

    private void updatePhoneNumber(Client client, TextField phoneField, Component phoneNumber) {
        String newPhone = phoneField.getValue().trim();
        H4 phoneNumberH4 = (H4) phoneNumber;

        if (newPhone.isEmpty()) {
            designTools.setEmptyFieldStyle(phoneNumberH4, "Əlavə et");
            client.setPhoneNumber("");
            clientService.create(client);
        } else if (newPhone.matches("^\\+(?:[0-9] ?){6,14}[0-9]$")) {
            client.setPhoneNumber(newPhone);
            clientService.create(client);
            designTools.resetFieldStyle(phoneNumberH4, newPhone);
        } else {
            phoneField.setInvalid(true);
            phoneField.focus();
            return;
        }

        phoneField.setVisible(false);
        phoneNumber.setVisible(true);
    }

    private void updateEmail(Client client, TextField emailField, Component email) {
        String newEmail = emailField.getValue().trim();
        H4 emailH4 = (H4) email;

        if (newEmail.isEmpty()) {
            designTools.setEmptyFieldStyle(emailH4, "Əlavə et");
            client.setEmail("");
            clientService.create(client);
        } else if (newEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            client.setEmail(newEmail);
            clientService.create(client);
            designTools.resetFieldStyle(emailH4, newEmail);
        } else {
            emailField.setInvalid(true);
            emailField.focus();
            return;
        }

        emailField.setVisible(false);
        email.setVisible(true);
    }

    private void updateVoen(Client client, TextField voenField, Component voen) {
        String newVoen = voenField.getValue().trim();
        H4 voenH4 = (H4) voen;

        if (newVoen.isEmpty()) {
            designTools.setEmptyFieldStyle(voenH4, "Əlavə et");
            client.setVoen("");
            clientService.create(client);
        } else if (newVoen.matches("^\\d+$")) {
            client.setVoen(newVoen);
            clientService.create(client);
            designTools.resetFieldStyle(voenH4, newVoen);
        } else {
            voenField.setInvalid(true);
            voenField.focus();
            return;
        }

        voenField.setVisible(false);
        voen.setVisible(true);
    }




}
