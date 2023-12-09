package com.example.inovasiyanotebook.views.client;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.service.ClientService;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationalTools;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

@PageTitle("Müştəri")
@Route(value = "client", layout = MainLayout.class)
@PermitAll
public class ClientView extends HorizontalLayout implements HasUrlParameter<String>, NavigationalTools {
    private Client client;
    private ClientService clientService;

    public ClientView(ClientService clientService) {
        this.clientService = clientService;
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String clientId) {
        removeAll();
        if (clientId == null) {
            handleHaventClient();
        } else {
            var clientOpt = clientService.findById(Long.parseLong(clientId));
            clientOpt.ifPresentOrElse(this::handleHasClient, this::handleHaventClient);
        }

    }


    private void handleHasClient (Client clientFromOpt) {
        client = clientFromOpt;

        H1 title = new H1(client.getName());

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        horizontalLayout.add(title);
        horizontalLayout.getStyle().set("margin-left", "20px"); // Добавляет отступ слева
        horizontalLayout.getStyle().set("margin-top", "20px");

        add(horizontalLayout);
    }

    private void handleHaventClient () {
        H1 title = new H1("Bütün müştərilər");

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        horizontalLayout.add(title);
        horizontalLayout.getStyle().set("margin-left", "20px"); // Добавляет отступ слева
        horizontalLayout.getStyle().set("margin-top", "20px");

        add(horizontalLayout);
    }
}
