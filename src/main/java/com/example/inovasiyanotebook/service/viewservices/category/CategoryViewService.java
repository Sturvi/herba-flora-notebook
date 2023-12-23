package com.example.inovasiyanotebook.service.viewservices.category;

import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.model.client.Client;
import com.example.inovasiyanotebook.model.interfaces.NamedEntity;
import com.example.inovasiyanotebook.model.user.User;
import com.example.inovasiyanotebook.securety.PermissionsCheck;
import com.example.inovasiyanotebook.service.entityservices.iml.CategoryService;
import com.example.inovasiyanotebook.views.DesignTools;
import com.example.inovasiyanotebook.views.NavigationTools;
import com.example.inovasiyanotebook.views.ViewsEnum;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Service
@UIScope
@RequiredArgsConstructor
@Slf4j
public class CategoryViewService {
    private final CategoryService categoryService;
    private final DesignTools designTools;
    private final PermissionsCheck permissionsCheck;
    private final NavigationTools navigationTools;

    private Dialog addCategoryDialog;


    public VerticalLayout getCategoryName(Category category, User user) {
        return new VerticalLayout(designTools.getNameLine(category, user, categoryService, this::updateCategoryName));
    }

    private void updateCategoryName(NamedEntity abstractEntity, TextField titleEditor, Component title) {
        Category category = (Category) abstractEntity;

        String newName = titleEditor.getValue().trim();
        if (!newName.isEmpty()) {
            category.setName(newName);
            categoryService.create(category);
            ((H1) title).setText(newName);
        }
        title.setVisible(true);
        titleEditor.setVisible(false);
    }

    public HorizontalLayout getAllCategoryHeader(User user) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        var headerName = new H1("Bütün kateqoriyalar.");
        horizontalLayout.add(headerName);

        if (permissionsCheck.needEditor(user)) {
            var addButton = designTools.getAddButton(this::addNewCategoryDialog);
            horizontalLayout.add(addButton);
        }

