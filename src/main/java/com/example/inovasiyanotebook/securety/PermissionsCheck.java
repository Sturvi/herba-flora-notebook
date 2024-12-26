package com.example.inovasiyanotebook.securety;

import com.example.inovasiyanotebook.model.user.RoleEnum;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.repository.UserRepository;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.views.NavigationTools;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class PermissionsCheck {

    private final NavigationTools navigationTools;
    private final UserService userService;

    private boolean hasRole(RoleEnum roleEnum, EnumSet<RoleEnum> allowedRoles) {
        return allowedRoles.contains(roleEnum);
    }

    public boolean needContributor() {
        var user = getCurrentUser();
        return user.isFunctionalityEnabled() && hasRole(user.getRole(), EnumSet.of(RoleEnum.CONTRIBUTOR, RoleEnum.EDITOR, RoleEnum.ADMIN));
    }

    public boolean needEditor() {
        var user = getCurrentUser();
        return user.isFunctionalityEnabled() && hasRole(user.getRole(), EnumSet.of(RoleEnum.EDITOR, RoleEnum.ADMIN));
    }

    public boolean needAdmin() {
        var user = getCurrentUser();
        return user.isFunctionalityEnabled() && hasRole(user.getRole(), EnumSet.of(RoleEnum.ADMIN));
    }


    public boolean isContributorOrHigher() {
        var user = getCurrentUser();
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.CONTRIBUTOR, RoleEnum.EDITOR, RoleEnum.ADMIN);
        return hasRole(user.getRole(), allowedRoles);
    }

    public boolean isEditorOrHigher() {
        var user = getCurrentUser();
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.EDITOR, RoleEnum.ADMIN);
        return hasRole(user.getRole(), allowedRoles);
    }

    public boolean isAdminOrHigher() {
        var user = getCurrentUser();
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.ADMIN);
        return hasRole(user.getRole(), allowedRoles);
    }

    private User getCurrentUser() {
        return userService.findByUsername(navigationTools.getCurrentUsername());
    }
}
