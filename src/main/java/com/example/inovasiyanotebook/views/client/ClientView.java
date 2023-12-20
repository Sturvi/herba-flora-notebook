package com.example.inovasiyanotebook.views.client;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.service.viewservices.client.ClientInformation;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.ClientService;
import com.example.inovasiyanotebook.service.viewservices.product.AddNewProductViewService;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.service.viewservices.product.ProductsGridService;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.transaction.annotation.Transactional;

@PageTitle("")
@Route(value = "client", layout = MainLayout.class)
@PermitAll
public class ClientView extends HorizontalLayout implements HasUrlParameter<String> {
    private Client client;
    private final ClientService clientService;
    private final ProductsGridService productsGridServise;
    private final PermissionsCheck permissionsCheck;
    private final UserService userService;
    private final ClientInformation clientInformation;
    private final AddNewProductViewService addNewProductViewService;
    private final NavigationTools navigationTools;
    private User user;

    public ClientView(ClientService clientService, ProductsGridService productsGridServise, PermissionsCheck permissionsCheck, UserService userService, ClientInformation clientInformation, AddNewProductViewService addNewProductViewService, NavigationTools navigationTools) {
        this.clientService = clientService;
        this.productsGridServise = productsGridServise;
        this.permissionsCheck = permissionsCheck;
        this.userService = userService;
        this.user = userService.findByUsername(navigationTools.getCurrentUsername());
        this.clientInformation = clientInformation;
        this.addNewProductViewService = addNewProductViewService;
        this.navigationTools = navigationTools;
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




/*        VerticalLayout notesLayout = new VerticalLayout(new H3("Notes"));
        client.getNote().forEach(note -> notesLayout.add(new Span(note.getText())));*/

        VerticalLayout clientLayout = new VerticalLayout(
                clientInformation.getClientName(client, user),
                information,
                productsGridServise.getProductGrid(client, user)
        );
        clientLayout.setWidthFull();

/*        clientLayout.getStyle().set("margin-left", "20px");
        clientLayout.getStyle().set("margin-top", "20px");*/
/*        setPadding(true);
        setMargin(true);*/
        setHeightFull();

        add(clientLayout, new VerticalLayout());
    }

    private void handleHaventClient() {
        H1 title = new H1("Bütün müştərilər");
        HorizontalLayout layout = new HorizontalLayout(title);
        layout.getStyle().set("margin-left", "20px");
        layout.getStyle().set("margin-top", "20px");

        add(layout);
    }
}