        return horizontalLayout;
    }

    public VerticalLayout getAllCategoriesGridLayout(User user) {
        TreeGrid<Category> categoriesGrid = new TreeGrid<>();
        categoriesGrid.setMaxWidth("700px");
        categoriesGrid.setHeightFull();

        // Хранение начальных данных и текущего состояния поиска
        List<Category> categoriesList = categoryService.getAllParentCategories();
        AtomicReference<String> currentSearchTerm = new AtomicReference<>("");

        // Функция для обновления данных в Grid
        Consumer<String> updateGridItems = getStringConsumer(categoriesGrid, categoriesList);

        categoriesGrid.addHierarchyColumn(Category::getName).setHeader("Kateqoriya");

        if (permissionsCheck.needEditor(user)) {
            // Добавление кнопки удаления
            var deleteColumn = categoriesGrid.addComponentColumn(category -> {
                Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
                deleteButton.setClassName("small-button");
                deleteButton.addClickListener(click -> designTools.showConfirmationDialog(() -> deleteCategory(categoriesList, category, updateGridItems, currentSearchTerm)));

                return deleteButton;
            });

            deleteColumn.setWidth("75px");

            // Отключаем возможность изменения размера колонки
            deleteColumn.setFlexGrow(0);
        }

        categoriesGrid.addItemClickListener(event -> {
            String categoryId = event.getItem().getId().toString();
            navigationTools.navigateTo(ViewsEnum.CATEGORY, categoryId);
        });

        return getVerticalLayout(currentSearchTerm, updateGridItems, categoriesGrid);
    }

    private Consumer<String> getStringConsumer(TreeGrid<Category> categoriesGrid, List<Category> categoriesList) {
        Consumer<String> updateGridItems = searchTerm -> {
            if (searchTerm.isEmpty()) {
                categoriesGrid.setItems(categoriesList, Category::getSubCategories);
            } else {
                List<Category> filteredList = filterCategoriesIncludingSubcategories(categoriesList, searchTerm);
                categoriesGrid.setItems(filteredList, category -> List.of());
            }
        };

        updateGridItems.accept(""); // Инициализация Grid начальными данными
        return updateGridItems;
    }

    private void deleteCategory(List<Category> categoriesList, Category category, Consumer<String> updateGridItems, AtomicReference<String> currentSearchTerm) {
        categoryService.delete(category);
        removeCategoryAndUpdateList(categoriesList, category); // Метод для обновления списка
        updateGridItems.accept(currentSearchTerm.get()); // Обновление Grid
    }

    private VerticalLayout getVerticalLayout(AtomicReference<String> currentSearchTerm, Consumer<String> updateGridItems, TreeGrid<Category> categoriesGrid) {
        TextField searchField = new TextField();
        searchField.setPlaceholder("Axtarış...");
        searchField.setClearButtonVisible(true);
        searchField.setWidthFull();
        searchField.setMaxWidth("700px");

        searchField.addValueChangeListener(e -> {
            String searchTerm = e.getValue().toLowerCase();
            currentSearchTerm.set(searchTerm);
            updateGridItems.accept(searchTerm);
        });

        VerticalLayout layout = new VerticalLayout(searchField, categoriesGrid);
        layout.setHeightFull();
        return layout;
    }


    private void removeCategoryAndUpdateList(List<Category> categories, Category categoryToRemove) {
        categories.removeIf(category -> category.equals(categoryToRemove));
        categories.forEach(category -> {
            List<Category> subCategories = category.getSubCategories();
            subCategories.removeIf(subCategory -> subCategory.equals(categoryToRemove));
        });
    }

    private List<Category> filterCategoriesIncludingSubcategories(List<Category> categories, String searchTerm) {
        List<Category> filteredCategory = new ArrayList<>();

        categories.forEach(category -> filterMatchingCategories(filteredCategory, category, searchTerm));

        return filteredCategory;
    }

    private void filterMatchingCategories(List<Category> filteredCategory, Category category, String searchTerm) {
        // Если категория соответствует условиям поиска, добавляем её в список
        if (category.getName().toLowerCase().contains(searchTerm.toLowerCase().trim())) {
            filteredCategory.add(category);
        }

        // Ищем совпадения в подкатегориях
        for (Category subCategory : category.getSubCategories()) {
            if (subCategory.getName().toLowerCase().contains(searchTerm.toLowerCase().trim())) {
                filteredCategory.add(subCategory);
            }
        }
    }


    private void addNewCategoryDialog() {
        addCategoryDialog = new Dialog();

        List<Category> categories = categoryService.getAllParentCategories();

        // we need 2 identical sheets with different objects of all components.
        // one for the mobile view, the second for the desktop view.
        var componentsList1 = createCommonComponents(categories);
        var componentsList2 = createCommonComponents(categories);


        designTools.creatDialog(addCategoryDialog, componentsList1, componentsList2);
    }

    private List<Component> createCommonComponents(List<Category> categories) {
        ViewComponents components = new ViewComponents();

        components.categoryName = designTools.createTextField("Adı", "^.+$", "Məhsul adı boş ola bilməz.");
        components.categories = designTools.creatComboBox("Məhsul növü:", categories, Category::getName);

        components.addButton = new Button("Əlavə et");
        components.addButton.addClickListener(click -> processNewCategory(components.categoryName, components.categories));

        components.cancelButton = new Button("Ləğv et");
        components.cancelButton.addClickListener(event -> addCategoryDialog.close());

        return components.toList();
    }

    private void processNewCategory(TextField categoryName, ComboBox<Category> categories) {
        if (categoryName.getValue().trim().isEmpty()) {
            categoryName.setInvalid(true);
            return;
        }

        Category category = Category.builder().name(categoryName.getValue().trim()).parentCategory(categories.getValue()).build();

        categoryService.update(category);
        addCategoryDialog.close();
    }

    private static class ViewComponents {
        TextField categoryName;
        ComboBox<Category> categories;
        Button addButton;
        Button cancelButton;

        public List<Component> toList() {
            HorizontalLayout horizontalLayout = new HorizontalLayout(addButton, cancelButton);

            return List.of(categoryName, categories, horizontalLayout);
        }
    }
}
