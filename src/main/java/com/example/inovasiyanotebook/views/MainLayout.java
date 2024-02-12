package com.example.inovasiyanotebook.views;

import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.client.ClientMapper;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.viewservices.client.AddNewClientMenuService;
import com.example.inovasiyanotebook.service.entityservices.iml.ClientService;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.service.updateevent.ClientListUpdateCommandEvent;
import com.example.inovasiyanotebook.service.viewservices.order.worddocument.UploadComponentCreator;
import com.example.inovasiyanotebook.views.category.CategoryView;
import com.example.inovasiyanotebook.views.order.OrderView;
import com.example.inovasiyanotebook.views.ordermapping.ProductMappingView;
import com.example.inovasiyanotebook.views.product.ProductView;
import com.example.inovasiyanotebook.views.user.UserView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Comparator;
import java.util.List;


/**
 * The MainLayout class is a component that serves as the main view for the application.
 * It extends the AppLayout class and provides the layout structure for the application's
 * header, footer, and drawer content.
 */
@Service
@UIScope
public class MainLayout extends AppLayout{

    private final UserService userService;
    private final PermissionsCheck permissionsCheck;
    private final DesignTools designTools;
    private final NavigationTools navigationTools;
    private final ClientMapper clientMapper;
    private final ClientService clientService;
    private final AddNewClientMenuService addNewClientMenuService;
    private final VerticalLayout clientsNav;
    private final UploadComponentCreator uploadComponentCreator;
    private final User user;

    private H2 viewTitle;

    public MainLayout(UserService userService, PermissionsCheck permissionsCheck, DesignTools designTools, NavigationTools navigationTools, ClientMapper clientMapper, ClientService clientService, AddNewClientMenuService addNewClientMenuService, UploadComponentCreator uploadComponentCreator) {
        this.userService = userService;
        this.uploadComponentCreator = uploadComponentCreator;
        this.user = userService.findByUsername(navigationTools.getCurrentUsername());
        this.permissionsCheck = permissionsCheck;
        this.designTools = designTools;
        this.navigationTools = navigationTools;
        this.clientMapper = clientMapper;
        this.clientService = clientService;
        this.addNewClientMenuService = addNewClientMenuService;

        this.clientsNav = new VerticalLayout();
        clientsNav.setPadding(false);
        clientsNav.setMargin(false);
        clientsNav.setSpacing(false);


        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        updateClientNav();
    }

    private void addDrawerContent() {
        addToDrawer(
                getHeader(),
                new SideNavItem("Kateqoriyalar", CategoryView.class, LineAwesomeIcon.LIST_ALT.create()),
                new SideNavItem("Məhsullar", ProductView.class, LineAwesomeIcon.SHOPPING_CART_SOLID.create()),
                new SideNavItem("Sifarişlər", OrderView.class, LineAwesomeIcon.CLIPBOARD_LIST_SOLID.create()),
                permissionsCheck.isAdminOrHigher(user) ? new SideNavItem("İstifadəçilər", UserView.class, LineAwesomeIcon.USERS_SOLID.create()) : null,
                permissionsCheck.isAdminOrHigher(user) ? new SideNavItem("1C eyniləşdirmə", ProductMappingView.class, LineAwesomeIcon.EXCHANGE_ALT_SOLID.create()) : null,
                designTools.addEmptySpace(),
                addTitle("Müştərilər"),
                newClientButton(user),
                new Scroller(clientsNav),
                getIsFunctionalityEnabledButton(),
                createFooter());
    }

    private static Header getHeader() {
        Image logo = new Image("images/inovasiya_logo.svg", "Innovasiya ve Tehlil");
        logo.setWidthFull(); // Задайте ширину
        return new Header(logo);
    }


    private void updateClientNav() {
        clientsNav.removeAll();


        clientService.fetchAllClients().stream()
                .sorted(Comparator.comparing((Client::getSortOrder), Comparator.nullsLast(Integer::compareTo)))
                .forEach(this::extracted);
    }

    private void extracted(Client client) {
        Anchor link = new Anchor(
                ViewsEnum.CLIENT.getViewWithParameter(client.getId().toString()),
                client.getName());
        link.addClassNames("text");

        if (permissionsCheck.needEditor(user)) {
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
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
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
        if (permissionsCheck.needEditor(user)) {
            Button newClientButton = new Button("Yeni şirkət");
            newClientButton.addClickListener(event -> addNewClientMenuService.createAddClientDialog());

            return newClientButton;
        }

        return null;
    }


    private Component getIsFunctionalityEnabledButton() {
        var upload = uploadComponentCreator.getUpload();
        upload.setDropAllowed(true);


        if (permissionsCheck.isEditorOrHigher(user)) {
            Checkbox toggleButton = new Checkbox("Admin funksiyaları");
            toggleButton.setValue(user.isFunctionalityEnabled());
            toggleButton.addClassName("custom-checkbox"); // Добавление кастомного класса

            toggleButton.addValueChangeListener(event -> {
                user.setFunctionalityEnabled(event.getValue());
                userService.update(user);
                navigationTools.reloadPage();
            });

            return new VerticalLayout(upload, toggleButton);
        }

        return null;
    }

    @EventListener
    public void waitingUpdateCommand(ClientListUpdateCommandEvent event) {
        updateClientNav();
    }
}
