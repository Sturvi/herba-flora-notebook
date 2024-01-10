package com.example.inovasiyanotebook.views.login;

import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
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
public class LoginView extends VerticalLayout {
    private final NavigationTools navigationTools;


    public LoginView(NavigationTools navigationTools) {
        this.navigationTools = navigationTools;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        var login = new LoginForm();
        login.setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Giriş");
        i18n.getForm().setUsername("İstifadəçi adı");
        i18n.getForm().setPassword("Şifrə");
        i18n.getForm().setSubmit("Daxil ol");
        i18n.getForm().setForgotPassword("");

        LoginI18n.ErrorMessage i18nErrorMessage = i18n.getErrorMessage();
        i18nErrorMessage.setTitle("İstifadəçi adı və ya şifrə doğru deyil");
        i18nErrorMessage.setMessage(
                "İstifadəçi adınızın və şifrənizin doğru olduğunu yoxlayın və yenidən cəhd edin.");
        i18n.setErrorMessage(i18nErrorMessage);


        login.setI18n(i18n);


        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(Alignment.CENTER);
        horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H1("Herba Flora"), new H3("İnnovasiya və təhlil"), login, horizontalLayout );
    }
}
