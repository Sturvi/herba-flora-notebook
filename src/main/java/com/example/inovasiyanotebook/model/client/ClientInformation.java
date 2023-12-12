package com.example.inovasiyanotebook.model.client;

import com.example.inovasiyanotebook.model.user.RoleEnum;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.ClientService;
import com.example.inovasiyanotebook.service.PermissionsCheck;
import com.example.inovasiyanotebook.service.updateevent.ClientListUpdateCommandEventPublisher;
import com.example.inovasiyanotebook.views.NavigationalTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.stereotype.Service;


/**
 * This class represents a service for fetching and displaying client information.
 */
@Service
@RequiredArgsConstructor
@UIScope
public class ClientInformation implements NavigationalTools, PermissionsCheck {

    private final ClientService clientService;
    private final ClientListUpdateCommandEventPublisher clientListUpdateCommandEventPublisher;


    /**
     * Retrieves the information for a given client and displays it in a vertical layout.
     *
     * @param client The client object for which the information needs to be retrieved.
     * @return A VerticalLayout object containing the client information.
     */
    public VerticalLayout getInformation(Client client, User user) {
        VerticalLayout verticalLayout = new VerticalLayout();

        if (needEditor(user.getRole())) {
            addEditableField(client, verticalLayout, "Telefon nömrəsi:", client.getPhoneNumber(),
                    "^\\+(?:[0-9] ?){6,14}[0-9]$", "Telefon nömrəsi doğru yazılmayıb",this::updatePhoneNumber);

            addEditableField(client, verticalLayout, "Email:", client.getEmail(),
                    "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", "Email doğru yazılmayıb",this::updateEmail);

            addEditableField(client, verticalLayout, "VÖEN:", client.getVoen(),
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
        H1 title = new H1(client.getName());
        HorizontalLayout titleLine = new HorizontalLayout(title);

        if (needEditor(user.getRole())) {
            TextField titleEditor = createEditableField(client, title, "", "", this::updateClientName);
            titleEditor.addClassName("my-text-field");
            titleLine.add(titleEditor);
            titleEditor.setWidthFull();

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH)); // Используем иконку корзины
            deleteButton.addClickListener(e -> deleteProject(client));

            deleteButton.addClassNames("text-error", "icon-error", "small-button");

            titleLine.add(deleteButton);

            titleLine.setAlignItems(FlexComponent.Alignment.CENTER);
        }

        return titleLine;
    }

    private void deleteProject(Client client) {
        clientService.deleteClient(client);
        clientListUpdateCommandEventPublisher.updateClientList();
        navigateTo(ViewsEnum.CLIENT);
    }

    private void updateClientName(Client client, TextField titleEditor, Component title) {
        String newName = titleEditor.getValue().trim();
        if (!newName.isEmpty()) {
            client.setName(newName);
            clientService.save(client);
            ((H1) title).setText(newName);
        }
        title.setVisible(true);
        titleEditor.setVisible(false);
        clientListUpdateCommandEventPublisher.updateClientList();
    }


    private TextField createEditableField(Client client, Component displayComponent, String regex, String errorMessage, TriConsumer<Client, TextField, com.vaadin.flow.component.Component> updateFunction) {
        TextField editField = new TextField();
        editField.setVisible(false);
        editField.setPattern(regex);
        editField.setErrorMessage(errorMessage);
        editField.setWidth("250px");

        editField.addBlurListener(e -> updateFunction.accept(client, editField, displayComponent));
        editField.addKeyDownListener(Key.ENTER, e -> updateFunction.accept(client, editField, displayComponent));
        editField.addKeyDownListener(Key.ESCAPE, e -> {
            displayComponent.setVisible(true);
            editField.setVisible(false);
        });


        displayComponent.getElement().addEventListener("click", e -> {
            String informationText = ((HtmlContainer) displayComponent).getText();
            editField.setValue(informationText.equals("Əlavə et") ? "" : informationText);
            displayComponent.setVisible(false);
            editField.setVisible(true);
            editField.focus();
        });

        return editField;
    }

    private void addEditableField(Client client, VerticalLayout layout, String titleText, String value, String regex, String errorMessage, TriConsumer<Client, TextField, com.vaadin.flow.component.Component> updateFunction) {
        H4 title = new H4(titleText);
        H4 displayValue = getInformationH4(value);
        TextField editField = createEditableField(client, displayValue, regex, errorMessage, updateFunction);

        HorizontalLayout line = new HorizontalLayout(title, displayValue, editField);
        line.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER); // Центрирование элементов по вертикали
        line.setFlexGrow(1, title); // Растягивание заголовка для выравнивания остальных элементов
        layout.add(line);
    }

    private void updatePhoneNumber(Client client, TextField phoneField, Component phoneNumber) {
        String newPhone = phoneField.getValue().trim();
        H4 phoneNumberH4 = (H4) phoneNumber;

        if (newPhone.isEmpty()) {
            setEmptyFieldStyle(phoneNumberH4, "Əlavə et");
            client.setPhoneNumber("");
            clientService.save(client);
        } else if (newPhone.matches("^\\+(?:[0-9] ?){6,14}[0-9]$")) {
            client.setPhoneNumber(newPhone);
            clientService.save(client);
            resetFieldStyle(phoneNumberH4, newPhone);
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
            setEmptyFieldStyle(emailH4, "Əlavə et");
            client.setEmail("");
            clientService.save(client);
        } else if (newEmail.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            client.setEmail(newEmail);
            clientService.save(client);
            resetFieldStyle(emailH4, newEmail);
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
            setEmptyFieldStyle(voenH4, "Əlavə et");
            client.setVoen("");
            clientService.save(client);
        } else if (newVoen.matches("^\\d+$")) {
            client.setVoen(newVoen);
            clientService.save(client);
            resetFieldStyle(voenH4, newVoen);
        } else {
            voenField.setInvalid(true);
            voenField.focus();
            return;
        }

        voenField.setVisible(false);
        voen.setVisible(true);
    }

    private H4 getInformationH4(String information) {
        H4 informationElement = new H4(information.isEmpty() ? "Əlavə et" : information);
        if (information.isEmpty()) {
            informationElement.getElement().getStyle().set("color", "#4fc3f7");
            informationElement.getElement().getClassList().add("italic");
        }
        return informationElement;
    }

    private void setEmptyFieldStyle(H4 field, String text) {
        field.setText(text);
        field.getElement().getStyle().set("color", "#4fc3f7");
        field.getElement().getClassList().add("italic");
    }

    private void resetFieldStyle(H4 field, String text) {
        field.setText(text);
        field.getElement().getStyle().remove("color");
        field.getElement().getClassList().remove("italic");
    }
}
