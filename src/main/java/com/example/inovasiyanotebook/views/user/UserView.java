package com.example.inovasiyanotebook.views.user;

import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.service.viewservices.user.AllUserGrid;
import com.example.inovasiyanotebook.service.viewservices.user.UserInfoValidation;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("İstifadəçilər")
@Route(value = "users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UserView extends VerticalLayout {
    private final DesignTools designTools;
    private final UserService userService;
    private final NavigationTools navigationTools;
    private final UserInfoValidation userInfoValidation;
    private final AllUserGrid allUserGrid;
    private final User user;

    public UserView(DesignTools designTools, UserService userService, NavigationTools navigationTools, UserInfoValidation userInfoValidation, AllUserGrid allUserGrid) {
        this.designTools = designTools;
        this.userService = userService;
        this.navigationTools = navigationTools;
        this.userInfoValidation = userInfoValidation;
        this.allUserGrid = allUserGrid;
        this.user = userService.findByUsername(navigationTools.getCurrentUsername());


        setHeightFull();
        setWidthFull();

        var usersPageHeaderLine = designTools.getAllCommonViewHeader(user, "İstifadəçilər", this::createNewUser);

        add(usersPageHeaderLine, allUserGrid.getAllUsersLayout(user));
    }

    private void createNewUser () {
        Dialog dialog = new Dialog();

        var desktopComponents = userInfoValidation.getNewComponents(dialog);
        var mobileComponents = userInfoValidation.getNewComponents(dialog);

        designTools.creatDialog(dialog, desktopComponents,mobileComponents);
    }
}
