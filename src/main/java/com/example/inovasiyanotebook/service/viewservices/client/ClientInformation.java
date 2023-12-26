package com.example.inovasiyanotebook.service.viewservices.client;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.interfaces.NamedEntity;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.ClientService;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.updateevent.ClientListUpdateCommandEventPublisher;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
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
    public Component getInformation(Client client, User user) {
        VerticalLayout verticalLayout = new VerticalLayout();

        if (permissionsCheck.needEditor(user)) {
            designTools.addEditableField(client, verticalLayout, "Telefon nömrəsi:", client.getPhoneNumber(),
                    "^\\+(?:[0-9] ?){6,14}[0-9]$|^$", "Telefon nömrəsi doğru yazılmayıb", this::updatePhoneNumber);

            designTools.addEditableField(client, verticalLayout, "Email:", client.getEmail(),
                    "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$|^$", "Email doğru yazılmayıb", this::updateEmail);

            designTools.addEditableField(client, verticalLayout, "VÖEN:", client.getVoen(),
                    "^\\d+$|^$", "VÖEN yalnız rəgəmlərdən ibarət ola bilər", this::updateVoen);
        } else {
            verticalLayout.add(
                    new H4("Telefon nömrəsi: " + client.getPhoneNumber()),
                    new H4("Email: " + client.getEmail()),
                    new H4("VÖEN: " + client.getVoen())
            );
        }

        verticalLayout.getStyle().set("font-weight", "100");
        return verticalLayout;
    }

    public HorizontalLayout getClientName(Client client, User user) {

        return designTools.getNameLine(
                client,
                user,
                clientService,
                this::updateClientName
        );
    }


    private void updateClientName(NamedEntity entity, String text) {
        Client client = (Client) entity;

        if (!text.isEmpty()) {
            client.setName(text);
            clientService.update(client);
        }

        clientListUpdateCommandEventPublisher.updateClientList();
    }

    private void updatePhoneNumber(NamedEntity entity, String newPhoneNumber) {
        Client client = (Client) entity;

        client.setPhoneNumber(newPhoneNumber);
        clientService.update(client);
    }

    private void updateEmail(NamedEntity entity, String newEmail) {
        Client client = (Client) entity;

        client.setEmail(newEmail);
        clientService.update(client);
    }

    private void updateVoen(NamedEntity entity, String newVoen) {
        Client client = (Client) entity;

        client.setVoen(newVoen);
        clientService.update(client);
    }


}
