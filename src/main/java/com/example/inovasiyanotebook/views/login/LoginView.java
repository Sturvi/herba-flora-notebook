package com.example.inovasiyanotebook.views.login;

import com.example.inovasiyanotebook.views.NavigationalTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


/**
 * Represents the login view of the application.
 *
 * This view is responsible for displaying the login form and allowing users to login or navigate to the registration view.
 * It extends the VerticalLayout class and implements the NavigationalTools interface.
 *
 * The login form is displayed at the center of the view, along with a "Register" button for navigation to the registration view.
 */
@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements NavigationalTools {


    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        var login = new LoginForm();
        login.setAction("login");

        Button registerButton = new Button("Register", event -> {
            navigateTo(ViewsEnum.REGISTRATION);
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(new H4("Don`t have account?"), registerButton);
        horizontalLayout.setAlignItems(Alignment.CENTER);
        horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H1("Todo app"), login, horizontalLayout );
    }
}
