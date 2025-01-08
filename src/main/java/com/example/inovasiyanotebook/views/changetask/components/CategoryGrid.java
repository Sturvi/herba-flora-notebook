package com.example.inovasiyanotebook.views.changetask.components;

import com.example.inovasiyanotebook.model.client.Category;
import com.example.inovasiyanotebook.service.entityservices.iml.CategoryService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.spring.annotation.UIScope;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@UIScope
public class CategoryGrid {
    private final CategoryService categoryService;

    @Getter
    private TreeGrid<Category> categoriesGrid;
    private List<Category> allCategoriesList;
    @Setter
    private Consumer<Category> selectedCategoryListener;
    @Setter
    private Consumer<Category> deselectedCategoryListener;

    public void selectCategory(Category category) {
        var currentCategory = allCategoriesList.stream().filter(c -> c.getId() == category.getId()).findFirst().orElse(null);

        categoriesGrid.select(currentCategory);

        toggleSubCategories(currentCategory, true);
        selectParentCategoryIfNeed(currentCategory, categoriesGrid.getSelectedItems());
    }

    public void unselectCategory(Category category) {
        var currentCategory = allCategoriesList.stream().filter(c -> c.getId() == category.getId()).findFirst().orElse(null);


        categoriesGrid.deselect(currentCategory);

        toggleSubCategories(currentCategory, false);
        deselectParent(currentCategory);
    }


    @PostConstruct
    public void init() {
        categoriesGrid = new TreeGrid<>();
        categoriesGrid.setClassName("category-grid");
        categoriesGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        List<Category> categoriesList = categoryService.getAllParentCategoriesWithSubCategories();

        allCategoriesList = categoriesList.stream()
                .flatMap(category -> Stream.concat(
                        Stream.of(category), // Основная категория
                        category.getSubCategories() != null
                                ? category.getSubCategories().stream() // Субкатегории
                                : Stream.empty()
                ))
                .toList();

        categoriesGrid.addHierarchyColumn(Category::getName).setHeader("Kateqoriya");

        categoriesGrid.setItems(categoriesList, Category::getSubCategories);

        // Добавляем обработчик для изменения выбора
        categoriesGrid.addSelectionListener(event -> {
            if (event.isFromClient()) {
                var selectedItems = event.getAllSelectedItems();
                var multiSelectionEvent = (MultiSelectionEvent) event;


                multiSelectionEvent.getAddedSelection().stream()
                        .forEach(addedSelection -> {
                            Category currentCategory = (Category) addedSelection;

                            toggleSubCategories(currentCategory, true);
                            selectParentCategoryIfNeed(currentCategory, selectedItems);
                            selectedCategoryListener.accept(currentCategory);
                        });

                multiSelectionEvent.getRemovedSelection().stream()
                        .forEach(removedSelection -> {
                            Category currentCategory = (Category) removedSelection;

                            toggleSubCategories(currentCategory, false);
                            deselectParent(currentCategory);

                            deselectedCategoryListener.accept(currentCategory);
                        });
            }
        });

    }

    private void deselectParent(Category currentCategory) {
        if (currentCategory.hasParent()) {
            categoriesGrid.deselect(currentCategory.getParentCategory());
            deselectParent(currentCategory.getParentCategory());
        }
    }

    private void selectParentCategoryIfNeed(Category currentCategory, Set<Category> selectedItems) {
        try {
            if (currentCategory.hasParent() && isAllSubcategoriesSelected(currentCategory.getParentCategory(), selectedItems)) {
                categoriesGrid.select(currentCategory.getParentCategory());

                // Создаем копию множества для изменения
                Set<Category> updatedSelectedItems = new HashSet<>(selectedItems);
                updatedSelectedItems.add(currentCategory.getParentCategory());

                selectParentCategoryIfNeed(currentCategory.getParentCategory(), updatedSelectedItems);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Категория: " + currentCategory.getName());
        }
    }


    private boolean isAllSubcategoriesSelected(Category parent, Set<Category> selectedItems) {
        List<Category> subCategories = parent.getSubCategories();


        for (Category category : subCategories) {
            if (!selectedItems.contains(category)) {
                return false;
            }
        }


        return true;
    }

    /**
     * Рекурсивный метод для выделения всех подкатегорий.
     */
    private void toggleSubCategories(Category category, boolean select) {
        // Получаем подкатегории
        List<Category> subCategories = category.getSubCategories();

        // Проверяем, есть ли подкатегории
        if (subCategories != null && !subCategories.isEmpty()) {
            subCategories.forEach(subCategory -> {
                // Выбираем или снимаем выбор подкатегории в зависимости от значения select
                if (select) {
                    categoriesGrid.select(subCategory); // Выбираем
                } else {
                    categoriesGrid.deselect(subCategory); // Снимаем выбор
                }

                // Рекурсивно применяем выбор или снятие выбора для дочерних категорий
                toggleSubCategories(subCategory, select);
            });
        }
    }


    public void setVisible(boolean b) {
        categoriesGrid.setVisible(b);
    }
}
