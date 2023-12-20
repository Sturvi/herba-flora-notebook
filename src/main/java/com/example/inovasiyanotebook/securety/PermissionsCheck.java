package com.example.inovasiyanotebook.securety;

import com.example.inovasiyanotebook.model.user.RoleEnum;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class PermissionsCheck {

    private boolean hasRole(RoleEnum roleEnum, EnumSet<RoleEnum> allowedRoles) {
        return allowedRoles.contains(roleEnum);
    }

    public boolean needContributor(RoleEnum roleEnum) {
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.CONTRIBUTOR, RoleEnum.EDITOR, RoleEnum.ADMIN);
        return hasRole(roleEnum, allowedRoles);
    }

    public boolean needEditor(RoleEnum roleEnum) {
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.EDITOR, RoleEnum.ADMIN);
        return hasRole(roleEnum, allowedRoles);
    }

    public boolean needAdmin(RoleEnum roleEnum) {
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.ADMIN);
        return hasRole(roleEnum, allowedRoles);
    }
}
