package com.example.inovasiyanotebook.views.category;

import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.CategoryService;
import com.example.inovasiyanotebook.service.entityservices.iml.UserService;
import com.example.inovasiyanotebook.service.viewservices.category.CategoryViewService;
import com.example.inovasiyanotebook.views.MainLayout;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.transaction.annotation.Transactional;

@PageTitle("Məhsul növləri")
@Route(value = "category", layout = MainLayout.class)
@PermitAll
public class CategoryView extends VerticalLayout implements HasUrlParameter<String> {
    private final UserService userService;
    private final PermissionsCheck permissionsCheck;
    private final CategoryService categoryService;
    private final CategoryViewService categoryViewService;
    private final NavigationTools navigationTools;

    private User user;
    private Category category;


    public CategoryView(UserService userService, PermissionsCheck permissionsCheck, CategoryService categoryService, CategoryViewService categoryViewService, NavigationTools navigationTools) {
        this.userService = userService;
        this.user = userService.findByUsername(navigationTools.getCurrentUsername());
        this.permissionsCheck = permissionsCheck;
        this.categoryService = categoryService;
        this.categoryViewService = categoryViewService;
        this.navigationTools = navigationTools;
        setHeightFull();
    }

    @Override
    @Transactional
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String categoryId) {
        removeAll();

        if (categoryId == null) {
            allCategoriesPage();
        } else {
            var categoryOpt = categoryService.getById(Long.parseLong(categoryId));
            categoryOpt.ifPresentOrElse(category -> {
                        this.category = category;
                        handleHasCategory();
                    },
                    this::allCategoriesPage);
        }
    }

    private void handleHasCategory() {


    }


    private void allCategoriesPage() {
        add(categoryViewService.getAllCategoryHeader(user), categoryViewService.getAllCategoriesGridLayout(user));
    }
}
