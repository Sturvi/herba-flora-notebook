package com.example.inovasiyanotebook.securety;

import com.example.inovasiyanotebook.model.user.RoleEnum;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.securety.RequiresPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final NavigationTools navigationTools;
    private final UserService userService;

    private static final Map<RoleEnum, EnumSet<RoleEnum>> ROLE_HIERARCHY = Map.of(
            RoleEnum.ADMIN, EnumSet.allOf(RoleEnum.class),
            RoleEnum.EDITOR, EnumSet.of(RoleEnum.EDITOR, RoleEnum.CONTRIBUTOR, RoleEnum.VIEWER),
            RoleEnum.CONTRIBUTOR, EnumSet.of(RoleEnum.CONTRIBUTOR, RoleEnum.VIEWER),
            RoleEnum.VIEWER, EnumSet.of(RoleEnum.VIEWER)
    );

    @Before("@annotation(requiresPermission)")
    public void checkPermission(RequiresPermission requiresPermission) throws PermissionDeniedException {
        Object principal = userService.findByUsername(navigationTools.getCurrentUsername());

        if (principal == null) {
            log.error("Unauthorized access attempt detected");
            throw new PermissionDeniedException("Неавторизованный доступ");
        }

        User user = (User) principal;
        RoleEnum requiredRole = RoleEnum.valueOf(requiresPermission.value());
        RoleEnum userRole = user.getRole();

        if (!ROLE_HIERARCHY.getOrDefault(userRole, EnumSet.noneOf(RoleEnum.class)).contains(requiredRole)) {
            log.error("Access denied for user with role {}", userRole);
            throw new PermissionDeniedException("Доступ запрещен");
        }
    }

}
