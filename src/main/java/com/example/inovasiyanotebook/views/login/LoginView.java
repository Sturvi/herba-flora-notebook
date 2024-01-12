package com.example.inovasiyanotebook.views.login;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;


/**
 * The LoginView class represents the login view of the application.
 * This view is responsible for displaying the login form and handling user authentication.
 * The view is annotated with the @Route annotation to specify the route path ("/login").
 * It is also annotated with the @AnonymousAllowed annotation to allow anonymous access to the view.
 * The view extends the VerticalLayout class, which is a layout component that arranges its child components vertically.
 */
@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {


    /**
     * The LoginView class represents the login view of the application.
     * This view is responsible for displaying the login form and handling user authentication.
     * The view is annotated with the @Route annotation to specify the route path ("/login").
     * It is also annotated with the @AnonymousAllowed annotation to allow anonymous access to the view.
     * The view extends the VerticalLayout class, which is a layout component that arranges its child components vertically.
     */
    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        var login = new LoginForm();
        login.setAction("login");
        login.setI18n(getLoginI18n());

        add(getHeaderLayout(), login);
    }

    /**
     * Retrieves the login internationalization settings.
     *
     * @return The LoginI18n object containing the login form title, username, password, submit button text, and error message settings.
     */
    private static LoginI18n getLoginI18n() {
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
        return i18n;
    }

    /**
     * Retrieves the header layout for the login view.
     *
     * @return The header layout containing the logo image.
     */
    private VerticalLayout getHeaderLayout() {
        VerticalLayout headerLayout = new VerticalLayout();
        StreamResource imageResource = new StreamResource("inovasiya_logo.svg",
                () -> getClass().getResourceAsStream("/META-INF/resources/images/inovasiya_logo.svg"));

        Image logo = new Image(imageResource, "Innovasiya ve Tehlil");

        logo.setWidth("300px");
        headerLayout.add(logo);


        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        return headerLayout;
    }
}
