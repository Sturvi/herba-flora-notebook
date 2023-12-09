package com.example.inovasiyanotebook.views;

import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.UserService;
import com.example.inovasiyanotebook.views.about.AboutView;
import com.example.inovasiyanotebook.views.client.ClientViewElements;
import com.example.inovasiyanotebook.views.helloworld.HelloWorldView;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.stereotype.Component;
import org.vaadin.lineawesome.LineAwesomeIcon;



/**
 * The MainLayout class is a component that serves as the main view for the application.
 * It extends the AppLayout class and provides the layout structure for the application's
 * header, footer, and drawer content.
 */
@Component
@UIScope
public class MainLayout extends AppLayout implements NavigationalTools{

    private UserService userService;
    private ClientViewElements clientViewElements;
    User user;

    private H2 viewTitle;

    public MainLayout(UserService userService, ClientViewElements clientViewElements) {
        this.userService = userService;
        this.user = userService.findByUsername(getCurrentUsername());
        this.clientViewElements = clientViewElements;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
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

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(
                header,
                new SideNavItem("Hello World", HelloWorldView.class, LineAwesomeIcon.GLOBE_SOLID.create()),
                new SideNavItem("About", AboutView.class, LineAwesomeIcon.FILE.create()),
                addEmptySpace(),
                addTitle("Şirkətlər"),
                clientViewElements.newClientButton(user),
                createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Hello World", HelloWorldView.class, LineAwesomeIcon.GLOBE_SOLID.create()));
        nav.addItem(new SideNavItem("About", AboutView.class, LineAwesomeIcon.FILE.create()));

        return nav;
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

    private HorizontalLayout addEmptySpace() {
        HorizontalLayout space = new HorizontalLayout();
        space.setWidthFull();
        space.setHeight("20px");

        return space;
    }
}
