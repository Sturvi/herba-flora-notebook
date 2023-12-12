package com.example.inovasiyanotebook.views;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.client.ClientMapper;
import com.example.inovasiyanotebook.model.user.RoleEnum;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.ClientService;
import com.example.inovasiyanotebook.service.UserService;
import com.example.inovasiyanotebook.service.updateevent.ClientListUpdateCommandEvent;
import com.example.inovasiyanotebook.views.about.AboutView;
import com.example.inovasiyanotebook.views.helloworld.HelloWorldView;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Comparator;
import java.util.List;


/**
 * The MainLayout class is a component that serves as the main view for the application.
 * It extends the AppLayout class and provides the layout structure for the application's
 * header, footer, and drawer content.
 */
@Component
@UIScope
public class MainLayout extends AppLayout implements NavigationalTools, DesignTools{

    private final UserService userService;
    private final ClientMapper clientMapper;
    private final ClientService clientService;

    private final VerticalLayout clientsNav;
    private final User user;

    private H2 viewTitle;

    public MainLayout(UserService userService, ClientMapper clientMapper, ClientService clientService) {
        this.userService = userService;
        this.user = userService.findByUsername(getCurrentUsername());
        this.clientMapper = clientMapper;
        this.clientService = clientService;

        this.clientsNav = new VerticalLayout();
        clientsNav.setPadding(false);
        clientsNav.setMargin(false);
        clientsNav.setSpacing(false);


        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        updateClientNav();
    }

    private void updateClientNav() {
        clientsNav.removeAll();


        clientService.fetchAllClients().stream()
                .sorted(Comparator.comparing((Client::getSortOrder), Comparator.nullsLast(Integer::compareTo)))
                .forEach(client -> {
                    Anchor link = new Anchor(
                            ViewsEnum.CLIENT.getViewWithParameter(client.getId().toString()),
                            client.getName());
                    link.addClassNames("text");

                    if (user.getRole() == RoleEnum.ADMIN) {
                        Span dragHandle = new Span(LineAwesomeIcon.BARS_SOLID.create());
                        dragHandle.addClassNames("drag-handle");

                        HorizontalLayout itemLayout = new HorizontalLayout(dragHandle, link);
                        itemLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
                        itemLayout.setSpacing(true);

                        Div itemContainer = new Div(itemLayout);
                        itemContainer.addClassName("project-item");
                        itemContainer.setId("project-" + client.getId());

                        // Устанавливаем dragHandle как источник для перетаскивания
                        DragSource<Span> dragSource = DragSource.create(dragHandle);
                        dragSource.setDraggable(true); // Указываем, что именно dragHandle можно перетаскивать

                        dragSource.addDragStartListener(e -> {
                            // Сохраняем идентификатор перетаскиваемого проекта в сессии
                            VaadinSession.getCurrent().setAttribute("draggedClientId", client.getId());
                        });

                        // Настройка обработчика событий Drop
                        DropTarget<Div> dropTarget = DropTarget.create(itemContainer);
                        dropTarget.addDropListener(e -> {
                            Long draggedClientId = (Long) VaadinSession.getCurrent().getAttribute("draggedClientId");
                            var draggedClientOpt = clientService.findById(draggedClientId);

                            draggedClientOpt.ifPresent(draggedClient -> {
                                // Определяем проекты до точки сброса
                                Client previousClient = null;

                                List<Client> clients = clientService.fetchAllClients().stream()
                                        .sorted(Comparator.comparing(Client::getSortOrder))
                                        .toList();

                                for (int i = 0; i < clients.size(); i++) {
                                    Client selectedClient = clients.get(i);
                                    if (selectedClient.getId().equals(client.getId())) {
                                        if (i > 0) previousClient = clients.get(i - 1);
                                        break;
                                    }
                                }

                                clientService.updateClientPosition(previousClient, draggedClient);

                                updateClientNav();
                            });
                        });
                        clientsNav.add(itemContainer);
                    } else {
                        Div itemContainer = new Div(link);
                        itemContainer.addClassName("project-item");
                        itemContainer.setId("project-" + client.getId());

                        clientsNav.add(itemContainer);
                    }



                });
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("My App");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);


        addToDrawer(
                header,
                new SideNavItem("Hello World", HelloWorldView.class, LineAwesomeIcon.GLOBE_SOLID.create()),
                new SideNavItem("About", AboutView.class, LineAwesomeIcon.FILE.create()),
                addEmptySpace(),
                addTitle("Şirkətlər"),
                newClientButton(user),
                new Scroller(clientsNav),
                createFooter());
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private HtmlContainer addTitle(String title) {
        H3 projectsTitle = new H3(title);
        projectsTitle.getStyle().set("text-align", "center");

        return projectsTitle;
    }

    public Button newClientButton(User user) {
        if (user.getRole() != RoleEnum.ADMIN) {
            return null;
        }

        Button newClientButton = new Button("Yeni şirkət");
        newClientButton.addClickListener(event -> createAddClientDialog());

        return newClientButton;
    }

    private void createAddClientDialog() {
        Dialog addClientDialog = new Dialog();
        addClientDialog.setWidth("75%");

        TextField nameField = createTextField("Müştəri adı", ".*\\S.*", "Ad boş ola bilməz");
        TextField phoneNumberField = createTextField("Telefon nömrəsi", null, null);
        TextField emailField = createTextField("Email", "^$|^(.+)@(.+)$", "Email doğru deyil");
        TextField voenField = createTextField("Vöen", null, null);

        VerticalLayout layout = new VerticalLayout(nameField, phoneNumberField, emailField, voenField);

        Button addButton = new Button("Əlavə et");
        addButton.addClickListener(click -> processNewClient(nameField, phoneNumberField, emailField, voenField, addClientDialog));

        Button cancelButton = new Button("Ləğv et");
        cancelButton.addClickListener(event -> addClientDialog.close());

        HorizontalLayout horizontalLayout = new HorizontalLayout(addButton, cancelButton);
        horizontalLayout.setWidth("50%");
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        layout.add(horizontalLayout);
        addClientDialog.add(layout);
        addClientDialog.open();
    }

    private TextField createTextField(String label, String pattern, String errorMessage) {
        TextField textField = new TextField();
        textField.setLabel(label);
        textField.setSizeFull();
        if (pattern != null) {
            textField.setPattern(pattern);
            textField.setErrorMessage(errorMessage);
        }
        return textField;
    }

    private void processNewClient(TextField nameField, TextField phoneNumberField, TextField emailField, TextField voenField, Dialog dialog) {
        String name = nameField.getValue().trim();
        if (name.isEmpty()) {
            nameField.setInvalid(true);
            return;
        }

        if (!emailField.getValue().matches("^$|^(.+)@(.+)$")) {
            emailField.setInvalid(true);
            return;
        }

        Client newClient = clientMapper.creatNewClient(name, emailField.getValue().trim(), phoneNumberField.getValue().trim(), voenField.getValue().trim());
        clientService.save(newClient);
        updateClientNav();
        dialog.close();
    }

    @EventListener
    public void waitingUpdateCommand(ClientListUpdateCommandEvent event) {
        updateClientNav();
    }
}
