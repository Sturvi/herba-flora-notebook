package com.example.inovasiyanotebook.service.viewservices.client;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.client.ClientMapper;
import com.example.inovasiyanotebook.service.entityservices.iml.ClientService;
import com.example.inovasiyanotebook.service.updateevent.ClientListUpdateCommandEventPublisher;
import com.example.inovasiyanotebook.views.DesignTools;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@UIScope
public class AddNewClientMenuService {
    private final DesignTools designTools;
    private final ClientService clientService;
    private final ClientMapper clientMapper;
    private final ClientListUpdateCommandEventPublisher clientListUpdateEventPublisher;
    private VerticalLayout mobileView;
    private VerticalLayout desktopView;


    public void createAddClientDialog() {
        Dialog addClientDialog = new Dialog();
        addClientDialog.setWidth("75%");

        createDesktopView(addClientDialog);
        createMobileView(addClientDialog);

        designTools.addResponsive(desktopView, mobileView);

        addClientDialog.add(desktopView, mobileView);
        addClientDialog.open();
    }

    private void createDesktopView(Dialog addClientDialog) {
        ViewComponents viewComponents = new ViewComponents();
        viewComponents.createNewViewComponents(addClientDialog);

        HorizontalLayout firstLine = new HorizontalLayout(viewComponents.getNameField(), viewComponents.getPhoneNumberField());
        HorizontalLayout secondLine = new HorizontalLayout(viewComponents.getEmailField(), viewComponents.getVoenField());
        HorizontalLayout thirdLine = new HorizontalLayout(viewComponents.getAddButton(), viewComponents.getCancelButton());

        firstLine.setWidthFull();
        secondLine.setWidthFull();
        thirdLine.setWidthFull();

        desktopView = new VerticalLayout(firstLine, secondLine, thirdLine);
        desktopView.setWidthFull();
        desktopView.setVisible(false);
    }

    private void createMobileView(Dialog addClientDialog) {
        ViewComponents viewComponents = new ViewComponents();
        viewComponents.createNewViewComponents(addClientDialog);

        HorizontalLayout buttonLine = new HorizontalLayout(viewComponents.getAddButton(), viewComponents.getCancelButton());

        mobileView = new VerticalLayout(
                viewComponents.getNameField(),
                viewComponents.getPhoneNumberField(),
                viewComponents.getEmailField(),
                viewComponents.getVoenField(),
                buttonLine);
        mobileView.setWidthFull();
        mobileView.setVisible(false);
    }

    private void processNewClient(TextField nameField, TextField phoneNumberField, TextField emailField, TextField voenField, Dialog dialog) {
        String name = nameField.getValue().trim();
        if (name.isEmpty()) {
            nameField.setInvalid(true);
            return;
        }

        if (!emailField.getValue().isEmpty() && !emailField.getValue().matches(emailField.getPattern())) {
            emailField.setInvalid(true);
            return;
        }

        if (!phoneNumberField.getValue().isEmpty() && !phoneNumberField.getValue().matches(phoneNumberField.getPattern())) {
            phoneNumberField.setInvalid(true);
            return;
        }

        if (!voenField.getValue().isEmpty() && !voenField.getValue().matches(voenField.getPattern())) {
            voenField.setInvalid(true);
            return;
        }

        Client newClient = clientMapper.creatNewClient(name, emailField.getValue().trim(), phoneNumberField.getValue().trim(), voenField.getValue().trim());
        clientService.create(newClient);
        clientListUpdateEventPublisher.updateClientList();
        dialog.close();
    }


    @Getter
    private class ViewComponents {
        private TextField nameField;
        private TextField phoneNumberField;
        private TextField emailField;
        private TextField voenField;
        private Button addButton;
        private Button cancelButton;

        public void createNewViewComponents(Dialog addClientDialog) {
            nameField = designTools.createTextField("Müştəri adı", ".*\\S.*", "Ad boş ola bilməz");
            phoneNumberField = designTools.createTextField("Telefon nömrəsi", "^\\+(?:[0-9] ?){6,14}[0-9]$", "Telefon nömrəsi doğru yazılmayıb");
            emailField = designTools.createTextField("Email", "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", "Email doğru deyil");
            voenField = designTools.createTextField("Vöen", "^\\d+$", "VÖEN yalnız rəgəmlərdən ibarət ola bilər");

            addButton = new Button("Əlavə et");
            addButton.addClickListener(click -> processNewClient(nameField, phoneNumberField, emailField, voenField, addClientDialog));

            cancelButton = new Button("Ləğv et");
            cancelButton.addClickListener(event -> addClientDialog.close());
        }
    }
}
