package com.example.inovasiyanotebook.service.viewservices.user;


import com.example.inovasiyanotebook.model.user.RoleEnum;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UIScope
public class UserInfoValidation {
    private final DesignTools designTools;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final NavigationTools navigationTools;


    public List<Component> getNewComponents (Dialog dialog) {
        return getNewComponents(dialog, null);
    }

    public List<Component> getNewComponentsWithValue (Dialog dialog, User user) {
        return getNewComponents(dialog, user);
    }


    private List<Component> getNewComponents(Dialog dialog, User user) {
        var name = designTools.createTextField(
                "Ad:",
                "^(?=.*\\p{L})[\\p{L}\\s]+$",
                "Ad yalnız hərflərdən ibarət ola bilər");

        var lastName = designTools.createTextField(
                "Soyad:",
                "^(?=.*\\p{L})[\\p{L}\\s]+$",
                "Ad yalnız hərflərdən ibarət ola bilər");

        var username = designTools.createTextField(
                "Username:",
                "^[\\w]{2,}$",
                "Username ən azı 2 simvoldan və yalnız hərif, rəgəm mə \"_\" işarəsindən ibarət ola bilər.");

        var email = designTools.createTextField(
                "Email:",
                "^([a-zA-Z0-9_\\.\\-+])+@([a-zA-Z0-9-]+\\.)+[a-zA-Z0-9-]{2,}$",
                "Email doğru deyil");

        var role = designTools.creatComboBox(
                "İstifadəçi rolu",
                RoleEnum.getAllRoles(),
                RoleEnum::getRoleName
        );

        var password = new PasswordField("Şifrə: ");
        password.setPattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
        password.setErrorMessage("Şifrə ən azı 8 rəqəmdən və ən azı bir böyük hərif, kiçik hərif və rəqəmdən ibarət olmalıdır.");
        password.setWidthFull();

        if (user != null) {
            name.setValue(user.getFirstName());
            lastName.setValue(user.getLastName());
            username.setValue(user.getUsername());
            email.setValue(user.getEmail());
            role.setValue(user.getRole());
        }

        var saveButton = new Button("Əlavə et");
        saveButton.addClickListener(event -> {
            if (validateUserInfo(name, lastName, username, email) && validatePassword(password, user)) {
                User newUser;

                if (user == null) {
                    newUser = User.builder()
                            .firstName(name.getValue())
                            .lastName(lastName.getValue())
                            .email(email.getValue())
                            .username(username.getValue())
                            .password(passwordEncoder.encode(password.getValue()))
                            .role(role.getValue())
                            .build();
                } else {
                    user.setFirstName(name.getValue());
                    user.setLastName(lastName.getValue());
                    user.setEmail(email.getValue());
                    user.setUsername(username.getValue());
                    user.setRole(role.getValue());
                    if (!password.getValue().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(password.getValue()));
                    }

                    newUser = user;
                }

                userService.create(newUser);
                dialog.close();
                navigationTools.reloadPage();
            }
        });

        var cancellButton = new Button("Bağla");
        cancellButton.addClickListener(event -> dialog.close());

        return List.of(name, lastName, email, role, username, password, new HorizontalLayout(saveButton, cancellButton));
    }

    private boolean validateUserInfo(TextField... textFields) {
        boolean isAllCorrect = true;

        for (TextField textField : textFields) {
            if (!textField.getValue().matches(textField.getPattern())) {
                textField.setInvalid(true);
                isAllCorrect = false;
            }
        }

        return isAllCorrect;
    }

    private boolean validatePassword (PasswordField passwordField, User user) {
        return (user == null && passwordField.getValue().matches(passwordField.getPattern())) ||
                (user != null && (passwordField.getValue().isEmpty()) || passwordField.getValue().matches(passwordField.getPattern()));
    }

    private String getPasswordValue (PasswordField passwordField, User user) {
        String password;

        if (user == null) {
            password = passwordEncoder.encode(passwordField.getValue());
        } else {
            if (passwordField.getValue().isEmpty()) {
                password = user.getPassword();
            } else {
                password = passwordEncoder.encode(passwordField.getValue());
            }
        }

        return password;
    }
}
