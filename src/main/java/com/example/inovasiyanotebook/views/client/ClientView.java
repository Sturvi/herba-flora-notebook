package com.example.inovasiyanotebook.views.client;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.user.RoleEnum;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.ClientService;
import com.example.inovasiyanotebook.service.UserService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationalTools;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.transaction.annotation.Transactional;

@PageTitle("Müştəri")
@Route(value = "client", layout = MainLayout.class)
@PermitAll
public class ClientView extends HorizontalLayout implements HasUrlParameter<String>, NavigationalTools, DesignTools {
    private Client client;
    private final ClientService clientService;
    private final User user;
    private final UserService userService;

    public ClientView(ClientService clientService, UserService userService) {
        this.clientService = clientService;
        this.userService = userService;
        this.user = userService.findByUsername(getCurrentUsername());
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
        client = clientFromOpt;



        var phoneLayout = getInformationLine("Telefon nömrəsi:", client.getPhoneNumber());
        var emailLayout = getInformationLine("Email:", client.getEmail());
        var voenLayout = getInformationLine("VÖEN:", client.getVoen());


        TextField voenField = new TextField("VOEN", client.getVoen());
        voenField.setReadOnly(true);

        // Создание компонентов для отображения списков products и notes
        VerticalLayout productsLayout = new VerticalLayout();
        productsLayout.add(new H3("Products"));
        client.getProducts().forEach(product -> productsLayout.add(new Span(product.getName())));

        VerticalLayout notesLayout = new VerticalLayout();
        notesLayout.add(new H3("Notes"));
        client.getNote().forEach(note -> notesLayout.add(new Span(note.getText())));

        // Добавление компонентов на макет
        VerticalLayout clientLayout = new VerticalLayout();
        clientLayout.add(getTitleLine(), addEmptySpace(), phoneLayout, emailLayout, voenLayout, productsLayout, notesLayout);
        clientLayout.getStyle().set("margin-left", "20px");
        clientLayout.getStyle().set("margin-top", "20px");

        add(clientLayout);
    }

    private HorizontalLayout getTitleLine () {
        HorizontalLayout titleLine = new HorizontalLayout();

        H1 title = new H1(client.getName());
        titleLine.add(title);

        if (user.getRole() == RoleEnum.ADMIN) {
            TextField titleEditor = new TextField();
            titleEditor.addClassName("my-text-field");
            titleEditor.setVisible(false);
            titleEditor.setErrorMessage("Ad boş ola bilməz");
            titleEditor.addBlurListener(e -> updateClientName(titleEditor, title));
            titleEditor.addKeyDownListener(Key.ENTER, e -> updateClientName(titleEditor, title));

            title.addClickListener(e -> {
                titleEditor.setValue(client.getName());
                title.setVisible(false);
                titleEditor.setVisible(true);
                titleEditor.focus();
            });

            titleLine.add(titleEditor);
        }

        return titleLine;
    }

    private void updateClientName (TextField titleEditor, H1 title){
        if (titleEditor.getValue().trim().isEmpty()) {
            titleEditor.setInvalid(true);
            return;
        }

        client.setName(titleEditor.getValue().trim());
        clientService.save(client);
        title.setText(client.getName());
        title.setVisible(true);
        titleEditor.setVisible(false);
        //создать ивент для обновления в боковой панели
    }

    private HorizontalLayout getInformationLine (String title, String information) {
        H4 titleElement = new H4(title);
        titleElement.setWidth("150px");

        H4 informationElement;
        if (!information.isEmpty()) {
            informationElement = new H4(information);
        } else {
            informationElement = new H4("Əlavə et");
            informationElement.getElement().getStyle().set("color", "#4fc3f7");
            informationElement.getElement().getClassList().add("italic");
        }

        return new HorizontalLayout(titleElement, informationElement);
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
