package com.example.inovasiyanotebook.views;

import com.vaadin.flow.component.UI;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class NavigationTools {


    public void reloadPage() {
        UI.getCurrent().access(() -> UI.getCurrent().getPage().reload());
    }


    public void navigateTo(ViewsEnum viewsEnum) {
        UI.getCurrent().access(() -> UI.getCurrent().navigate(viewsEnum.getView()));
    }

    public void navigateTo(ViewsEnum viewsEnum, String parametr) {
        UI.getCurrent().access(() -> UI.getCurrent().navigate(viewsEnum.getView() + "/" + parametr));
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return null;
        }
    }
}

