package com.example.inovasiyanotebook.views.client;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.client.ClientInformation;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.ClientService;
import com.example.inovasiyanotebook.service.UserService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationalTools;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.transaction.annotation.Transactional;

@PageTitle("Müştəri")
@Route(value = "client", layout = MainLayout.class)
@PermitAll
public class ClientView extends HorizontalLayout implements HasUrlParameter<String>, NavigationalTools, DesignTools {
    private Client client;
    private final ClientService clientService;
    private final UserService userService;
    private final ClientInformation clientInformation;
    private User user;

    public ClientView(ClientService clientService, UserService userService, ClientInformation clientInformation) {
        this.clientService = clientService;
        this.userService = userService;
        this.user = userService.findByUsername(getCurrentUsername());
        this.clientInformation = clientInformation;
    }


    @Override
    @Transactional
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String clientId) {
        removeAll();
        if (clientId == null) {
            handleHaventClient();
        } else {
            var clientOpt = clientService.findById(Long.parseLong(clientId));
            clientOpt.ifPresentOrElse(this::handleHasClient, this::handleHaventClient);
        }
    }

    private void handleHasClient(Client clientFromOpt) {
        this.client = clientFromOpt;

        var information = clientInformation.getInformation(client, user);

        VerticalLayout productsLayout = new VerticalLayout(new H3("Products"));
        client.getProducts().forEach(product -> productsLayout.add(new Span(product.getName())));

        VerticalLayout notesLayout = new VerticalLayout(new H3("Notes"));
        client.getNote().forEach(note -> notesLayout.add(new Span(note.getText())));

        VerticalLayout clientLayout = new VerticalLayout(
                clientInformation.getClientName(client, user),
                information,
                productsLayout,
                notesLayout
        );
        clientLayout.getStyle().set("margin-left", "20px");
        clientLayout.getStyle().set("margin-top", "20px");

        add(clientLayout);
    }

    private void handleHaventClient() {
        H1 title = new H1("Bütün müştərilər");
        HorizontalLayout layout = new HorizontalLayout(title);
        layout.getStyle().set("margin-left", "20px");
        layout.getStyle().set("margin-top", "20px");

        add(layout);
    }
}
