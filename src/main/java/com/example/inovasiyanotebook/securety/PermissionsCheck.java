package com.example.inovasiyanotebook.securety;

import com.example.inovasiyanotebook.model.user.RoleEnum;
import com.example.inovasiyanotebook.model.user.User;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class PermissionsCheck {

    private boolean hasRole(RoleEnum roleEnum, EnumSet<RoleEnum> allowedRoles) {
        return allowedRoles.contains(roleEnum);
    }

    public boolean needContributor(User user) {
        return user.isFunctionalityEnabled() && hasRole(user.getRole(), EnumSet.of(RoleEnum.CONTRIBUTOR, RoleEnum.EDITOR, RoleEnum.ADMIN));
    }

    public boolean needEditor(User user) {
        return user.isFunctionalityEnabled() && hasRole(user.getRole(), EnumSet.of(RoleEnum.EDITOR, RoleEnum.ADMIN));
    }

    public boolean needAdmin(User user) {
        return user.isFunctionalityEnabled() && hasRole(user.getRole(), EnumSet.of(RoleEnum.ADMIN));
    }


    public boolean isContributorOrHigher(User user) {
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.CONTRIBUTOR, RoleEnum.EDITOR, RoleEnum.ADMIN);
        return hasRole(user.getRole(), allowedRoles);
    }

    public boolean isEditorOrHigher(User user) {
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.EDITOR, RoleEnum.ADMIN);
        return hasRole(user.getRole(), allowedRoles);
    }

    public boolean isAdminOrHigher(User user) {
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.ADMIN);
        return hasRole(user.getRole(), allowedRoles);
    }
}
