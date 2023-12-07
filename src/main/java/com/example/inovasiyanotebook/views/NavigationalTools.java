package com.example.inovasiyanotebook.views;

import com.vaadin.flow.component.UI;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * The NavigationalTools interface provides methods for navigating and interacting with the web UI.
 *
 * This interface can be implemented by any class to provide functionality for reloading pages, navigating to specific pages, and getting the current username of the logged in user.
 */
public interface NavigationalTools {

    /**
     * Reloads the current page.
     *
     * This method reloads the current page by accessing the UI instance and invoking the reload method on the page.
     * It should be called within the UI thread to ensure proper synchronization.
     */
    default void reloadPage() {
        UI.getCurrent().access(() -> UI.getCurrent().getPage().reload());
    }

    /**
     * Navigates to the specified page.
     *
     * @param page the page to navigate to
     */
    default void navigateTo(ViewsEnum viewsEnum) {
        UI.getCurrent().access(() -> UI.getCurrent().navigate(viewsEnum.getView()));
    }

    default void navigateTo(ViewsEnum viewsEnum, String parametr) {
        UI.getCurrent().access(() -> UI.getCurrent().navigate(viewsEnum.getView() + "/" + parametr));
    }

    /**
     * Retrieves the currently logged in user's username.
     *
     * @return the username of the currently logged in user, or null if no user is logged in
     */
    default String getCurrentUsername() {
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

