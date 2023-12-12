package com.example.inovasiyanotebook.views.client;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.user.RoleEnum;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.ClientService;
import com.example.inovasiyanotebook.service.UserService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationalTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiConsumer;

@PageTitle("Müştəri")
@Route(value = "client", layout = MainLayout.class)
@PermitAll
public class ClientView extends HorizontalLayout implements HasUrlParameter<String>, NavigationalTools, DesignTools {
    private Client client;
    private final ClientService clientService;
    private final UserService userService;
    private User user;

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
        this.client = clientFromOpt;

        var information = getInformation();

        VerticalLayout productsLayout = new VerticalLayout(new H3("Products"));
        client.getProducts().forEach(product -> productsLayout.add(new Span(product.getName())));

        VerticalLayout notesLayout = new VerticalLayout(new H3("Notes"));
        client.getNote().forEach(note -> notesLayout.add(new Span(note.getText())));

        VerticalLayout clientLayout = new VerticalLayout(
                getTitleLine(),
                addEmptySpace(),
                information,
                productsLayout,
                notesLayout
        );
        clientLayout.getStyle().set("margin-left", "20px");
        clientLayout.getStyle().set("margin-top", "20px");

        add(clientLayout);
    }

    private HorizontalLayout getTitleLine() {
        H1 title = new H1(client.getName());
        HorizontalLayout titleLine = new HorizontalLayout(title);

        if (user.getRole() == RoleEnum.ADMIN) {
            TextField titleEditor = createEditableField(title, "", "", this::updateClientName);
            titleEditor.addClassName("my-text-field");
            titleLine.add(titleEditor);
            titleEditor.setWidthFull();

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH)); // Используем иконку корзины
            deleteButton.addClickListener(e -> deleteProject());

            deleteButton.addClassNames("text-error", "icon-error", "small-button");

            titleLine.add(deleteButton);

            titleLine.setAlignItems(Alignment.CENTER);
        }

        return titleLine;
    }

    private void deleteProject() {
        clientService.deleteClient(client);
        navigateTo(ViewsEnum.CLIENT);
    }

    private void updateClientName(TextField titleEditor, Component title) {
        String newName = titleEditor.getValue().trim();
        if (!newName.isEmpty()) {
            client.setName(newName);
            clientService.save(client);
            ((H1) title).setText(newName);
        }
        title.setVisible(true);
        titleEditor.setVisible(false);
    }

    private VerticalLayout getInformation() {
        VerticalLayout verticalLayout = new VerticalLayout();

        addEditableField(verticalLayout, "Telefon nömrəsi:", client.getPhoneNumber(),
                "^\\+(?:[0-9] ?){6,14}[0-9]$", "Telefon nömrəsi doğru yazılmayıb",this::updatePhoneNumber);

        addEditableField(verticalLayout, "Email:", client.getEmail(),
                "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", "Email doğru yazılmayıb",this::updateEmail);

        addEditableField(verticalLayout, "VÖEN:", client.getVoen(),
                "^\\d+$", "VÖEN yalnız rəgəmlərdən ibarət ola bilər", this::updateVoen);

        return verticalLayout;
    }

    private void addEditableField(VerticalLayout layout, String titleText, String value, String regex, String errorMessage, BiConsumer<TextField, Component> updateFunction) {
        H4 title = new H4(titleText);
        H4 displayValue = getInformationH4(value);
        TextField editField = createEditableField(displayValue, regex, errorMessage, updateFunction);

        HorizontalLayout line = new HorizontalLayout(title, displayValue, editField);
        line.setDefaultVerticalComponentAlignment(Alignment.CENTER); // Центрирование элементов по вертикали
        line.setFlexGrow(1, title); // Растягивание заголовка для выравнивания остальных элементов
        layout.add(line);
    }


    private TextField createEditableField(Component displayComponent, String regex, String errorMessage, BiConsumer<TextField, Component> updateFunction) {
        TextField editField = new TextField();
        editField.setVisible(false);
        editField.setPattern(regex);
        editField.setErrorMessage(errorMessage);
        editField.setWidth("250px");

        editField.addBlurListener(e -> updateFunction.accept(editField, displayComponent));
        editField.addKeyDownListener(Key.ENTER, e -> updateFunction.accept(editField, displayComponent));
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

    private void updatePhoneNumber(TextField phoneField, Component phoneNumber) {
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

    private void updateEmail(TextField emailField, Component email) {
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

    private void updateVoen(TextField voenField, Component voen) {
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


    private H4 getInformationH4(String information) {
        H4 informationElement = new H4(information.isEmpty() ? "Əlavə et" : information);
        if (information.isEmpty()) {
            informationElement.getElement().getStyle().set("color", "#4fc3f7");
            informationElement.getElement().getClassList().add("italic");
        }
        return informationElement;
    }

    private void handleHaventClient() {
        H1 title = new H1("Bütün müştərilər");
        HorizontalLayout layout = new HorizontalLayout(title);
        layout.getStyle().set("margin-left", "20px");
        layout.getStyle().set("margin-top", "20px");

        add(layout);
    }
}
