package com.example.inovasiyanotebook.service.viewservices.user;

import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@UIScope
@PreAuthorize("hasRole('ADMIN')")
public class AllUserGrid {
    private final UserService userService;
    private final DesignTools designTools;
    private final UserInfoValidation userInfoValidation;
    private final PermissionsCheck permissionsCheck;

    public Component getAllUsersLayout(User user) {
        var UserList = userService.getAll();

        Grid<User> userGrid = new Grid<>();

        userGrid.setHeightFull();
        userGrid.addColumn(User::getFirstName)
                .setHeader("Adı")
                .setSortable(true)
                .setKey("firstName")
                .setFlexGrow(5);

        userGrid.addColumn(User::getLastName)
                .setHeader("Soyadı")
                .setSortable(true)
                .setKey("lastName")
                .setFlexGrow(5);

        userGrid.addColumn(User::getUsername)
                .setHeader("Username")
                .setSortable(true)
                .setKey("username")
                .setFlexGrow(5);

        userGrid.addColumn(User::getEmail)
                .setHeader("Email")
                .setSortable(true)
                .setKey("email")
                .setFlexGrow(5);

        userGrid.addColumn(currentUser -> currentUser.getRole().getRoleName())
                .setHeader("Rol")
                .setSortable(true)
                .setKey("role")
                .setFlexGrow(1);

        if (permissionsCheck.needEditor(user)) {
            userGrid
                    .addComponentColumn(this::getButtonLayout)
                    .setFlexGrow(1);
        }

        userGrid.setItems(UserList);


        return userGrid;
    }

    private Component getButtonLayout(User user) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addClickListener(e -> createEditDialog(user));

/*        Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
        deleteButton.addClickListener(e -> {
            userService.delete(user);
            navigationTools.reloadPage();
        });*/

        horizontalLayout.add(editButton);

        return horizontalLayout;
    }

    private void createEditDialog(User user) {
        Dialog dialog = new Dialog();

        List<Component> desktopComponents = userInfoValidation.getNewComponentsWithValue(dialog, user);
        List<Component> mobileComponents = userInfoValidation.getNewComponentsWithValue(dialog, user);
        designTools.creatDialog(dialog, desktopComponents, mobileComponents);
    }
}


