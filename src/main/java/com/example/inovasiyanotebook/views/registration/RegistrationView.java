package com.example.inovasiyanotebook.views.registration;

import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.UserService;
import com.example.inovasiyanotebook.views.NavigationalTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * RegistrationView is a UI component that allows users to register by entering their
 * username, password, name, last name, and email. It uses bean validation to validate
 * the input fields and saves the user data using the provided UserService.
 * <p>
 * It implements the NavigationalTools interface to allow navigation to other views.
 * <p>
 * The class is annotated with @Route("registration") to specify the route path for this view.
 * It is also annotated with @AnonymousAllowed to allow access to this view without authentication.
 */
@Route("registration")
@AnonymousAllowed
public class RegistrationView extends VerticalLayout implements NavigationalTools {

    private User user;

    // Create fields
    private TextField usernameField = new TextField("Username");
    private PasswordField passwordField = new PasswordField("Password");
    private TextField firstNameField = new TextField("Name");
    private TextField lastNameField = new TextField("Last name");
    private TextField emailField = new TextField("Email");
    private Button save = new Button("Save");
    private FormLayout formLayout = new FormLayout();

    private final UserService userService;
    private PasswordEncoder passwordEncoder;


    // Create binder
    private BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

    public RegistrationView(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);


        user = new User();
        save.addClickListener(event -> {
            bindFields();
            if (binder.writeBeanIfValid(user)) {
                String hashedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(hashedPassword);

                userService.saveNewUser(user);

                navigateTo(ViewsEnum.LOGIN);
            } else {
                showError();
            }
        });

        // Configure FormLayout
        formLayout.add(usernameField, passwordField, firstNameField, lastNameField, emailField, save);

        add(new H1("User Registration"), formLayout);
    }

    private void showError() {
        // Show error message
    }

    /**
     * Binds form fields to the corresponding properties of the User object.
     * Validates the input values based on the specified validators.
     */
    private void bindFields() {
        Validator<String> usernameValidator = (value, context) -> {
            if (userService.checkUsername(value)) {
                return ValidationResult.error("Username is already in use");
            } else {
                return ValidationResult.ok();
            }
        };

        binder.forField(usernameField)
                .withValidator(new StringLengthValidator(
                        "Username must be at least 2 characters long", 2, null))
                .withValidator(new RegexpValidator(
                        "Username must contain only alphanumeric characters and underscores", "^[\\w]{2,}$"))
                .withValidator(usernameValidator)
                .bind(User::getUsername, User::setUsername);

        binder.forField(passwordField)
                .withValidator(new StringLengthValidator(
                        "Password must be at least 8 characters long", 8, null))
                .withValidator(new RegexpValidator(
                        "Password must contain at least one digit, one lower and one upper case letter", "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"))
                .bind(User::getPassword, User::setPassword);

        binder.forField(firstNameField)
                .withValidator(new StringLengthValidator(
                        "Name cannot be empty", 1, null))
                .bind(User::getFirstName, User::setFirstName);

        binder.forField(lastNameField)
                .withValidator(new StringLengthValidator(
                        "Surname cannot be empty", 1, null))
                .bind(User::getLastName, User::setLastName);

        binder.forField(emailField)
                .withValidator(new EmailValidator("Email format is not valid"))
                .bind(User::getEmail, User::setEmail);
    }
}