package com.example.inovasiyanotebook.service;

import com.example.inovasiyanotebook.model.user.RoleEnum;

import java.util.EnumSet;

public interface PermissionsCheck {

    private boolean hasRole(RoleEnum roleEnum, EnumSet<RoleEnum> allowedRoles) {
        return allowedRoles.contains(roleEnum);
    }

    default boolean needContributor(RoleEnum roleEnum) {
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.CONTRIBUTOR, RoleEnum.EDITOR, RoleEnum.ADMIN);
        return hasRole(roleEnum, allowedRoles);
    }

    default boolean needEditor(RoleEnum roleEnum) {
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.EDITOR, RoleEnum.ADMIN);
        return hasRole(roleEnum, allowedRoles);
    }

    default boolean needAdmin(RoleEnum roleEnum) {
        EnumSet<RoleEnum> allowedRoles = EnumSet.of(RoleEnum.ADMIN);
        return hasRole(roleEnum, allowedRoles);
    }
}
