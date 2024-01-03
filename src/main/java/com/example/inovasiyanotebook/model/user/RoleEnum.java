package com.example.inovasiyanotebook.model.user;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum RoleEnum {
    VIEWER ("İzləyici"),
    CONTRIBUTOR ("İstifadəçi"),
    EDITOR ("Redaktor"),
    ADMIN ("Admin");

    @Getter
    private String roleName;

    RoleEnum(String roleName) {
        this.roleName = roleName;
    }

    public static List<RoleEnum> getAllRoles() {
        return Arrays.asList(RoleEnum.values());
    }
}